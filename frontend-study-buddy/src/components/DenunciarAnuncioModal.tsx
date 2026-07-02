import { useState } from "react";
import { Button } from "./ui/Button";
import { Modal } from "./ui/Modal";
import { Input, Textarea } from "./ui/Input";
import { denunciasApi } from "../api/denuncias";
import { ApiError } from "../api/client";
import { useAuth } from "../auth/useAuth";
import { useToast } from "./ui/useToast";

interface Props {
  open: boolean;
  onClose: () => void;
  anuncioId: string;
}

export function DenunciarAnuncioModal({ open, onClose, anuncioId }: Props) {
  const { sessao } = useAuth();
  const { show } = useToast();
  const [titulo, setTitulo] = useState("");
  const [corpo, setCorpo] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  function reset() {
    setTitulo("");
    setCorpo("");
    setErro(null);
  }

  async function enviar() {
    if (!sessao) return;
    setErro(null);
    if (titulo.trim().length < 2 || titulo.length > 255) {
      setErro("Título precisa ter entre 2 e 255 caracteres.");
      return;
    }
    if (corpo.trim().length < 2 || corpo.length > 1000) {
      setErro("Descrição precisa ter entre 2 e 1000 caracteres.");
      return;
    }
    setEnviando(true);
    try {
      await denunciasApi.criar({
        denuncianteId: sessao.id,
        anuncioId,
        titulo,
        corpo,
      });
      show("Denúncia registrada");
      reset();
      onClose();
    } catch (e: unknown) {
      setErro(e instanceof ApiError ? e.message : "Erro ao enviar denúncia.");
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
      title="Denunciar anúncio"
      footer={
        <>
          <Button
            variant="secondary"
            onClick={() => {
              reset();
              onClose();
            }}
          >
            Cancelar
          </Button>
          <Button onClick={enviar} disabled={enviando}>
            {enviando ? "Enviando..." : "Enviar denúncia"}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        <p className="text-sm text-apto-text-muted">
          A denúncia será analisada pela equipe de moderação. Forneça
          informações suficientes para entendermos o problema.
        </p>
        <Input
          label="Título"
          required
          maxLength={255}
          value={titulo}
          onChange={(e) => setTitulo(e.target.value)}
          placeholder="Resumo do problema"
        />
        <Textarea
          label="Descrição"
          required
          maxLength={1000}
          value={corpo}
          onChange={(e) => setCorpo(e.target.value)}
          placeholder="Detalhe o que está errado..."
          hint={`${corpo.length}/1000`}
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
