import type {
  AcaoModeracaoAnuncio,
  FrequenciaVisitas,
  Genero,
  HorarioSono,
  NivelBarulho,
  NivelOrganizacao,
  OrigemCompatibilidade,
  PreferenciaGeneroConvivencia,
  RotinaEstudos,
  StatusAnuncio,
  StatusDenuncia,
  StatusManifestacaoInteresse,
  TipoAnuncio,
  TipoMoradia,
} from "../api/types";

const BRL = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL",
});

export function formatBRL(value: number | string | null | undefined): string {
  if (value === null || value === undefined) return "—";
  const n = typeof value === "string" ? Number(value) : value;
  if (Number.isNaN(n)) return "—";
  return BRL.format(n);
}

export function formatDate(iso: string | null | undefined): string {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleDateString("pt-BR");
}

export function formatDateTime(iso: string | null | undefined): string {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export const tipoMoradiaLabel: Record<TipoMoradia, string> = {
  APARTAMENTO: "Apartamento",
  CASA: "Casa",
  REPUBLICA: "República",
  QUARTO: "Quarto",
};

export const tipoAnuncioLabel: Record<TipoAnuncio, string> = {
  IMOVEL_COMPLETO: "Imóvel completo",
  VAGA_COMPARTILHADA: "Vaga compartilhada",
};

export const statusAnuncioLabel: Record<StatusAnuncio, string> = {
  ATIVO: "Ativo",
  PAUSADO: "Pausado",
  ENCERRADO: "Encerrado",
};

export const statusManifestacaoLabel: Record<
  StatusManifestacaoInteresse,
  string
> = {
  PENDENTE: "Pendente",
  ACEITA: "Aceita",
  RECUSADA: "Recusada",
  CANCELADA: "Cancelada",
};

export const statusDenunciaLabel: Record<StatusDenuncia, string> = {
  PENDENTE: "Pendente",
  EM_ANALISE: "Em análise",
  IMPROCEDENTE: "Improcedente",
  PROCEDENTE: "Procedente",
  ARQUIVADA: "Arquivada",
};

export const acaoModeracaoLabel: Record<AcaoModeracaoAnuncio, string> = {
  NENHUMA: "Nenhuma ação",
  PAUSAR_ANUNCIO: "Pausar anúncio",
  ENCERRAR_ANUNCIO: "Encerrar anúncio",
};

export const generoLabel: Record<Genero, string> = {
  MASCULINO: "Masculino",
  FEMININO: "Feminino",
  OUTRO: "Outro",
  PREFIRO_NAO_INFORMAR: "Prefiro não informar",
};

export const origemCompatibilidadeLabel: Record<
  OrigemCompatibilidade,
  string
> = {
  LLM: "IA",
  FALLBACK_DETERMINISTICO: "Cálculo direto",
};

export const horarioSonoLabel: Record<HorarioSono, string> = {
  CEDO: "Dorme cedo",
  MEDIO: "Horário médio",
  TARDE: "Coruja (dorme tarde)",
};

export const nivelBarulhoLabel: Record<NivelBarulho, string> = {
  BAIXO: "Prefere silêncio",
  MEDIO: "Tolerância média",
  ALTO: "Tolera barulho",
};

export const frequenciaVisitasLabel: Record<FrequenciaVisitas, string> = {
  RARAMENTE: "Raramente",
  AS_VEZES: "Às vezes",
  FREQUENTEMENTE: "Frequentemente",
};

export const nivelOrganizacaoLabel: Record<NivelOrganizacao, string> = {
  BAIXO: "Bagunceiro",
  MEDIO: "Moderado",
  ALTO: "Muito organizado",
};

export const rotinaEstudosLabel: Record<RotinaEstudos, string> = {
  CASA: "Estuda em casa",
  BIBLIOTECA: "Estuda na biblioteca",
  MISTA: "Mista",
};

export const preferenciaGeneroConvivenciaLabel: Record<
  PreferenciaGeneroConvivencia,
  string
> = {
  SEM_PREFERENCIA: "Sem preferência",
  APENAS_MULHERES: "Apenas mulheres",
  APENAS_HOMENS: "Apenas homens",
  A_COMBINAR: "A combinar",
};

export function enumOptions<T extends string>(
  labels: Record<T, string>,
): { value: T; label: string }[] {
  return (Object.keys(labels) as T[]).map((value) => ({
    value,
    label: labels[value],
  }));
}

export function iniciais(nome: string | null | undefined): string {
  if (!nome) return "?";
  const partes = nome.trim().split(/\s+/);
  if (partes.length === 1) return partes[0].slice(0, 2).toUpperCase();
  return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
}
