import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Navbar } from "./components/layout/Navbar";
import { ProtectedRoute } from "./components/layout/ProtectedRoute";
import { AuthProvider } from "./auth/AuthContext";
import { ToastProvider } from "./components/ui/Toast";
import Home from "./pages/study-buddy/Home";
import Login from "./pages/study-buddy/Login";
import Cadastro from "./pages/study-buddy/Cadastro";
import Profile from "./pages/study-buddy/Profile";
import PerfilAcademicoForm from "./pages/study-buddy/PerfilAcademicoForm";
import Grupos from "./pages/study-buddy/Grupos";
import GrupoForm from "./pages/study-buddy/GrupoForm";
import GrupoDetalhe from "./pages/study-buddy/GrupoDetalhe";
import Interesses from "./pages/study-buddy/Interesses";
import InteressesRecebidos from "./pages/study-buddy/InteressesRecebidos";
import Matchmaking from "./pages/study-buddy/Matching";
import Moderacao from "./pages/study-buddy/Moderacao";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <div className="min-h-screen bg-apto-bg">
            <Navbar />
            <main className="pt-16 md:pt-20">
              <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/cadastro" element={<Cadastro />} />

                <Route
                  path="/"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <Home />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/perfil"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <Profile />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/perfil/academico"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <PerfilAcademicoForm />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/grupos"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <Grupos />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/grupos/novo"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <GrupoForm />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/grupos/:id"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <GrupoDetalhe />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/grupos/:id/interesses"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <InteressesRecebidos />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/interesses"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <Interesses />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/interesses-recebidos"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <InteressesRecebidos />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/matchmaking"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <Matchmaking />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/moderacao"
                  element={
                    <ProtectedRoute permitirTipos={["ESTUDANTE"]}>
                      <Moderacao />
                    </ProtectedRoute>
                  }
                />
              </Routes>
            </main>
          </div>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
