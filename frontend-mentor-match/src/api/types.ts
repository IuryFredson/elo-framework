export type UUID = string;
export type TipoUsuario = "MENTOR" | "ALUNO";
export type NivelConhecimento = "INICIANTE" | "INTERMEDIARIO" | "AVANCADO" | "ESPECIALISTA";
export type ModalidadeMentoria = "PRESENCIAL" | "ONLINE" | "HIBRIDA";
export type PeriodoDisponibilidade = "MANHA" | "TARDE" | "NOITE" | "FIM_DE_SEMANA" | "FLEXIVEL";
export type StatusSessao = "ATIVA" | "PAUSADA" | "ENCERRADA";
export type StatusSolicitacao = "PENDENTE" | "ACEITA" | "RECUSADA" | "CANCELADA";
export type StatusDenuncia = "PENDENTE" | "EM_ANALISE" | "IMPROCEDENTE" | "PROCEDENTE" | "ARQUIVADA";
export type CriterioDenuncia = "CONTEUDO_INADEQUADO" | "INFORMACAO_FALSA" | "COMPORTAMENTO_INADEQUADO" | "FRAUDE" | "USO_INDEVIDO" | "OUTRO";
export type AcaoModeracao = "NENHUMA" | "PAUSAR_SESSAO" | "ENCERRAR_SESSAO";
export type OrigemCompatibilidade = "LLM" | "FALLBACK_DETERMINISTICO";
export interface Participante { id: UUID; nome: string; email: string; telefone: string; ativo: boolean; tipo: TipoUsuario }
export interface Perfil { participanteId: UUID; nome: string; tipo: TipoUsuario; areas: string[]; objetivos: string[]; nivelConhecimento: NivelConhecimento; modalidades: ModalidadeMentoria[]; disponibilidades: PeriodoDisponibilidade[]; idiomas: string[]; descricao?: string }
export interface SessaoMentoria { id: UUID; titulo: string; descricao: string; area: string; nivelAtendido: NivelConhecimento; modalidade: ModalidadeMentoria; periodo: PeriodoDisponibilidade; capacidade: number; status: StatusSessao; dataPublicacao: string; mentorId: UUID; mentorNome: string }
export interface Solicitacao { id: UUID; sessaoId: UUID; sessaoTitulo: string; alunoId: UUID; alunoNome: string; mentorId: UUID; mentorNome: string; status: StatusSolicitacao; mensagem: string; dataSolicitacao: string; dataResposta?: string }
export interface MatchMentor { id:UUID; nome:string; areas:string[]; objetivos:string[]; nivelConhecimento:NivelConhecimento; modalidades:ModalidadeMentoria[]; disponibilidades:PeriodoDisponibilidade[]; idiomas:string[]; descricao?:string; percentualCompatibilidade:number; justificativa:string; criteriosAtendidos:string[]; origem:OrigemCompatibilidade }
export interface Matching { solicitanteId: UUID; total: number; mentores: MatchMentor[] }
export interface Denuncia { id: UUID; denuncianteId: UUID; sessaoId: UUID; titulo: string; corpo: string; criterio: CriterioDenuncia; status: StatusDenuncia; criadoEm: string }
