import React from "react";
import ReactDOM from "react-dom/client";
import { App } from "./App";

class AppErrorBoundary extends React.Component<
  { children: React.ReactNode },
  { hasError: boolean; errorMsg: string }
> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false, errorMsg: "" };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, errorMsg: error?.message || "Erreur d'affichage" };
  }

  render() {
    if (this.state.hasError) {
      return (
        <div
          style={{
            minHeight: "100vh",
            backgroundColor: "#101820",
            color: "#E8EDF2",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            padding: 24,
            textAlign: "center"
          }}
        >
          <h1 style={{ color: "#F07836", marginBottom: 8 }}>MEKANIK+ — Récupération</h1>
          <p style={{ color: "#94A3B8", maxWidth: 480, fontSize: 13 }}>
            Une ancienne donnée locale a provoqué une interruption ({this.state.errorMsg}).
          </p>
          <button
            onClick={() => {
              try {
                localStorage.clear();
              } catch (_e) {}
              window.location.reload();
            }}
            style={{
              marginTop: 12,
              padding: "12px 20px",
              borderRadius: 10,
              border: "none",
              backgroundColor: "#F07836",
              color: "#FFF",
              fontWeight: 800,
              cursor: "pointer"
            }}
          >
            Recharger la plateforme MEKANIK+
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <AppErrorBoundary>
      <App />
    </AppErrorBoundary>
  </React.StrictMode>
);

