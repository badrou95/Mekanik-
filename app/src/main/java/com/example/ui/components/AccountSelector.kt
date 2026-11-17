package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.model.AccountRole
import com.example.data.model.HybridSubAccountMember
import com.example.data.model.defaultHybridWorkshopStoreSubAccounts
import com.example.data.repository.SecurityShieldFirestoreRepository
import com.example.ui.theme.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AccountSelectorPrefs {
  private const val PREFS_NAME = "dms_mecanicauto_account_selector_prefs"
  private const val KEY_HAS_SELECTED_ROLE_FIRST_LOGIN = "has_selected_role_first_login_v2"
  private const val KEY_SAVED_ACCOUNT_ROLE = "saved_account_role_v2"
  private const val KEY_SAVED_ORG_NAME = "saved_org_name_v2"
  private const val KEY_SAVED_SUB_ACCOUNTS_COUNT = "saved_sub_accounts_count_v2"

  fun shouldShowAccountSelectorOnFirstLogin(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return !prefs.getBoolean(KEY_HAS_SELECTED_ROLE_FIRST_LOGIN, false)
  }

  fun getSavedAccountRole(context: Context): AccountRole? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    if (!prefs.getBoolean(KEY_HAS_SELECTED_ROLE_FIRST_LOGIN, false)) return null
    val raw = prefs.getString(KEY_SAVED_ACCOUNT_ROLE, null) ?: return null
    return AccountRole.fromId(raw)
  }

  fun saveAccountSelectionLocally(
    context: Context,
    role: AccountRole,
    organizationName: String,
    subAccountsCount: Int
  ) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit()
      .putBoolean(KEY_HAS_SELECTED_ROLE_FIRST_LOGIN, true)
      .putString(KEY_SAVED_ACCOUNT_ROLE, role.name)
      .putString(KEY_SAVED_ORG_NAME, organizationName)
      .putInt(KEY_SAVED_SUB_ACCOUNTS_COUNT, subAccountsCount)
      .apply()
  }
}

fun attemptSilentGoogleSignInForAccountSelector(
  context: Context,
  credentialManager: CredentialManager,
  onAuthSuccess: () -> Unit,
  onUnauthenticated: () -> Unit,
  scope: CoroutineScope
) {
  if (Firebase.auth.currentUser != null) {
    onAuthSuccess()
    return
  }
  val clientId = try {
    context.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    onUnauthenticated()
    return
  }

  val googleIdOption = GetGoogleIdOption.Builder()
    .setFilterByAuthorizedAccounts(true)
    .setServerClientId(clientId)
    .setAutoSelectEnabled(true)
    .build()

  val request = GetCredentialRequest.Builder()
    .addCredentialOption(googleIdOption)
    .build()

  scope.launch {
    try {
      val result = credentialManager.getCredential(context, request)
      val credential = result.credential
      if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        Firebase.auth.signInWithCredential(authCredential).await()
        onAuthSuccess()
      } else {
        onUnauthenticated()
      }
    } catch (e: Exception) {
      onUnauthenticated()
    }
  }
}

fun onInteractiveGoogleSignInClicked(
  context: Context,
  credentialManager: CredentialManager,
  onAuthSuccess: () -> Unit,
  onAuthError: (String) -> Unit,
  scope: CoroutineScope,
  onAuthCancelled: () -> Unit = {}
) {
  val clientId = try {
    context.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    onAuthError("Configuration Google Sign-In absente (default_web_client_id introuvable)")
    return
  }

  val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
  val request = GetCredentialRequest.Builder()
    .addCredentialOption(signInOption)
    .build()

  scope.launch {
    try {
      val activityContext = context as? Activity ?: context
      val result = credentialManager.getCredential(activityContext, request)
      val credential = result.credential
      if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        Firebase.auth.signInWithCredential(authCredential).await()
        onAuthSuccess()
      } else {
        onAuthError("Type d'identifiant Google inattendu")
      }
    } catch (e: GetCredentialCancellationException) {
      Log.w("Auth", "Google Sign-In cancelled or dismissed: ${e.message}", e)
      onAuthCancelled()
    } catch (e: Exception) {
      Log.e("Auth", "Google Sign-In failed", e)
      onAuthError(e.localizedMessage ?: "Échec de la connexion Google")
    }
  }
}

fun signOutGoogleAccountSelector(
  credentialManager: CredentialManager,
  scope: CoroutineScope,
  onSignOutComplete: () -> Unit = {}
) {
  scope.launch {
    withContext(NonCancellable) {
      try {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
      } catch (e: Exception) {
        Log.w("Auth", "Failed to clear credential state: ${e.message}")
      } finally {
        Firebase.auth.signOut()
        onSignOutComplete()
      }
    }
  }
}

/**
 * Composant `AccountSelector` :
 * Permet à l'utilisateur ou au professionnel de choisir sa formule de compte lors de la première connexion
 * ou à tout moment :
 * 1. Particulier (`CONDUCTEUR`)
 * 2. Atelier Mécanique (`GARAGE_PRO`)
 * 3. Vendeur / Magasin de Pièces (`VENDEUR_PIECES`)
 * 4. Compte Mixte : Atelier Mécanique + Magasin de Pièces (`ATELIER_MAGASIN_MIXTE` : 1 Principal + 3 ou 4 Sous-Comptes, avec toutes les options des deux)
 * 5. Compte Spécial Carrosserie & Tôlerie (`ATELIER_CARROSSERIE` : accès VIN pièces tôlerie/plastique + photos Avant/Après obligatoires)
 * 6. Entreprise & Flotte (`LOCATION_VOITURES`)
 *
 * Stocke ce choix dans Cloud Firestore (`/user_account_selections/{userId}`) et filtre dynamiquement toute l'interface.
 */
@Composable
fun AccountSelector(
  currentRole: AccountRole,
  isFirstLoginModal: Boolean = false,
  onRoleConfirmed: (AccountRole, String, Int) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember(context) { CredentialManager.create(context) }

  var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
  var isAuthLoading by remember { mutableStateOf(false) }
  var authStatusMessage by remember { mutableStateOf<String?>(null) }
  var isSavingToFirestore by remember { mutableStateOf(false) }
  var firestoreSyncBadge by remember {
    mutableStateOf(
      if (Firebase.auth.currentUser != null) "CONNECTÉ À FIRESTORE CLOUD"
      else "SYNCHRONISATION FIRESTORE PRÊTE"
    )
  }

  var selectedRole by remember(currentRole) { mutableStateOf(currentRole) }
  var organizationName by remember(selectedRole) {
    mutableStateOf(
      when (selectedRole) {
        AccountRole.ATELIER_MAGASIN_MIXTE -> "Atelier Montage & Pièces Auto El-Harrach"
        AccountRole.ATELIER_CARROSSERIE -> "Carrosserie & Tôlerie Moderne Alger"
        AccountRole.GARAGE_PRO -> "Atelier Mécanique Pro DMS Mécanicauto+"
        AccountRole.VENDEUR_PIECES -> "Magasin Pièces de Rechange Bab Ezzouar"
        AccountRole.LOCATION_VOITURES -> "Entreprise & Flotte Auto Algérie"
        AccountRole.CONDUCTEUR -> "Mon Garage Personnel"
      }
    )
  }
  var selectedWilaya by remember { mutableStateOf("16 - Alger") }

  // Configuration spécifique au Compte Mixte Atelier + Magasin (1 Principal + 3 ou 4 Sous-Comptes)
  var hybridSubAccountsCount by remember { mutableIntStateOf(4) } // 3 ou 4 sous-comptes
  var hybridSubAccounts by remember { mutableStateOf(defaultHybridWorkshopStoreSubAccounts) }

  DisposableEffect(Unit) {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      currentUser = auth.currentUser
    }
    Firebase.auth.addAuthStateListener(listener)
    onDispose {
      Firebase.auth.removeAuthStateListener(listener)
    }
  }

  // Observation temps réel du choix stocké dans Firestore lorsque l'utilisateur est authentifié
  LaunchedEffect(currentUser?.uid) {
    val uid = currentUser?.uid ?: return@LaunchedEffect
    try {
      val repo = SecurityShieldFirestoreRepository(context)
      val existing = repo.getUserAccountSelection(uid).getOrNull()
      if (existing != null) {
        selectedRole = existing.parsedAccountRole
        if (existing.organizationName.isNotBlank()) {
          organizationName = existing.organizationName
        }
        if (existing.subAccountsCount in 3..4) {
          hybridSubAccountsCount = existing.subAccountsCount
        }
        firestoreSyncBadge = "SYNCHRONISÉ FIRESTORE (${existing.parsedAccountRole.shortTitle})"
      }
    } catch (e: Exception) {
      Log.w("AccountSelector", "Lecture initiale Firestore ignorée: ${e.message}")
    }
  }

  Dialog(
    onDismissRequest = {
      if (!isFirstLoginModal) onDismiss()
    },
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = !isFirstLoginModal,
      dismissOnClickOutside = !isFirstLoginModal
    )
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MekanikBrandOrange),
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 10.dp, vertical = 14.dp)
        .testTag("account_selector_modal")
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // En-tête DMS Mécanicauto+ AccountSelector
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MekanikBrandOrange.copy(alpha = 0.18f),
                  border = BorderStroke(1.dp, MekanikBrandOrange),
                  modifier = Modifier.size(44.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.ManageAccounts,
                      contentDescription = null,
                      tint = MekanikBrandOrange,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = "ACCOUNT SELECTOR • DMS MÉCANICAUTO+",
                      color = OffWhite,
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 14.5.sp
                    )
                  }
                  Text(
                    text = if (isFirstLoginModal) {
                      "Première connexion : Choisissez votre formule de compte pour filtrer l'interface"
                    } else {
                      "Changement de formule : Tout l'espace précédent sera purgé et adapté à votre compte"
                    },
                    color = AluminumGray,
                    fontSize = 11.sp
                  )
                }
              }

              if (!isFirstLoginModal) {
                IconButton(
                  onClick = onDismiss,
                  modifier = Modifier.testTag("btn_close_account_selector")
                ) {
                  Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                }
              }
            }

            // Barre Google Sign-In + Statut Cloud Firestore
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = GraphiteDark,
              border = BorderStroke(
                1.dp,
                if (currentUser != null) MekanikValidationGreen.copy(alpha = 0.7f) else MetalBorder
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(
                    imageVector = if (currentUser != null) Icons.Default.CloudDone else Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = if (currentUser != null) MekanikValidationGreen else MekanikBrandOrange,
                    modifier = Modifier.size(18.dp)
                  )
                  Column {
                    Text(
                      text = if (currentUser != null) {
                        "Connecté : ${currentUser?.email ?: currentUser?.displayName ?: "Compte Google"}"
                      } else {
                        "Synchronisation Cloud Firestore (Google Sign-In)"
                      },
                      color = OffWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      text = authStatusMessage ?: firestoreSyncBadge,
                      color = if (currentUser != null) MekanikValidationGreen else AluminumGray,
                      fontSize = 9.5.sp
                    )
                  }
                }

                if (currentUser == null) {
                  Button(
                    onClick = {
                      isAuthLoading = true
                      authStatusMessage = "Ouverture Google Sign-In..."
                      onInteractiveGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                          isAuthLoading = false
                          currentUser = Firebase.auth.currentUser
                          authStatusMessage = "Connecté à Firestore Cloud"
                        },
                        onAuthError = { err ->
                          isAuthLoading = false
                          authStatusMessage = err
                        },
                        scope = coroutineScope,
                        onAuthCancelled = {
                          isAuthLoading = false
                          authStatusMessage = "Connexion Google annulée (sauvegarde locale active)"
                        }
                      )
                    },
                    enabled = !isAuthLoading,
                    shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MekanikBrandOrange,
                      contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_account_selector_google_signin")
                  ) {
                    if (isAuthLoading) {
                      CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                      )
                    } else {
                      Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                      )
                      Spacer(modifier = Modifier.width(5.dp))
                      Text(
                        text = "Sign in with Google",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                      )
                    }
                  }
                } else {
                  OutlinedButton(
                    onClick = {
                      signOutGoogleAccountSelector(
                        credentialManager = credentialManager,
                        scope = coroutineScope,
                        onSignOutComplete = {
                          currentUser = null
                          authStatusMessage = "Déconnecté de Google"
                        }
                      )
                    },
                    shape = RoundedCornerShape(9.dp),
                    border = BorderStroke(1.dp, MetalBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text("Déconnexion", color = AluminumGray, fontSize = 10.sp)
                  }
                }
              }
            }
          }
        }

        // Liste des 6 Formules de Comptes + Configurateur Sous-Comptes Mixte & Règle Externe MécaVerif+
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Bannière d'avertissement : MécaVerif+™ (VerifyCar) est un service EXTERNE non inclus dans les abonnements
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF1E293B),
              border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.75f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.VerifiedUser,
                  contentDescription = null,
                  tint = MekanikBrandOrange,
                  modifier = Modifier.size(22.dp)
                )
                Column {
                  Text(
                    text = "MÉCAVERIF+™ (VERIFYCAR) : SERVICE EXTERNE HORS ABONNEMENT",
                    color = MekanikBrandOrange,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                  )
                  Text(
                    text = "MécaVerif+™ n'est inclus dans aucun abonnement. Chaque consultation d'historique véhicule (courbe kilométrique, photos dommages tôle avant/après carrosserie, passages mécanicien) coûte 250 DA (Pro) ou 300 DA (Particulier) par numéro de châssis (VIN).",
                    color = OffWhite,
                    fontSize = 10.5.sp,
                    lineHeight = 14.5.sp
                  )
                }
              }
            }
          }

          // Cartes des 6 rôles de compte
          items(AccountRole.entries) { role ->
            val isSelected = selectedRole == role
            val roleAccentColor = when (role) {
              AccountRole.ATELIER_MAGASIN_MIXTE -> Color(0xFFF07836)
              AccountRole.ATELIER_CARROSSERIE -> Color(0xFF38BDF8)
              AccountRole.GARAGE_PRO -> Color(0xFFF07836)
              AccountRole.VENDEUR_PIECES -> Color(0xFF22C55E)
              AccountRole.CONDUCTEUR -> Color(0xFFA855F7)
              AccountRole.LOCATION_VOITURES -> Color(0xFFEAB308)
            }

            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (isSelected) roleAccentColor.copy(alpha = 0.13f) else MetalCard,
              border = BorderStroke(
                if (isSelected) 2.dp else 1.dp,
                if (isSelected) roleAccentColor else MetalBorder
              ),
              onClick = { selectedRole = role },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("account_selector_card_${role.name}")
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = roleAccentColor.copy(alpha = 0.18f),
                      border = BorderStroke(1.dp, roleAccentColor),
                      modifier = Modifier.size(44.dp)
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Icon(
                          imageVector = accountRoleVectorIcon(role),
                          contentDescription = null,
                          tint = roleAccentColor,
                          modifier = Modifier.size(24.dp)
                        )
                      }
                    }

                    Column {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Surface(
                          shape = RoundedCornerShape(5.dp),
                          color = roleAccentColor.copy(alpha = 0.22f)
                        ) {
                          Text(
                            text = role.badgeLabel,
                            color = roleAccentColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                        if (role == AccountRole.ATELIER_MAGASIN_MIXTE) {
                          Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = MekanikValidationGreen.copy(alpha = 0.2f)
                          ) {
                            Text(
                              text = "1 PRINCIPAL + 3/4 SOUS-COMPTES",
                              color = MekanikValidationGreen,
                              fontWeight = FontWeight.ExtraBold,
                              fontSize = 8.5.sp,
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                          }
                        }
                        if (role == AccountRole.ATELIER_CARROSSERIE) {
                          Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.2f)
                          ) {
                            Text(
                              text = "PHOTOS AVANT/APRÈS OBLIGATOIRES",
                              color = Color(0xFFEF4444),
                              fontWeight = FontWeight.ExtraBold,
                              fontSize = 8.5.sp,
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                          }
                        }
                      }
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = role.label,
                        color = OffWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                      )
                    }
                  }

                  // Prix Abonnement DZD + Tarif externe MécaVerif+
                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = role.subscriptionPriceLabel,
                      color = MekanikValidationGreen,
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 13.5.sp
                    )
                    Text(
                      text = "MécaVerif+ : ${role.mecaVerifExternalRateDzd} DA/VIN",
                      color = MekanikBrandOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp
                    )
                  }
                }

                Text(
                  text = role.description,
                  color = AluminumGray,
                  fontSize = 11.sp,
                  lineHeight = 15.sp
                )

                // Résumé des options incluses pour ce compte
                AnimatedVisibility(visible = isSelected) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = GraphiteDark,
                      border = BorderStroke(1.dp, roleAccentColor.copy(alpha = 0.5f)),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        Text(
                          text = "OPTIONS AFFICHÉES POUR CE COMPTE UNIQUEMENT :",
                          color = roleAccentColor,
                          fontWeight = FontWeight.ExtraBold,
                          fontSize = 10.sp
                        )
                        role.allowedFeaturesSummary.forEach { feat ->
                          Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                          ) {
                            Icon(
                              imageVector = Icons.Default.CheckCircle,
                              contentDescription = null,
                              tint = MekanikValidationGreen,
                              modifier = Modifier.size(14.dp).padding(top = 2.dp)
                            )
                            Text(
                              text = feat,
                              color = OffWhite,
                              fontSize = 10.5.sp,
                              lineHeight = 14.sp
                            )
                          }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                          text = "Isolation : ${role.excludedFeaturesSummary}",
                          color = AluminumGray,
                          fontSize = 10.sp
                        )
                      }
                    }

                    // CONFIGURATEUR DÉDIÉ AU COMPTE MIXTE (ATELIER + MAGASIN DE PIÈCES : 1 PRINCIPAL + 3 OU 4 SOUS-COMPTES)
                    if (role == AccountRole.ATELIER_MAGASIN_MIXTE) {
                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.2.dp, MekanikBrandOrange),
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("hybrid_sub_accounts_configurator")
                      ) {
                        Column(
                          modifier = Modifier.padding(12.dp),
                          verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                          Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Column(modifier = Modifier.weight(1f)) {
                              Text(
                                text = "CONFIGURATION COMPTE MIXTE (ATELIER + MAGASIN)",
                                color = MekanikBrandOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp
                              )
                              Text(
                                text = "1 Compte Principal (Gérant) + $hybridSubAccountsCount Sous-Comptes actifs",
                                color = OffWhite,
                                fontSize = 10.5.sp
                              )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                              listOf(3, 4).forEach { countOption ->
                                val isCountSelected = hybridSubAccountsCount == countOption
                                Surface(
                                  shape = RoundedCornerShape(8.dp),
                                  color = if (isCountSelected) MekanikBrandOrange else MetalCard,
                                  border = BorderStroke(
                                    1.dp,
                                    if (isCountSelected) MekanikBrandOrange else MetalBorder
                                  ),
                                  onClick = { hybridSubAccountsCount = countOption }
                                ) {
                                  Text(
                                    text = "+$countOption Sous-Comptes",
                                    color = if (isCountSelected) Color.White else OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                  )
                                }
                              }
                            }
                          }

                          // Affichage du Compte Principal + des 3 ou 4 Sous-Comptes
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MetalCard,
                            border = BorderStroke(1.dp, MekanikValidationGreen.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                          ) {
                            Row(
                              modifier = Modifier.padding(9.dp),
                              horizontalArrangement = Arrangement.SpaceBetween,
                              verticalAlignment = Alignment.CenterVertically
                            ) {
                              Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                              ) {
                                Icon(
                                  imageVector = Icons.Default.WorkspacePremium,
                                  contentDescription = null,
                                  tint = MekanikValidationGreen,
                                  modifier = Modifier.size(18.dp)
                                )
                                Column {
                                  Text(
                                    text = "COMPTE PRINCIPAL • Gérant Atelier & Magasin",
                                    color = OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                  )
                                  Text(
                                    text = "Accès intégral : Devis Montage + Vente Comptoir + Stock + Caisse",
                                    color = AluminumGray,
                                    fontSize = 9.5.sp
                                  )
                                }
                              }
                              Text(
                                text = "MAÎTRE",
                                color = MekanikValidationGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 9.5.sp
                              )
                            }
                          }

                          hybridSubAccounts.take(hybridSubAccountsCount).forEach { sub ->
                            val isAtelierSide = sub.departmentBadge.contains("ATELIER")
                            val badgeColor = if (isAtelierSide) MekanikBrandOrange else Color(0xFF38BDF8)
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = MetalCard,
                              border = BorderStroke(1.dp, MetalBorder),
                              modifier = Modifier.fillMaxWidth()
                            ) {
                              Row(
                                modifier = Modifier.padding(9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                Column(modifier = Modifier.weight(1f)) {
                                  Text(
                                    text = "Sous-Compte #${sub.slotNumber} • ${sub.fullName}",
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                  )
                                  Text(
                                    text = "${sub.roleTitle} (PIN : ${sub.pinCode})",
                                    color = AluminumGray,
                                    fontSize = 9.5.sp
                                  )
                                }
                                Surface(
                                  shape = RoundedCornerShape(6.dp),
                                  color = badgeColor.copy(alpha = 0.16f),
                                  border = BorderStroke(1.dp, badgeColor)
                                ) {
                                  Text(
                                    text = sub.departmentBadge,
                                    color = badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                  )
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // Champ Nom de la structure (Atelier / Magasin / Carrosserie / Entreprise / Particulier)
          item {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MetalCard,
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = "NOM DU TITULAIRE / ATELIER / MAGASIN / CARROSSERIE :",
                  color = AluminumGray,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
                OutlinedTextField(
                  value = organizationName,
                  onValueChange = { organizationName = it },
                  singleLine = true,
                  label = { Text("Nom affiché sur votre espace et vos documents", fontSize = 11.sp) },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Business,
                      contentDescription = null,
                      tint = MekanikBrandOrange
                    )
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MekanikBrandOrange,
                    unfocusedBorderColor = MetalBorder,
                    focusedTextColor = OffWhite,
                    unfocusedTextColor = OffWhite,
                    focusedContainerColor = GraphiteDark,
                    unfocusedContainerColor = GraphiteDark
                  ),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_selector_org_input")
                )
              }
            }
          }
        }

        // Pied de page : Bouton de validation qui enregistre dans Firestore et filtre l'interface
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val effectiveSubCount = if (selectedRole == AccountRole.ATELIER_MAGASIN_MIXTE) {
              hybridSubAccountsCount
            } else {
              selectedRole.defaultSubAccountsCount
            }

            Button(
              onClick = {
                isSavingToFirestore = true
                AccountSelectorPrefs.saveAccountSelectionLocally(
                  context = context,
                  role = selectedRole,
                  organizationName = organizationName,
                  subAccountsCount = effectiveSubCount
                )

                coroutineScope.launch {
                  if (Firebase.auth.currentUser != null) {
                    try {
                      val repo = SecurityShieldFirestoreRepository(context)
                      repo.upsertUserAccountSelection(
                        email = Firebase.auth.currentUser?.email ?: "client@mekanikplus.dz",
                        displayName = Firebase.auth.currentUser?.displayName ?: organizationName,
                        accountRole = selectedRole,
                        organizationName = organizationName,
                        wilaya = selectedWilaya,
                        subAccountsCount = effectiveSubCount
                      )
                    } catch (e: Exception) {
                      Log.w("AccountSelector", "Firestore sync warning: ${e.message}")
                    }
                  }
                  isSavingToFirestore = false
                  onRoleConfirmed(selectedRole, organizationName, effectiveSubCount)
                }
              },
              enabled = !isSavingToFirestore,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MekanikBrandOrange,
                contentColor = Color.White
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_confirm_account_selector")
            ) {
              if (isSavingToFirestore) {
                CircularProgressIndicator(
                  modifier = Modifier.size(20.dp),
                  color = Color.White,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
              } else {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
              }
              Text(
                text = "Activer ${selectedRole.shortTitle} (${selectedRole.subscriptionPriceLabel}) & Filtrer l'Interface",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp
              )
            }
          }
        }
      }
    }
  }
}

fun accountRoleVectorIcon(role: AccountRole): ImageVector {
  return when (role) {
    AccountRole.CONDUCTEUR -> Icons.Default.DirectionsCar
    AccountRole.GARAGE_PRO -> Icons.Default.Handyman
    AccountRole.VENDEUR_PIECES -> Icons.Default.Storefront
    AccountRole.ATELIER_MAGASIN_MIXTE -> Icons.Default.Hub
    AccountRole.ATELIER_CARROSSERIE -> Icons.Default.CarRepair
    AccountRole.LOCATION_VOITURES -> Icons.Default.AirportShuttle
  }
}
