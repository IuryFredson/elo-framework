import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../../components/ui/Button";
import { Input, Textarea } from "../../components/ui/Input";
import { Select } from "../../components/ui/Select";
import { gruposApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { enumOptions } from "../../lib/format";
import {
  modalidadeEstudoLabel,
  periodoDisponibilidadeLabel,
} from "../../lib/studyBuddyFormat";
import type { ModalidadeEstudo, PeriodoDisponibilidade } from "../../study-buddy/types";

export default function GrupoForm() {
  const sessao = useSessaoObrigatoria();
  const navigate = useNavigate();
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [disciplina, setDisciplina] = useState("");
  const [quantidadeVagas, setQuantidadeVagas] = useState("4");
  const [modalidade, setModalidade] = useState<ModalidadeEstudo>("HIBRIDO");
  const [periodo, setPeriodo] = useState<PeriodoDisponibilidade>("NOITE");
  const [erro, setErro] = useState<string | null>(null);
  const [salvando, setSalvando] = useState(false);

  async function submeter(event: React.FormEvent) {
    event.preventDefault();
    setErro(null);
    setSalvando(true);

    try {
      const grupo = await gruposApi.criar({
        publicadorId: sessao.id,
        titulo,
        descricao,
        disciplina,
        quantidadeVagas: Number(quantidadeVagas),
        modalidade,
        periodo,
      });
      navigate(`/grupos/${grupo.id}`, { replace: true });
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao criar grupo.");
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8 pb-20">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-apto-text-main">Novo grupo de estudo</h1>
        <p className="text-apto-text-muted mt-2">
          Publique a oferta concreta da instância Study Buddy: um grupo de estudo com disciplina, modalidade, período e vagas.
        </p>
      </div>

      <form
        onSubmit={submeter}
        className="bg-white rounded-apto-section border border-apto-border p-6 space-y-5"
      >
        <Input label="Título" required value={titulo} onChange={(e) => setTitulo(e.target.value)} />
        <Input
          label="Disciplina"
          required
          value={disciplina}
          onChange={(e) => setDisciplina(e.target.value)}
        />
        <Textarea
          label="Descrição"
          required
          value={descricao}
          onChange={(e) => setDescricao(e.target.value)}
          placeholder="Explique objetivo, formato, pré-requisitos e ritmo esperado."
        />
        <div className="grid sm:grid-cols-3 gap-4">
          <Input
            label="Vagas"
            type="number"
            min={1}
            value={quantidadeVagas}
            onChange={(e) => setQuantidadeVagas(e.target.value)}
          />
          <Select
            label="Modalidade"
            value={modalidade}
            onChange={(e) => setModalidade(e.target.value as ModalidadeEstudo)}
            options={enumOptions(modalidadeEstudoLabel)}
          />
          <Select
            label="Período"
            value={periodo}
            onChange={(e) => setPeriodo(e.target.value as PeriodoDisponibilidade)}
            options={enumOptions(periodoDisponibilidadeLabel)}
          />
        </div>

        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" disabled={salvando} className="flex-1">
            {salvando ? "Publicando..." : "Publicar grupo"}
          </Button>
          <Button type="button" variant="secondary" onClick={() => navigate("/grupos")}>
            Cancelar
          </Button>
        </div>
      </form>
    </div>
  );
}
