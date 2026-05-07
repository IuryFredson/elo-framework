import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/Button";
import { Input, Textarea } from "../components/ui/Input";
import { Select } from "../components/ui/Select";
import { perfilApi } from "../api/usuarios";
import { useAuth } from "../auth/useAuth";
import { ApiError } from "../api/client";
import { useToast } from "../components/ui/useToast";
import {
  enumOptions,
  frequenciaVisitasLabel,
  generoLabel,
  horarioSonoLabel,
  nivelBarulhoLabel,
  nivelOrganizacaoLabel,
  preferenciaGeneroConvivenciaLabel,
  rotinaEstudosLabel,
} from "../lib/format";
import type {
  AtualizarPerfilRequest,
  FrequenciaVisitas,
  Genero,
  HorarioSono,
  NivelBarulho,
  NivelOrganizacao,
  PreferenciaGeneroConvivencia,
  RotinaEstudos,
} from "../api/types";

export default function PerfilConvivenciaForm() {
  const { sessao } = useAuth();
  const { show } = useToast();
  const navigate = useNavigate();

  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [form, setForm] = useState<AtualizarPerfilRequest>({
    nome: "",
    email: "",
    emailInstitucional: "",
    telefone: "",
    curso: "",
    dataNascimento: "",
    genero: "MASCULINO",
    horarioSono: "MEDIO",
    nivelBarulhoAceitavel: "MEDIO",
    frequenciaVisitas: "AS_VEZES",
    nivelOrganizacao: "MEDIO",
    rotinaEstudos: "MISTA",
    consomeAlcool: false,
    fumante: false,
    aceitaAnimais: false,
    preferenciaGeneroConvivencia: "SEM_PREFERENCIA",
    descricaoLivre: "",
  });

  useEffect(() => {
    if (!sessao || sessao.tipo !== "UNIVERSITARIO") {
      setCarregando(false);
      return;
    }
    perfilApi
      .obter(sessao.id)
      .then((p) => {
        setForm({
          nome: p.nome,
          email: p.email,
          emailInstitucional: p.emailInstitucional,
          telefone: p.telefone,
          curso: p.curso,
          dataNascimento: p.dataNascimento,
          genero: p.genero,
          horarioSono: p.horarioSono ?? "MEDIO",
          nivelBarulhoAceitavel: p.nivelBarulhoAceitavel ?? "MEDIO",
          frequenciaVisitas: p.frequenciaVisitas ?? "AS_VEZES",
          nivelOrganizacao: p.nivelOrganizacao ?? "MEDIO",
          rotinaEstudos: p.rotinaEstudos ?? "MISTA",
          consomeAlcool: p.consomeAlcool ?? false,
          fumante: p.fumante ?? false,
          aceitaAnimais: p.aceitaAnimais ?? false,
          preferenciaGeneroConvivencia:
            p.preferenciaGeneroConvivencia ?? "SEM_PREFERENCIA",
          descricaoLivre: p.descricaoLivre ?? "",
        });
      })
      .catch((e: unknown) => {
        if (!(e instanceof ApiError && e.status === 404)) {
          setErro(e instanceof ApiError ? e.message : "Erro ao carregar perfil.");
        }
      })
      .finally(() => setCarregando(false));
  }, [sessao]);

  if (!sessao || sessao.tipo !== "UNIVERSITARIO") {
    return (
      <div className="max-w-2xl mx-auto px-4 py-12 text-apto-text-muted">
        O perfil de convivência só é preenchido por universitários.
      </div>
    );
  }

  if (carregando) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-12 text-apto-text-muted">
        Carregando...
      </div>
    );
  }

  async function submeter(e: React.FormEvent) {
    e.preventDefault();
    if (!sessao) return;
    setSalvando(true);
    setErro(null);
    try {
      await perfilApi.atualizar(sessao.id, {
        ...form,
        descricaoLivre: form.descricaoLivre?.trim() || null,
      });
      show("Perfil atualizado");
      navigate("/profile");
    } catch (e: unknown) {
      setErro(
        e instanceof ApiError ? e.message : "Erro ao salvar perfil.",
      );
    } finally {
      setSalvando(false);
    }
  }

  function set<K extends keyof AtualizarPerfilRequest>(
    key: K,
    value: AtualizarPerfilRequest[K],
  ) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-12 pb-24">
      <h1 className="text-2xl font-bold text-apto-text-main mb-2">
        Perfil de convivência
      </h1>
      <p className="text-apto-text-muted mb-8">
        Estes dados alimentam o matchmaking de colegas. Quanto mais honesto,
        melhor o resultado.
      </p>

      <form
        onSubmit={submeter}
        className="bg-white rounded-apto-section border border-apto-border p-6 space-y-6"
      >
        <section className="space-y-4">
          <h2 className="section-title">Dados pessoais</h2>
          <Input
            label="Nome"
            required
            value={form.nome}
            onChange={(e) => set("nome", e.target.value)}
          />
          <div className="grid sm:grid-cols-2 gap-4">
            <Input
              label="Email"
              type="email"
              required
              value={form.email}
              onChange={(e) => set("email", e.target.value)}
            />
            <Input
              label="Email institucional"
              type="email"
              required
              value={form.emailInstitucional}
              onChange={(e) => set("emailInstitucional", e.target.value)}
            />
          </div>
          <div className="grid sm:grid-cols-2 gap-4">
            <Input
              label="Telefone"
              required
              value={form.telefone}
              onChange={(e) => set("telefone", e.target.value)}
            />
            <Input
              label="Curso"
              required
              value={form.curso}
              onChange={(e) => set("curso", e.target.value)}
            />
          </div>
          <div className="grid sm:grid-cols-2 gap-4">
            <Input
              label="Data de nascimento"
              type="date"
              required
              value={form.dataNascimento}
              onChange={(e) => set("dataNascimento", e.target.value)}
            />
            <Select
              label="Gênero"
              value={form.genero}
              onChange={(e) => set("genero", e.target.value as Genero)}
              options={[
                { value: "MASCULINO", label: generoLabel.MASCULINO },
                { value: "FEMININO", label: generoLabel.FEMININO },
                { value: "OUTRO", label: generoLabel.OUTRO },
              ]}
            />
          </div>
        </section>

        <section className="space-y-4 pt-4 border-t border-apto-border">
          <h2 className="section-title">Convivência</h2>
          <div className="grid sm:grid-cols-2 gap-4">
            <Select
              label="Horário de sono"
              value={form.horarioSono}
              onChange={(e) =>
                set("horarioSono", e.target.value as HorarioSono)
              }
              options={enumOptions(horarioSonoLabel)}
            />
            <Select
              label="Tolerância a barulho"
              value={form.nivelBarulhoAceitavel}
              onChange={(e) =>
                set("nivelBarulhoAceitavel", e.target.value as NivelBarulho)
              }
              options={enumOptions(nivelBarulhoLabel)}
            />
            <Select
              label="Frequência de visitas"
              value={form.frequenciaVisitas}
              onChange={(e) =>
                set("frequenciaVisitas", e.target.value as FrequenciaVisitas)
              }
              options={enumOptions(frequenciaVisitasLabel)}
            />
            <Select
              label="Nível de organização"
              value={form.nivelOrganizacao}
              onChange={(e) =>
                set("nivelOrganizacao", e.target.value as NivelOrganizacao)
              }
              options={enumOptions(nivelOrganizacaoLabel)}
            />
            <Select
              label="Rotina de estudos"
              value={form.rotinaEstudos}
              onChange={(e) =>
                set("rotinaEstudos", e.target.value as RotinaEstudos)
              }
              options={enumOptions(rotinaEstudosLabel)}
            />
            <Select
              label="Preferência de gênero p/ convivência"
              value={form.preferenciaGeneroConvivencia}
              onChange={(e) =>
                set(
                  "preferenciaGeneroConvivencia",
                  e.target.value as PreferenciaGeneroConvivencia,
                )
              }
              options={enumOptions(preferenciaGeneroConvivenciaLabel)}
            />
          </div>

          <div className="grid sm:grid-cols-3 gap-4">
            <CheckRow
              label="Consome álcool"
              value={form.consomeAlcool}
              onChange={(v) => set("consomeAlcool", v)}
            />
            <CheckRow
              label="Fumante"
              value={form.fumante}
              onChange={(v) => set("fumante", v)}
            />
            <CheckRow
              label="Aceita animais"
              value={form.aceitaAnimais}
              onChange={(v) => set("aceitaAnimais", v)}
            />
          </div>

          <Textarea
            label="Sobre você"
            placeholder="Conte algo sobre seus hábitos, rotina e o que busca em um colega..."
            maxLength={1000}
            value={form.descricaoLivre ?? ""}
            onChange={(e) => set("descricaoLivre", e.target.value)}
            hint={`${(form.descricaoLivre ?? "").length}/1000`}
          />
        </section>

        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" disabled={salvando} className="flex-1">
            {salvando ? "Salvando..." : "Salvar perfil"}
          </Button>
          <Button
            type="button"
            variant="secondary"
            onClick={() => navigate("/profile")}
          >
            Cancelar
          </Button>
        </div>
      </form>
    </div>
  );
}

function CheckRow({
  label,
  value,
  onChange,
}: {
  label: string;
  value: boolean;
  onChange: (v: boolean) => void;
}) {
  return (
    <label className="flex items-center gap-3 p-3 rounded-apto-ui border border-apto-border bg-apto-bg/40 cursor-pointer">
      <input
        type="checkbox"
        checked={value}
        onChange={(e) => onChange(e.target.checked)}
        className="h-4 w-4 accent-apto-primary"
      />
      <span className="text-sm font-medium text-apto-text-main">{label}</span>
    </label>
  );
}
