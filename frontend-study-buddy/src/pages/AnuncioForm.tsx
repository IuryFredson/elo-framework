import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/Button";
import { Input, Textarea } from "../components/ui/Input";
import { Select } from "../components/ui/Select";
import { moradiasApi } from "../api/moradias";
import { anunciosApi } from "../api/anuncios";
import { useAuth } from "../auth/useAuth";
import { useToast } from "../components/ui/useToast";
import { ApiError } from "../api/client";
import {
  enumOptions,
  tipoAnuncioLabel,
  tipoMoradiaLabel,
} from "../lib/format";
import type { TipoAnuncio, TipoMoradia } from "../api/types";

export default function AnuncioForm() {
  const { sessao } = useAuth();
  const { show } = useToast();
  const navigate = useNavigate();

  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [valorMensal, setValorMensal] = useState("");
  const [tipoAnuncio, setTipoAnuncio] =
    useState<TipoAnuncio>("IMOVEL_COMPLETO");

  const [tipoMoradia, setTipoMoradia] = useState<TipoMoradia>("APARTAMENTO");
  const [bairro, setBairro] = useState("");
  const [enderecoResumo, setEnderecoResumo] = useState("");
  const [mobiliado, setMobiliado] = useState(false);
  const [aceitaAnimais, setAceitaAnimais] = useState(false);
  const [quantidadeVagas, setQuantidadeVagas] = useState(1);
  const [regrasMoradia, setRegrasMoradia] = useState("");

  if (!sessao) return null;

  async function submeter(e: React.FormEvent) {
    e.preventDefault();
    if (!sessao) return;
    setSalvando(true);
    setErro(null);
    try {
      const moradia = await moradiasApi.criar({
        tipoMoradia,
        bairro,
        enderecoResumo,
        mobiliado,
        aceitaAnimais,
        quantidadeVagas,
        regrasMoradia,
      });
      const anuncio = await anunciosApi.criar({
        titulo,
        descricao,
        valorMensal: Number(valorMensal),
        tipoAnuncio,
        moradiaId: moradia.id,
        anuncianteId: sessao.id,
      });
      show("Anúncio publicado");
      navigate(`/anuncios/${anuncio.id}`);
    } catch (e: unknown) {
      setErro(
        e instanceof ApiError ? e.message : "Erro ao publicar anúncio.",
      );
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-12 pb-24">
      <h1 className="text-2xl font-bold text-apto-text-main mb-2">
        Publicar anúncio
      </h1>
      <p className="text-apto-text-muted mb-8">
        Cadastramos a moradia e o anúncio em uma só etapa.
      </p>

      <form
        onSubmit={submeter}
        className="bg-white rounded-apto-section border border-apto-border p-6 space-y-6"
      >
        <section className="space-y-4">
          <h2 className="section-title">Sobre o anúncio</h2>
          <Input
            label="Título"
            required
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
            placeholder="Ex.: Quarto em república perto da USP"
          />
          <Textarea
            label="Descrição"
            required
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
            placeholder="Conte sobre a moradia, vizinhança, regras..."
          />
          <div className="grid sm:grid-cols-2 gap-4">
            <Input
              label="Valor mensal (R$)"
              type="number"
              min={0}
              step="0.01"
              required
              value={valorMensal}
              onChange={(e) => setValorMensal(e.target.value)}
            />
            <Select
              label="Tipo de anúncio"
              value={tipoAnuncio}
              onChange={(e) => setTipoAnuncio(e.target.value as TipoAnuncio)}
              options={enumOptions(tipoAnuncioLabel)}
            />
          </div>
        </section>

        <section className="space-y-4 pt-4 border-t border-apto-border">
          <h2 className="section-title">Sobre a moradia</h2>
          <div className="grid sm:grid-cols-2 gap-4">
            <Select
              label="Tipo de moradia"
              value={tipoMoradia}
              onChange={(e) => setTipoMoradia(e.target.value as TipoMoradia)}
              options={enumOptions(tipoMoradiaLabel)}
            />
            <Input
              label="Bairro"
              required
              value={bairro}
              onChange={(e) => setBairro(e.target.value)}
            />
          </div>
          <Input
            label="Endereço (resumo)"
            required
            value={enderecoResumo}
            onChange={(e) => setEnderecoResumo(e.target.value)}
            placeholder="Rua, número, referência"
          />
          <div className="grid sm:grid-cols-3 gap-4">
            <Input
              label="Quantidade de vagas"
              type="number"
              min={0}
              required
              value={quantidadeVagas}
              onChange={(e) => setQuantidadeVagas(Number(e.target.value))}
            />
            <label className="flex items-center gap-3 p-3 rounded-apto-ui border border-apto-border bg-apto-bg/40 cursor-pointer">
              <input
                type="checkbox"
                checked={mobiliado}
                onChange={(e) => setMobiliado(e.target.checked)}
                className="h-4 w-4 accent-apto-primary"
              />
              <span className="text-sm font-medium">Mobiliado</span>
            </label>
            <label className="flex items-center gap-3 p-3 rounded-apto-ui border border-apto-border bg-apto-bg/40 cursor-pointer">
              <input
                type="checkbox"
                checked={aceitaAnimais}
                onChange={(e) => setAceitaAnimais(e.target.checked)}
                className="h-4 w-4 accent-apto-primary"
              />
              <span className="text-sm font-medium">Aceita animais</span>
            </label>
          </div>
          <Textarea
            label="Regras da moradia"
            required
            value={regrasMoradia}
            onChange={(e) => setRegrasMoradia(e.target.value)}
            placeholder="Ex.: Sem festas após 22h, divisão de despesas igualitária..."
          />
        </section>

        {erro && (
          <div className="p-3 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
            {erro}
          </div>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" disabled={salvando} className="flex-1">
            {salvando ? "Publicando..." : "Publicar anúncio"}
          </Button>
          <Button
            type="button"
            variant="secondary"
            onClick={() => navigate(-1)}
          >
            Cancelar
          </Button>
        </div>
      </form>
    </div>
  );
}
