const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_mechanic_123";
const BOB_UID = "bob_manager_456";
const SUPER_ADMIN_UID = "super_admin_badrou_001";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user RBAC profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read their own RBAC profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@mekanikplus.dz",
      displayName: "Amine Technicien",
      rbacRole: "TECHNICIEN",
      boundHardwareDeviceId: "HW-KEYSTORE-DZ16-9941",
      wilaya: "16 - Alger",
      operatorPinHash: "PIN-SHA256-8841",
      isRemoteWiped: false,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's RBAC profile", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      email: "bob@mekanikplus.dz",
      displayName: "Karim Gestionnaire",
      rbacRole: "GESTIONNAIRE",
      boundHardwareDeviceId: "HW-KEYSTORE-DZ31-2210",
      wilaya: "31 - Oran",
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("AccountSelector: authenticated user can store ATELIER_MAGASIN_MIXTE and ATELIER_CARROSSERIE role in user_account_selections, and cross-user access is denied", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date();

  await assertFails(unauthDb.collection("user_account_selections").doc(ALICE_UID).get());

  await assertSucceeds(
    aliceDb.collection("user_account_selections").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@mekanikplus.dz",
      displayName: "Karim Benali (Gérant Principal)",
      accountRole: "ATELIER_MAGASIN_MIXTE",
      organizationName: "Auto Pièces & Atelier Montage El-Harrach",
      wilaya: "16 - Alger",
      subAccountsCount: 4,
      subAccountsSummary: "1 Principal + 4 Sous-Comptes (Chef Atelier, Mécanicien Monteur, Vendeur Comptoir, Magasinier Stock)",
      subscriptionPriceDzd: 23000,
      mecaVerifFeePerVinDzd: 250,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(aliceDb.collection("user_account_selections").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("user_account_selections").doc(ALICE_UID).get());

  // Update to ATELIER_CARROSSERIE
  await assertSucceeds(
    aliceDb.collection("user_account_selections").doc(ALICE_UID).update({
      accountRole: "ATELIER_CARROSSERIE",
      organizationName: "Atelier Tôlerie & Peinture Pro Alger",
      subAccountsCount: 2,
      subAccountsSummary: "1 Chef Tôlier + 2 Carrossiers/Peintres (Photos Avant/Après obligatoires)",
      subscriptionPriceDzd: 19500,
      mecaVerifFeePerVinDzd: 250,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can append tamper-proof security audit log and cannot delete it", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("security_audit_logs").doc("log_001").set({
      userId: ALICE_UID,
      operatorName: "Amine Technicien",
      rbacRole: "TECHNICIEN",
      shieldCategory: "RBAC_AUDIT",
      actionDescription: "Connexion Technicien DMS autorisée sur appareil HW-KEYSTORE-DZ16-9941",
      hmacSha256Seal: "HMAC-SHA256-9F8E7D6C5B4A3210",
      createdAt: now,
    })
  );

  // Append-only: cannot delete or update audit log
  await assertFails(aliceDb.collection("security_audit_logs").doc("log_001").delete());
});

test("Vendor stock: authenticated seller can create/update own stock and other authenticated users can read it, but cannot modify it", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date();

  await assertSucceeds(
    aliceDb.collection("vendor_stock").doc("stk_pad_01").set({
      userId: ALICE_UID,
      vendorName: "AutoPièces Bab Ezzouar (Alger 16)",
      partName: "Jeu de plaquettes de frein avant",
      oemReference: "5Q0 698 151 BR",
      iamReference: "Brembo P 85 126",
      quantityInStock: 18,
      priceDzd: 8500,
      availabilityStatus: "EN_STOCK",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Another signed-in user (Bob) can read the real-time stock listing
  await assertSucceeds(bobDb.collection("vendor_stock").doc("stk_pad_01").get());

  // Bob cannot modify Alice's stock listing
  await assertFails(
    bobDb.collection("vendor_stock").doc("stk_pad_01").update({
      quantityInStock: 0,
      updatedAt: now,
    })
  );
});

test("System Error Logger: unauthenticated user cannot read or write system_error_logs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("system_error_logs").get());
});

test("System Error Logger: authenticated user can log crash/API/persistence error, cannot read another user's error, while SuperAdmin can read and resolve all errors", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const superAdminDb = testEnv
    .authenticatedContext(SUPER_ADMIN_UID, {
      email: "badroumc@gmail.com",
      email_verified: true,
    })
    .firestore();
  const now = new Date();

  await assertSucceeds(
    aliceDb.collection("system_error_logs").doc("err_crash_001").set({
      userId: ALICE_UID,
      userEmail: "alice@mekanikplus.dz",
      rbacRole: "TECHNICIEN",
      errorCategory: "CRASH_RUNTIME",
      severity: "CRITICAL",
      moduleSource: "MainActivity / Compose UI",
      errorCode: "UNCAUGHT_RUNTIME_EXCEPTION",
      errorTitle: "Plantage intercepté par UncaughtExceptionHandler",
      technicalMessage: "Exception critique interceptée avant fermeture brutale de l'application.",
      stackTracePreview: "java.lang.IllegalStateException: State sync error at com.example.MainActivity.kt:142",
      deviceHardwareInfo: "Android 16 (API 36) • HW-KEYSTORE-DZ16-9941 • Alger 16",
      resolutionStatus: "OPEN",
      superAdminNote: "",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Alice can query her own error logs
  await assertSucceeds(
    aliceDb.collection("system_error_logs").where("userId", "==", ALICE_UID).get()
  );

  // Bob (regular non-SuperAdmin user) cannot read Alice's error log
  await assertFails(
    bobDb.collection("system_error_logs").doc("err_crash_001").get()
  );

  // SuperAdmin (badroumc@gmail.com) can list ALL error logs and resolve them
  await assertSucceeds(superAdminDb.collection("system_error_logs").get());
  await assertSucceeds(
    superAdminDb.collection("system_error_logs").doc("err_crash_001").update({
      resolutionStatus: "RESOLVED",
      superAdminNote: "Corrigé via patch hotfix SuperAdmin v2.6.4",
      updatedAt: new Date(),
    })
  );
});
