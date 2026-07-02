import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Users } from "lucide-react";
import { Button } from "../../components/ui/Button";
import { Input } from "../../components/ui/Input";
import { estudantesApi } from "../../api/studyBuddy";
import { useAuth } from "../../auth/useAuth";
import { ApiError } from "../../api/client";

export default function Cadastro() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [telefone, setTelefone] = useState("");
  const [matricula, setMatricula] = useState("");
  const [instituicao, setInstituicao] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [salvando, setSalvando] = useState(false);

  async function submeter(event: React.FormEvent) {
    event.preventDefault();
    setErro(null);
    setSalvando(true);

    try {
      const estudante = await estudantesApi.criar({
        nome,
        email,
        telefone,
        matricula,
        instituicao,
      });
      login({ id: estudante.id, tipo: "ESTUDANTE", nome: estudante.nome });
      navigate("/", { replace: true });
    } catch (error: unknown) {
      setErro(
        error instanceof ApiError
          ? error.message
          : "Erro ao cadastrar estudante.",
      );
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="max-w-xl mx-auto px-4 py-12">
      <div className="text-center mb-8">
        <div className="h-12 w-12 rounded-2xl bg-apto-primary-light text-apto-primary flex items-center justify-center mx-auto mb-3">
          <Users size={20} />
        </div>
        <h1 className="text-2xl font-bold text-apto-text-main">Criar conta no Study Buddy</h1>
      </div>

      <form
        onSubmit={submeter}
        className="bg-white rounded-apto-section border border-apto-border p-6 space-y-4"
      >
        <Input label="Nome" required value={nome} onChange={(e) => setNome(e.target.value)} />
        <Input
          label="Email"
          type="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <Input
          label="Telefone"
          value={telefone}
          onChange={(e) => setTelefone(e.target.value)}
          placeholder="(85) 99999-9999"
        />
        <Input
          label="Matrícula"
          required
          value={matricula}
          onChange={(e) => setMatricula(e.target.value)}
        />
        <Input
          label="Instituição"
          required
          value={instituicao}
          onChange={(e) => setInstituicao(e.target.value)}
        />

        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" disabled={salvando} className="flex-1">
            {salvando ? "Cadastrando..." : "Cadastrar e entrar"}
          </Button>
          <Button type="button" variant="secondary" onClick={() => navigate("/login")}>
            Cancelar
          </Button>
        </div>
      </form>

      <p className="text-sm text-apto-text-muted text-center mt-6">
        Já tem conta?{" "}
        <Link to="/login" className="text-apto-primary font-bold hover:underline">
          Entrar
        </Link>
      </p>
    </div>
  );
}
