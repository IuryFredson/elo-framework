import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Navbar } from "./components/layout/Navbar";
import { ProtectedRoute } from "./components/layout/ProtectedRoute";
import { AuthProvider } from "./auth/AuthContext";
import { ToastProvider } from "./components/ui/Toast";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Cadastro from "./pages/Cadastro";
import Profile from "./pages/Profile";
import PerfilConvivenciaForm from "./pages/PerfilConvivenciaForm";
import Buscar from "./pages/Buscar";
import AnuncioForm from "./pages/AnuncioForm";
import MeusAnuncios from "./pages/MeusAnuncios";
import AnuncioDetalhe from "./pages/AnuncioDetalhe";
import Interesses from "./pages/Interesses";
import InteressesRecebidos from "./pages/InteressesRecebidos";
import InteressesPorAnuncio from "./pages/InteressesPorAnuncio";
import Matchmaking from "./pages/Matchmaking";
import LocadorPublico from "./pages/LocadorPublico";
import Moderacao from "./pages/Moderacao";

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
                    <ProtectedRoute>
                      <Home />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/profile"
                  element={
                    <ProtectedRoute>
                      <Profile />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/profile/convivencia"
                  element={
                    <ProtectedRoute permitirTipos={["UNIVERSITARIO"]}>
                      <PerfilConvivenciaForm />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/buscar"
                  element={
                    <ProtectedRoute>
                      <Buscar />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/anuncios/novo"
                  element={
                    <ProtectedRoute>
                      <AnuncioForm />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/meus-anuncios"
                  element={
                    <ProtectedRoute permitirTipos={["LOCADOR"]}>
                      <MeusAnuncios />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/anuncios/:id"
                  element={
                    <ProtectedRoute>
                      <AnuncioDetalhe />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/anuncios/:id/interesses"
                  element={
                    <ProtectedRoute>
                      <InteressesPorAnuncio />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/interesses"
                  element={
                    <ProtectedRoute permitirTipos={["UNIVERSITARIO"]}>
                      <Interesses />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/interesses-recebidos"
                  element={
                    <ProtectedRoute permitirTipos={["LOCADOR"]}>
                      <InteressesRecebidos />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/matchmaking"
                  element={
                    <ProtectedRoute permitirTipos={["UNIVERSITARIO"]}>
                      <Matchmaking />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/locadores/:id"
                  element={
                    <ProtectedRoute>
                      <LocadorPublico />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/moderacao"
                  element={
                    <ProtectedRoute>
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
