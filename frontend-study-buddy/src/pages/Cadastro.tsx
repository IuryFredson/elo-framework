import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Building2, GraduationCap } from "lucide-react";
import { Button } from "../components/ui/Button";
import { Input, Textarea } from "../components/ui/Input";
import { Select } from "../components/ui/Select";
import { locadoresApi, universitariosApi } from "../api/usuarios";
import { useAuth } from "../auth/useAuth";
import { ApiError } from "../api/client";
import type {
  CriarLocadorRequest,
  CriarUsuarioUniversitarioRequest,
  Genero,
} from "../api/types";

type Tab = "UNIVERSITARIO" | "LOCADOR";

export default function Cadastro() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState<Tab>("UNIVERSITARIO");
  const [erro, setErro] = useState<string | null>(null);
  const [salvando, setSalvando] = useState(false);

  // Campos comuns
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [telefone, setTelefone] = useState("");

  // Universitário
  const [emailInstitucional, setEmailInstitucional] = useState("");
  const [curso, setCurso] = useState("");
  const [dataNascimento, setDataNascimento] = useState("");
  const [genero, setGenero] = useState<Genero>("MASCULINO");

  // Locador
  const [documentoIdentificacao, setDocumentoIdentificacao] = useState("");
  const [nomeExibicaoOuRazao, setNomeExibicaoOuRazao] = useState("");

  async function submeter(e: React.FormEvent) {
    e.preventDefault();
    setErro(null);
    setSalvando(true);
    try {
      if (tab === "UNIVERSITARIO") {
        const body: CriarUsuarioUniversitarioRequest = {
          nome,
          email,
          telefone,
          emailInstitucional,
          curso,
          dataNascimento,
          genero,
        };
        const novo = await universitariosApi.criar(body);
        login({ id: novo.id, tipo: "UNIVERSITARIO", nome: novo.nome });
      } else {
        const body: CriarLocadorRequest = {
          nome,
          email,
          telefone,
          documentoIdentificacao,
          nomeExibicaoOuRazao,
        };
        const novo = await locadoresApi.criar(body);
        login({ id: novo.id, tipo: "LOCADOR", nome: novo.nome });
      }
      navigate("/", { replace: true });
    } catch (e: unknown) {
      setErro(
        e instanceof ApiError
          ? e.message
          : "Erro ao cadastrar. Tente novamente.",
      );
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="max-w-xl mx-auto px-4 py-12">
      <div className="text-center mb-8">
        <img src="/logo.svg" alt="Apto" className="h-12 w-auto mx-auto mb-3" />
        <h1 className="text-2xl font-bold text-apto-text-main">
          Criar conta no Apto
        </h1>
      </div>

      <div className="bg-white rounded-apto-section border border-apto-border p-1 flex gap-1 mb-6">
        <button
          type="button"
          onClick={() => setTab("UNIVERSITARIO")}
          className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-apto-ui font-medium text-sm transition-colors ${
            tab === "UNIVERSITARIO"
              ? "bg-apto-primary text-white"
              : "text-apto-text-muted"
          }`}
        >
          <GraduationCap size={16} />
          Sou universitário
        </button>
        <button
          type="button"
          onClick={() => setTab("LOCADOR")}
          className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-apto-ui font-medium text-sm transition-colors ${
            tab === "LOCADOR"
              ? "bg-apto-primary text-white"
              : "text-apto-text-muted"
          }`}
        >
          <Building2 size={16} />
          Sou locador
        </button>
      </div>

      <form
        onSubmit={submeter}
        className="bg-white rounded-apto-section border border-apto-border p-6 space-y-4"
      >
        <Input
          label="Nome"
          required
          value={nome}
          onChange={(e) => setNome(e.target.value)}
        />
        <Input
          label="Email"
          type="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <Input
          label="Telefone"
          required
          value={telefone}
          onChange={(e) => setTelefone(e.target.value)}
          placeholder="(11) 99999-9999"
        />

        {tab === "UNIVERSITARIO" ? (
          <>
            <Input
              label="Email institucional"
              type="email"
              required
              value={emailInstitucional}
              onChange={(e) => setEmailInstitucional(e.target.value)}
            />
            <Input
              label="Curso"
              required
              value={curso}
              onChange={(e) => setCurso(e.target.value)}
            />
            <Input
              label="Data de nascimento"
              type="date"
              required
              value={dataNascimento}
              onChange={(e) => setDataNascimento(e.target.value)}
            />
            <Select
              label="Gênero"
              value={genero}
              onChange={(e) => setGenero(e.target.value as Genero)}
              options={[
                { value: "MASCULINO", label: "Masculino" },
                { value: "FEMININO", label: "Feminino" },
                { value: "OUTRO", label: "Outro" },
                {
                  value: "PREFIRO_NAO_INFORMAR",
                  label: "Prefiro não informar",
                },
              ]}
            />
          </>
        ) : (
          <>
            <Input
              label="Documento de identificação"
              required
              value={documentoIdentificacao}
              onChange={(e) => setDocumentoIdentificacao(e.target.value)}
              placeholder="CPF/CNPJ"
            />
            <Textarea
              label="Nome de exibição ou razão social"
              required
              value={nomeExibicaoOuRazao}
              onChange={(e) => setNomeExibicaoOuRazao(e.target.value)}
            />
          </>
        )}

        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" disabled={salvando} className="flex-1">
            {salvando ? "Cadastrando..." : "Cadastrar e entrar"}
          </Button>
          <Button
            type="button"
            variant="secondary"
            onClick={() => navigate("/login")}
          >
            Cancelar
          </Button>
        </div>
      </form>

      <p className="text-sm text-apto-text-muted text-center mt-6">
        Já tem conta?{" "}
        <Link
          to="/login"
          className="text-apto-primary font-bold hover:underline"
        >
          Entrar
        </Link>
      </p>
    </div>
  );
}
