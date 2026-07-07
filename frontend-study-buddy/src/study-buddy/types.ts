import type {
  OrigemCompatibilidade,
  StatusDenuncia,
  StatusManifestacaoInteresse,
  UUID,
} from "../api/types";

export type ModalidadeEstudo = "PRESENCIAL" | "ONLINE" | "HIBRIDO";
export type NivelConhecimento = "INICIANTE" | "INTERMEDIARIO" | "AVANCADO";
export type ObjetivoEstudo =
  | "PROVA"
  | "TRABALHO"
  | "PROJETO"
  | "REFORCO"
  | "CONCURSO"
  | "PESQUISA";
export type PeriodoDisponibilidade =
  | "MANHA"
  | "TARDE"
  | "NOITE"
  | "FIM_DE_SEMANA"
  | "FLEXIVEL";
export type StatusGrupoEstudo = "ATIVO" | "PAUSADO" | "ENCERRADO";

export interface EstudanteResponse {
  id: UUID;
  nome: string;
  email: string;
  telefone: string;
  ativo: boolean;
  matricula: string;
  instituicao: string;
}

export interface CriarEstudanteRequest {
  nome: string;
  email: string;
  telefone: string;
  matricula: string;
  instituicao: string;
}

export interface AtualizarEstudanteRequest extends CriarEstudanteRequest {}

export interface AlterarStatusEstudanteRequest {
  ativo: boolean;
}

export interface PerfilAcademicoResponse {
  estudanteId: UUID;
  nome: string;
  email: string;
  matricula: string;
  instituicao: string;
  curso: string;
  disciplinasInteresse: string[];
  disponibilidade: PeriodoDisponibilidade[];
  objetivoEstudo: ObjetivoEstudo;
  nivelConhecimento: NivelConhecimento;
  modalidadePreferida: ModalidadeEstudo;
  descricao?: string | null;
}

export interface AtualizarPerfilAcademicoRequest {
  curso: string;
  disciplinasInteresse: string[];
  disponibilidade: PeriodoDisponibilidade[];
  objetivoEstudo: ObjetivoEstudo;
  nivelConhecimento: NivelConhecimento;
  modalidadePreferida: ModalidadeEstudo;
  descricao?: string | null;
}

export interface GrupoEstudoResponse {
  id: UUID;
  titulo: string;
  descricao: string;
  disciplina: string;
  publicadorId: UUID;
  publicadorNome: string;
  quantidadeVagas: number;
  modalidade: ModalidadeEstudo;
  periodo: PeriodoDisponibilidade;
  status: StatusGrupoEstudo;
  dataPublicacao: string;
}

export interface CriarGrupoEstudoRequest {
  publicadorId: UUID;
  titulo: string;
  descricao: string;
  disciplina: string;
  quantidadeVagas: number;
  modalidade: ModalidadeEstudo;
  periodo: PeriodoDisponibilidade;
}

export interface CriarManifestacaoInteresseGrupoRequest {
  grupoId: UUID;
  interessadoId: UUID;
  mensagem: string;
}

export interface ManifestacaoInteresseGrupoResponse {
  id: UUID;
  grupoId: UUID;
  grupoTitulo: string;
  interessadoId: UUID;
  interessadoNome: string;
  publicadorId: UUID;
  publicadorNome: string;
  status: StatusManifestacaoInteresse;
  mensagem: string;
  dataManifestacao: string;
  dataResposta?: string | null;
}

export interface MatchEstudanteResponse {
  id: UUID;
  nome: string;
  curso: string;
  instituicao: string;
  disciplinasInteresse: string[];
  objetivoEstudo: ObjetivoEstudo;
  nivelConhecimento: NivelConhecimento;
  modalidadePreferida: ModalidadeEstudo;
  percentualCompatibilidade: number;
  justificativa: string;
  criteriosAtendidos: string[];
  origem: OrigemCompatibilidade;
}

export interface StudyBuddyMatchingResponse {
  solicitanteId: UUID;
  total: number;
  candidatos: MatchEstudanteResponse[];
}

export type CriterioDenunciaStudyBuddy =
  | "GRUPO_INADEQUADO"
  | "CONTEUDO_FORA_DO_TEMA"
  | "COMPORTAMENTO_INADEQUADO"
  | "USO_INDEVIDO"
  | "OUTRO";

export type AcaoModeracaoGrupoEstudo = "NENHUMA" | "PAUSAR_GRUPO" | "ENCERRAR_GRUPO";

export interface CriarDenunciaGrupoEstudoRequest {
  denuncianteId: UUID;
  grupoId: UUID;
  titulo: string;
  corpo: string;
  criterio: CriterioDenunciaStudyBuddy;
}

export interface DenunciaGrupoEstudoResponse {
  id: UUID;
  denuncianteId: UUID;
  grupoId: UUID;
  titulo: string;
  corpo: string;
  criterio: CriterioDenunciaStudyBuddy;
  status: StatusDenuncia;
  criadoEm: string;
}

export interface ModerarDenunciaGrupoEstudoRequest {
  novoStatus: StatusDenuncia;
  acaoGrupoEstudo: AcaoModeracaoGrupoEstudo;
  justificativa: string;
}

export interface ModeracaoGrupoEstudoResponse {
  denunciaId: UUID;
  grupoId: UUID;
  statusDenunciaAnterior: StatusDenuncia;
  statusDenunciaAtual: StatusDenuncia;
  acaoGrupoEstudoAplicada: AcaoModeracaoGrupoEstudo;
  statusGrupoAnterior: StatusGrupoEstudo;
  statusGrupoAtual: StatusGrupoEstudo;
  justificativa: string;
  moderadoEm: string;
}
