import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../../components/ui/Button";
import { Input, Textarea } from "../../components/ui/Input";
import { Select } from "../../components/ui/Select";
import { perfilAcademicoApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { enumOptions } from "../../lib/format";
import {
  modalidadeEstudoLabel,
  nivelConhecimentoLabel,
  objetivoEstudoLabel,
  parseDisciplinas,
  periodoDisponibilidadeLabel,
} from "../../lib/studyBuddyFormat";
import type {
  ModalidadeEstudo,
  NivelConhecimento,
  ObjetivoEstudo,
  PeriodoDisponibilidade,
} from "../../study-buddy/types";

const PERIODOS = Object.keys(periodoDisponibilidadeLabel) as PeriodoDisponibilidade[];

export default function PerfilAcademicoForm() {
  const sessao = useSessaoObrigatoria();
  const navigate = useNavigate();
  const [curso, setCurso] = useState("");
  const [disciplinasTexto, setDisciplinasTexto] = useState("");
  const [disponibilidade, setDisponibilidade] = useState<PeriodoDisponibilidade[]>([]);
  const [objetivoEstudo, setObjetivoEstudo] = useState<ObjetivoEstudo>("PROVA");
  const [nivelConhecimento, setNivelConhecimento] = useState<NivelConhecimento>("INICIANTE");
  const [modalidadePreferida, setModalidadePreferida] = useState<ModalidadeEstudo>("HIBRIDO");
  const [descricao, setDescricao] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    perfilAcademicoApi
      .obter(sessao.id)
      .then((perfil) => {
        setCurso(perfil.curso);
        setDisciplinasTexto(perfil.disciplinasInteresse.join(", "));
        setDisponibilidade(perfil.disponibilidade);
        setObjetivoEstudo(perfil.objetivoEstudo);
        setNivelConhecimento(perfil.nivelConhecimento);
        setModalidadePreferida(perfil.modalidadePreferida);
        setDescricao(perfil.descricao ?? "");
      })
      .catch((error: unknown) => {
        if (!(error instanceof ApiError && error.status === 404)) {
          setErro(error instanceof ApiError ? error.message : "Erro ao carregar perfil.");
        }
      });
  }, [sessao.id]);

  function alternarPeriodo(periodo: PeriodoDisponibilidade) {
    setDisponibilidade((atual) =>
      atual.includes(periodo)
        ? atual.filter((item) => item !== periodo)
        : [...atual, periodo],
    );
  }

  async function submeter(event: React.FormEvent) {
    event.preventDefault();
    setErro(null);

    const disciplinasInteresse = parseDisciplinas(disciplinasTexto);
    if (disciplinasInteresse.length === 0) {
      setErro("Informe pelo menos uma disciplina de interesse.");
      return;
    }
    if (disponibilidade.length === 0) {
      setErro("Selecione ao menos um período de disponibilidade.");
      return;
    }

    setSalvando(true);
    try {
      await perfilAcademicoApi.atualizar(sessao.id, {
        curso,
        disciplinasInteresse,
        disponibilidade,
        objetivoEstudo,
        nivelConhecimento,
        modalidadePreferida,
        descricao: descricao || null,
      });
      navigate("/perfil", { replace: true });
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao salvar perfil.");
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8 pb-20">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-apto-text-main">Perfil acadêmico</h1>
        <p className="text-apto-text-muted mt-2">
          Preencha os dados usados pelo matching e pelos grupos de estudo.
        </p>
      </div>

      <form
        onSubmit={submeter}
        className="bg-white rounded-apto-section border border-apto-border p-6 space-y-5"
      >
        <Input label="Curso" required value={curso} onChange={(e) => setCurso(e.target.value)} />
        <Input
          label="Disciplinas de interesse"
          required
          value={disciplinasTexto}
          onChange={(e) => setDisciplinasTexto(e.target.value)}
          placeholder="Ex.: Estruturas de Dados, Cálculo II, PDS"
          hint="Separe por vírgula"
        />

        <div className="space-y-2">
          <p className="text-sm font-medium text-apto-text-main">Disponibilidade</p>
          <div className="flex flex-wrap gap-2">
            {PERIODOS.map((periodo) => {
              const ativo = disponibilidade.includes(periodo);
              return (
                <button
                  key={periodo}
                  type="button"
                  onClick={() => alternarPeriodo(periodo)}
                  className={`px-3 py-2 rounded-apto-ui border text-sm transition-colors ${
                    ativo
                      ? "bg-apto-primary text-white border-apto-primary"
                      : "bg-white text-apto-text-main border-apto-border hover:bg-apto-bg"
                  }`}
                >
                  {periodoDisponibilidadeLabel[periodo]}
                </button>
              );
            })}
          </div>
        </div>

        <div className="grid sm:grid-cols-3 gap-4">
          <Select
            label="Objetivo"
            value={objetivoEstudo}
            onChange={(e) => setObjetivoEstudo(e.target.value as ObjetivoEstudo)}
            options={enumOptions(objetivoEstudoLabel)}
          />
          <Select
            label="Nível"
            value={nivelConhecimento}
            onChange={(e) => setNivelConhecimento(e.target.value as NivelConhecimento)}
            options={enumOptions(nivelConhecimentoLabel)}
          />
          <Select
            label="Modalidade"
            value={modalidadePreferida}
            onChange={(e) => setModalidadePreferida(e.target.value as ModalidadeEstudo)}
            options={enumOptions(modalidadeEstudoLabel)}
          />
        </div>

        <Textarea
          label="Descrição complementar"
          value={descricao}
          onChange={(e) => setDescricao(e.target.value)}
          placeholder="Contexto, objetivos, ritmo de estudo, ferramentas preferidas..."
          hint={`${descricao.length}/500`}
          maxLength={500}
        />

        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" disabled={salvando} className="flex-1">
            {salvando ? "Salvando..." : "Salvar perfil"}
          </Button>
          <Button type="button" variant="secondary" onClick={() => navigate("/perfil")}>
            Cancelar
          </Button>
        </div>
      </form>
    </div>
  );
}
