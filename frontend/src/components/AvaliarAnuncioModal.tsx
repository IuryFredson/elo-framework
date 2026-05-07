import { useState } from "react";
import { Button } from "./ui/Button";
import { Modal } from "./ui/Modal";
import { Textarea } from "./ui/Input";
import { StarRating } from "./ui/StarRating";
import { avaliacoesApi } from "../api/avaliacoes";
import { ApiError } from "../api/client";
import { useAuth } from "../auth/useAuth";
import { useToast } from "./ui/useToast";

interface Props {
  open: boolean;
  onClose: () => void;
  anuncioId: string;
  onSucesso?: () => void;
}

export function AvaliarAnuncioModal({
  open,
  onClose,
  anuncioId,
  onSucesso,
}: Props) {
  const { sessao } = useAuth();
  const { show } = useToast();
  const [notaGeral, setNotaGeral] = useState(0);
  const [notaComunicacao, setNotaComunicacao] = useState(0);
  const [notaFidelidade, setNotaFidelidade] = useState(0);
  const [notaEstado, setNotaEstado] = useState(0);
  const [notaCustoBeneficio, setNotaCustoBeneficio] = useState(0);
  const [comentario, setComentario] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  function reset() {
    setNotaGeral(0);
    setNotaComunicacao(0);
    setNotaFidelidade(0);
    setNotaEstado(0);
    setNotaCustoBeneficio(0);
    setComentario("");
    setErro(null);
  }

  async function enviar() {
    if (!sessao) return;
    setErro(null);
    if (
      [
        notaGeral,
        notaComunicacao,
        notaFidelidade,
        notaEstado,
        notaCustoBeneficio,
      ].some((n) => n < 1 || n > 5)
    ) {
      setErro("Todas as dimensões precisam de uma nota de 1 a 5.");
      return;
    }
    setEnviando(true);
    try {
      await avaliacoesApi.criar({
        avaliadorId: sessao.id,
        anuncioId,
        notaGeral,
        notaComunicacao,
        notaFidelidadeAnuncio: notaFidelidade,
        notaEstadoMoradia: notaEstado,
        notaCustoBeneficio,
        comentario,
      });
      show("Avaliação enviada");
      onSucesso?.();
      reset();
      onClose();
    } catch (e: unknown) {
      setErro(e instanceof ApiError ? e.message : "Erro ao enviar avaliação.");
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
      title="Avaliar locador"
      size="lg"
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
            {enviando ? "Enviando..." : "Enviar avaliação"}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        <p className="text-sm text-apto-text-muted">
          Avalie 5 dimensões de 1 a 5 estrelas. Seu feedback alimenta a
          reputação pública do locador.
        </p>
        <div className="grid sm:grid-cols-2 gap-4">
          <StarRating
            value={notaGeral}
            onChange={setNotaGeral}
            label="Geral"
          />
          <StarRating
            value={notaComunicacao}
            onChange={setNotaComunicacao}
            label="Comunicação"
          />
          <StarRating
            value={notaFidelidade}
            onChange={setNotaFidelidade}
            label="Fidelidade ao anúncio"
          />
          <StarRating
            value={notaEstado}
            onChange={setNotaEstado}
            label="Estado da moradia"
          />
          <StarRating
            value={notaCustoBeneficio}
            onChange={setNotaCustoBeneficio}
            label="Custo-benefício"
          />
        </div>
        <Textarea
          label="Comentário"
          maxLength={1000}
          value={comentario}
          onChange={(e) => setComentario(e.target.value)}
          placeholder="Conte sobre sua experiência..."
          hint={`${comentario.length}/1000`}
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
