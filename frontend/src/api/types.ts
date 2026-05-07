// Tipos espelhando os DTOs do apto-api. Mantemos os nomes em pt-BR para
// alinhar 1:1 com o backend e evitar tradução em runtime.

export type UUID = string;

export type TipoUsuario = "UNIVERSITARIO" | "LOCADOR";

export type Genero =
  | "MASCULINO"
  | "FEMININO"
  | "OUTRO"
  | "PREFIRO_NAO_INFORMAR";

export type TipoMoradia = "APARTAMENTO" | "CASA" | "REPUBLICA" | "QUARTO";

export type TipoAnuncio = "IMOVEL_COMPLETO" | "VAGA_COMPARTILHADA";

export type StatusAnuncio = "ATIVO" | "PAUSADO" | "ENCERRADO";

export type StatusManifestacaoInteresse =
  | "PENDENTE"
  | "ACEITA"
  | "RECUSADA"
  | "CANCELADA";

export type StatusDenuncia =
  | "PENDENTE"
  | "EM_ANALISE"
  | "IMPROCEDENTE"
  | "PROCEDENTE"
  | "ARQUIVADA";

export type AcaoModeracaoAnuncio =
  | "NENHUMA"
  | "PAUSAR_ANUNCIO"
  | "ENCERRAR_ANUNCIO";

export type OrigemCompatibilidade = "LLM" | "FALLBACK_DETERMINISTICO";

// Perfil de Convivência (UC01)
export type HorarioSono = "CEDO" | "MEDIO" | "TARDE";
export type NivelBarulho = "BAIXO" | "MEDIO" | "ALTO";
export type FrequenciaVisitas = "RARAMENTE" | "AS_VEZES" | "FREQUENTEMENTE";
export type NivelOrganizacao = "BAIXO" | "MEDIO" | "ALTO";
export type RotinaEstudos = "CASA" | "BIBLIOTECA" | "MISTA";
export type PreferenciaGeneroConvivencia =
  | "SEM_PREFERENCIA"
  | "APENAS_MULHERES"
  | "APENAS_HOMENS"
  | "A_COMBINAR";

export interface PerfilConvivencia {
  horarioSono?: HorarioSono | null;
  nivelBarulhoAceitavel?: NivelBarulho | null;
  frequenciaVisitas?: FrequenciaVisitas | null;
  nivelOrganizacao?: NivelOrganizacao | null;
  rotinaEstudos?: RotinaEstudos | null;
  consomeAlcool?: boolean | null;
  fumante?: boolean | null;
  aceitaAnimais?: boolean | null;
  preferenciaGeneroConvivencia?: PreferenciaGeneroConvivencia | null;
  descricaoLivre?: string | null;
}

export interface UsuarioUniversitarioResponse {
  id: UUID;
  nome: string;
  email: string;
  telefone: string;
  ativo: boolean;
  emailInstitucional: string;
  curso: string;
  dataNascimento: string; // ISO LocalDate
  genero: Genero;
}

export interface CriarUsuarioUniversitarioRequest {
  nome: string;
  email: string;
  telefone: string;
  emailInstitucional: string;
  curso: string;
  dataNascimento: string;
  genero: Genero;
}

export interface LocadorResponse {
  id: UUID;
  nome: string;
  email: string;
  telefone: string;
  ativo: boolean;
  documentoIdentificacao: string;
  nomeExibicaoOuRazao: string;
}

export interface CriarLocadorRequest {
  nome: string;
  email: string;
  telefone: string;
  documentoIdentificacao: string;
  nomeExibicaoOuRazao: string;
}

export interface PerfilResponse extends PerfilConvivencia {
  idUsuario: UUID;
  nome: string;
  email: string;
  emailInstitucional: string;
  telefone: string;
  curso: string;
  dataNascimento: string;
  genero: Genero;
}

// AtualizarPerfilRequestDTO no backend exige TODOS os campos (NotNull/NotBlank).
// Modelamos como requeridos exceto descricaoLivre.
export interface AtualizarPerfilRequest {
  nome: string;
  email: string;
  emailInstitucional: string;
  telefone: string;
  curso: string;
  dataNascimento: string;
  genero: Genero;
  horarioSono: HorarioSono;
  nivelBarulhoAceitavel: NivelBarulho;
  frequenciaVisitas: FrequenciaVisitas;
  nivelOrganizacao: NivelOrganizacao;
  rotinaEstudos: RotinaEstudos;
  consomeAlcool: boolean;
  fumante: boolean;
  aceitaAnimais: boolean;
  preferenciaGeneroConvivencia: PreferenciaGeneroConvivencia;
  descricaoLivre?: string | null;
}

// Moradia
export interface MoradiaResponse {
  id: UUID;
  tipoMoradia: TipoMoradia;
  bairro: string;
  enderecoResumo: string;
  mobiliado: boolean;
  aceitaAnimais: boolean;
  quantidadeVagas: number;
  regrasMoradia?: string | null;
}

export interface CriarMoradiaRequest {
  tipoMoradia: TipoMoradia;
  bairro: string;
  enderecoResumo: string;
  mobiliado: boolean;
  aceitaAnimais: boolean;
  quantidadeVagas: number;
  regrasMoradia: string;
}

// Anúncio
export interface AnuncioResponse {
  id: UUID;
  titulo: string;
  descricao: string;
  valorMensal: number;
  tipoAnuncio: TipoAnuncio;
  status: StatusAnuncio;
  dataPublicacao: string;
  anuncianteId: UUID;
  moradiaId: UUID;
}

export interface CriarAnuncioRequest {
  titulo: string;
  descricao: string;
  valorMensal: number;
  tipoAnuncio: TipoAnuncio;
  moradiaId: UUID;
  anuncianteId: UUID;
}

export interface AtualizarAnuncioRequest {
  titulo?: string;
  descricao?: string;
  valorMensal?: number;
  tipoAnuncio?: TipoAnuncio;
}

export interface FiltroBuscaAnuncio {
  valorMin?: number;
  valorMax?: number;
  bairro?: string;
  tipoMoradia?: TipoMoradia;
  tipoAnuncio?: TipoAnuncio;
  mobiliado?: boolean;
  aceitaAnimais?: boolean;
  quantidadeVagas?: number;
  page?: number;
  size?: number;
}

export interface BuscaAnuncioResponse {
  id: UUID;
  titulo: string;
  descricao: string;
  valorMensal: number;
  tipoAnuncio: TipoAnuncio;
  status: StatusAnuncio;
  dataPublicacao: string;
  moradiaId: UUID;
  tipoMoradia: TipoMoradia;
  bairro: string;
  enderecoResumo: string;
  mobiliado: boolean;
  aceitaAnimais: boolean;
  quantidadeVagas: number;
  nomeAnunciante: string;
}

export interface PaginaResponse<T> {
  conteudo: T[];
  paginaAtual: number;
  totalPaginas: number;
  totalElementos: number;
  tamanhoPagina: number;
}

// Manifestação de Interesse
export interface ContatoLiberadoResponse {
  nome: string;
  email: string;
  telefone: string;
  emailInstitucional?: string | null;
}

export interface ManifestacaoInteresseResponse {
  id: UUID;
  anuncioId: UUID;
  anuncioTitulo: string;
  interessadoId: UUID;
  interessadoNome: string;
  status: StatusManifestacaoInteresse;
  mensagem: string;
  dataManifestacao: string;
  dataResposta?: string | null;
}

export interface ManifestacaoInteresseDetalheResponse
  extends ManifestacaoInteresseResponse {
  contatoInteressado?: ContatoLiberadoResponse | null;
  contatoAnunciante?: ContatoLiberadoResponse | null;
}

export interface CriarManifestacaoInteresseRequest {
  anuncioId: UUID;
  interessadoId: UUID;
  mensagem: string;
}

// Matchmaking
export interface MatchColegaResponse {
  id: UUID;
  nome: string;
  curso: string;
  genero: Genero;
  percentualCompatibilidade: number;
  justificativa: string;
  origem: OrigemCompatibilidade;
}

export interface MatchmakingResponse {
  solicitanteId: UUID;
  total: number;
  candidatos: MatchColegaResponse[];
}

// Avaliação
export interface CriarAvaliacaoRequest {
  avaliadorId: UUID;
  anuncioId: UUID;
  notaGeral: number;
  notaComunicacao: number;
  notaFidelidadeAnuncio: number;
  notaEstadoMoradia: number;
  notaCustoBeneficio: number;
  comentario: string;
}

export interface AvaliacaoResponse {
  id: UUID;
  avaliadorId: UUID;
  avaliadorNome: string;
  locadorAvaliadoId: UUID;
  locadorAvaliadoNome: string;
  moradiaId: UUID;
  anuncioId: UUID;
  anuncioTitulo: string;
  notaGeral: number;
  notaComunicacao: number;
  notaFidelidadeAnuncio: number;
  notaEstadoMoradia: number;
  notaCustoBeneficio: number;
  comentario: string;
  dataCriacao: string;
  ativa: boolean;
}

export interface ResumoAvaliacoesLocadorResponse {
  locadorId: UUID;
  nomeLocador: string;
  totalAvaliacoes: number;
  notaMediaGeral: number | null;
  notaMediaComunicacao: number | null;
  notaMediaFidelidadeAnuncio: number | null;
  notaMediaEstadoMoradia: number | null;
  notaMediaCustoBeneficio: number | null;
}

// Reputação
export interface ReputacaoLocadorResponse {
  id: UUID;
  locadorId: UUID;
  reputacaoScore: number;
  totalAvaliacoes: number;
  mediaGeral: number;
  mediaComunicacao: number;
  mediaFidelidadeAnuncio: number;
  mediaEstadoMoradia: number;
  mediaCustoBeneficio: number;
  ultimaAtualizacao?: string | null;
}

// Denúncia
export interface CriarDenunciaRequest {
  denuncianteId: UUID;
  anuncioId: UUID;
  titulo: string;
  corpo: string;
}

export interface DenunciaResponse {
  id: UUID;
  denuncianteId: UUID;
  anuncioId: UUID;
  titulo: string;
  corpo: string;
  statusDenuncia: StatusDenuncia;
  criadoEm: string;
}

// Moderação
export interface ModerarDenunciaRequest {
  novoStatus: StatusDenuncia;
  acaoAnuncio: AcaoModeracaoAnuncio;
  justificativa: string;
}

export interface ModeracaoResponse {
  id: UUID;
  denunciaId: UUID;
  statusAnterior: StatusDenuncia;
  statusNovo: StatusDenuncia;
  acaoAnuncio: AcaoModeracaoAnuncio;
  justificativa: string;
  criadoEm: string;
}
