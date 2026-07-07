import { useState } from "react";
import { denunciasGrupoApi } from "../../api/studyBuddy";
import { ApiError } from "../../api/client";
import { enumOptions } from "../../lib/format";
import { criterioDenunciaStudyBuddyLabel } from "../../lib/studyBuddyFormat";
import { Button } from "../ui/Button";
import { Input, Textarea } from "../ui/Input";
import { Modal } from "../ui/Modal";
import { Select } from "../ui/Select";
import { useToast } from "../ui/useToast";
import type { UUID } from "../../api/types";
import type { CriterioDenunciaStudyBuddy } from "../../study-buddy/types";

interface ReportarGrupoModalProps {
  open: boolean;
  onClose: () => void;
  grupoId: UUID;
  grupoTitulo: string;
  denuncianteId: UUID;
}

export function ReportarGrupoModal({
  open,
  onClose,
  grupoId,
  grupoTitulo,
  denuncianteId,
}: ReportarGrupoModalProps) {
  const { show } = useToast();
  const [titulo, setTitulo] = useState("");
  const [corpo, setCorpo] = useState("");
  const [criterio, setCriterio] = useState<CriterioDenunciaStudyBuddy>("OUTRO");
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  function reset() {
    setTitulo("");
    setCorpo("");
    setCriterio("OUTRO");
    setErro(null);
  }

  async function enviar() {
    setErro(null);
    if (titulo.trim().length < 2 || corpo.trim().length < 2) {
      setErro("Informe título e descrição com pelo menos 2 caracteres.");
      return;
    }

    setEnviando(true);
    try {
      await denunciasGrupoApi.criar({
        denuncianteId,
        grupoId,
        titulo,
        corpo,
        criterio,
      });
      show("Denúncia registrada para análise da moderação.");
      reset();
      onClose();
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao registrar denúncia.");
    } finally {
      setEnviando(false);
    }
  }

  return (
    <Modal
      open={open}
      onClose={() => {
        reset();
        onClose();
      }}
      title={`Reportar grupo: ${grupoTitulo}`}
      footer={
        <>
          <Button
            variant="secondary"
            onClick={() => {
              reset();
              onClose();
            }}
          >
            Fechar
          </Button>
          <Button onClick={enviar} disabled={enviando}>
            {enviando ? "Registrando..." : "Registrar denúncia"}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        <div className="rounded-apto-ui border border-apto-border bg-apto-bg p-4 text-sm text-apto-text-muted">
          A denúncia segue o fluxo fixo do framework e entra na fila de moderação de grupos.
        </div>
        <Select
          label="Critério"
          value={criterio}
          onChange={(event) => setCriterio(event.target.value as CriterioDenunciaStudyBuddy)}
          options={enumOptions(criterioDenunciaStudyBuddyLabel)}
        />
        <Input
          label="Título do relato"
          value={titulo}
          onChange={(event) => setTitulo(event.target.value)}
          placeholder="Resumo do problema"
          maxLength={255}
        />
        <Textarea
          label="Descrição"
          value={corpo}
          onChange={(event) => setCorpo(event.target.value)}
          placeholder="Descreva o que deve ser analisado pela moderação."
          hint={`${corpo.length}/1000`}
          maxLength={1000}
        />
        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}
      </div>
    </Modal>
  );
}
