import { useState } from "react";
import { Button } from "../ui/Button";
import { Input, Textarea } from "../ui/Input";
import { Modal } from "../ui/Modal";
import { useToast } from "../ui/useToast";

interface ReportarGrupoModalProps {
  open: boolean;
  onClose: () => void;
  grupoTitulo: string;
}

export function ReportarGrupoModal({
  open,
  onClose,
  grupoTitulo,
}: ReportarGrupoModalProps) {
  const { show } = useToast();
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");

  function reset() {
    setTitulo("");
    setDescricao("");
  }

  function enviar() {
    show(
      "Fluxo de denúncia preparado na interface. A integração ficará para etapa futura.",
      "info",
    );
    reset();
    onClose();
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
          <Button onClick={enviar}>Registrar</Button>
        </>
      }
    >
      <div className="space-y-4">
        <div className="rounded-apto-ui border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">
          Denúncia e moderação já têm espaço previsto na interface, mas ainda
          não foram implementadas no backend da instância Study Buddy.
        </div>
        <Input
          label="Título do relato"
          value={titulo}
          onChange={(event) => setTitulo(event.target.value)}
          placeholder="Resumo do problema"
        />
        <Textarea
          label="Descrição"
          value={descricao}
          onChange={(event) => setDescricao(event.target.value)}
          placeholder="Descreva o que você quer sinalizar para moderação futura."
          hint={`${descricao.length}/1000`}
          maxLength={1000}
        />
      </div>
    </Modal>
  );
}
