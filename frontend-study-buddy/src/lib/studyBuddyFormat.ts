import type {
  ModalidadeEstudo,
  NivelConhecimento,
  ObjetivoEstudo,
  PeriodoDisponibilidade,
  StatusGrupoEstudo,
} from "../study-buddy/types";

export const modalidadeEstudoLabel: Record<ModalidadeEstudo, string> = {
  PRESENCIAL: "Presencial",
  ONLINE: "Online",
  HIBRIDO: "Híbrido",
};

export const nivelConhecimentoLabel: Record<NivelConhecimento, string> = {
  INICIANTE: "Iniciante",
  INTERMEDIARIO: "Intermediário",
  AVANCADO: "Avançado",
};

export const objetivoEstudoLabel: Record<ObjetivoEstudo, string> = {
  PROVA: "Prova",
  TRABALHO: "Trabalho",
  PROJETO: "Projeto",
  REFORCO: "Reforço",
  CONCURSO: "Concurso",
  PESQUISA: "Pesquisa",
};

export const periodoDisponibilidadeLabel: Record<PeriodoDisponibilidade, string> = {
  MANHA: "Manhã",
  TARDE: "Tarde",
  NOITE: "Noite",
  FIM_DE_SEMANA: "Fim de semana",
  FLEXIVEL: "Flexível",
};

export const statusGrupoEstudoLabel: Record<StatusGrupoEstudo, string> = {
  ATIVO: "Ativo",
  PAUSADO: "Pausado",
  ENCERRADO: "Encerrado",
};

export function parseDisciplinas(raw: string): string[] {
  return raw
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

export function formatarDisciplinas(disciplinas: string[]): string {
  return disciplinas.join(", ");
}
