import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { BookOpen, Mail, Phone, Power, School, ShieldAlert } from "lucide-react";
import { Avatar } from "../../components/ui/Avatar";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { estudantesApi, perfilAcademicoApi } from "../../api/studyBuddy";
import { useSessaoObrigatoria } from "../../auth/useAuth";
import { ApiError } from "../../api/client";
import { formatarDisciplinas, modalidadeEstudoLabel, nivelConhecimentoLabel, objetivoEstudoLabel, periodoDisponibilidadeLabel } from "../../lib/studyBuddyFormat";
import type { EstudanteResponse, PerfilAcademicoResponse } from "../../study-buddy/types";

export default function Profile() {
  const sessao = useSessaoObrigatoria();
  const [estudante, setEstudante] = useState<EstudanteResponse | null>(null);
  const [perfil, setPerfil] = useState<PerfilAcademicoResponse | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [alterandoStatus, setAlterandoStatus] = useState(false);

  useEffect(() => {
    Promise.all([
      estudantesApi.obter(sessao.id),
      perfilAcademicoApi.obter(sessao.id).catch((error: unknown) => {
        if (error instanceof ApiError && error.status === 404) {
          return null;
        }
        throw error;
      }),
    ])
      .then(([dadosEstudante, dadosPerfil]) => {
        setEstudante(dadosEstudante);
        setPerfil(dadosPerfil);
      })
      .catch((error: unknown) => {
        setErro(error instanceof ApiError ? error.message : "Erro ao carregar perfil.");
      })
      .finally(() => setCarregando(false));
  }, [sessao.id]);

  async function alternarStatus() {
    if (!estudante) return;
    setAlterandoStatus(true);
    try {
      const atualizado = await estudantesApi.alterarStatus(estudante.id, {
        ativo: !estudante.ativo,
      });
      setEstudante(atualizado);
    } catch (error: unknown) {
      setErro(error instanceof ApiError ? error.message : "Erro ao alterar status.");
    } finally {
      setAlterandoStatus(false);
    }
  }

  if (carregando) {
    return <div className="max-w-4xl mx-auto px-4 py-12 text-apto-text-muted">Carregando perfil...</div>;
  }

  if (erro || !estudante) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12">
        <div className="p-4 rounded-apto-ui bg-red-50 border border-red-200 text-sm text-red-700">
          {erro ?? "Estudante não encontrado."}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-12 space-y-8 pb-20">
      <div className="flex flex-col md:flex-row gap-8 items-start">
        <div className="w-full md:w-1/3 space-y-6">
          <div className="bg-white p-6 rounded-apto-section border border-apto-border shadow-sm text-center">
            <Avatar nome={estudante.nome} size="xl" className="mx-auto mb-4" />
            <h2 className="text-xl font-bold text-apto-text-main">{estudante.nome}</h2>
            <p className="text-sm text-apto-text-muted">{estudante.instituicao}</p>
            <div className="mt-3 flex justify-center">
              <Badge tone={estudante.ativo ? "success" : "warning"}>
                {estudante.ativo ? "Ativo" : "Inativo"}
              </Badge>
            </div>

            <div className="space-y-3 mt-6">
              <Link to="/perfil/academico">
                <Button variant="secondary" className="w-full justify-center gap-2">
                  <BookOpen size={16} />
                  Editar perfil acadêmico
                </Button>
              </Link>
              <Button
                variant="ghost"
                className="w-full justify-center gap-2"
                onClick={alternarStatus}
                disabled={alterandoStatus}
              >
                <Power size={16} />
                {alterandoStatus
                  ? "Atualizando..."
                  : estudante.ativo
                    ? "Desativar estudante"
                    : "Reativar estudante"}
              </Button>
            </div>
          </div>

          <div className="bg-white p-6 rounded-apto-section border border-apto-border shadow-sm space-y-3">
            <p className="section-title">Contato</p>
            <div className="flex items-center gap-2 text-sm text-apto-text-main">
              <Mail size={14} className="text-apto-text-muted" />
              <span className="truncate">{estudante.email}</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-apto-text-main">
              <Phone size={14} className="text-apto-text-muted" />
              <span>{estudante.telefone || "Não informado"}</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-apto-text-main">
              <School size={14} className="text-apto-text-muted" />
              <span>{estudante.matricula}</span>
            </div>
          </div>
        </div>

        <div className="flex-1 space-y-6">
          {!perfil ? (
            <section className="bg-white p-8 rounded-apto-section border border-apto-border shadow-sm space-y-4">
              <h3 className="text-2xl font-bold text-apto-text-main">Perfil acadêmico pendente</h3>
              <p className="text-apto-text-muted">
                Complete seu perfil para habilitar o matching e enriquecer sua participação
                nos grupos de estudo.
              </p>
              <Link to="/perfil/academico">
                <Button>Preencher perfil acadêmico</Button>
              </Link>
            </section>
          ) : (
            <section className="bg-white p-8 rounded-apto-section border border-apto-border shadow-sm space-y-6">
              <div>
                <h3 className="text-2xl font-bold text-apto-text-main">Perfil acadêmico</h3>
                <p className="text-apto-text-muted leading-relaxed mt-2">
                  Dados usados para matchmaking, compatibilidade e montagem de grupos.
                </p>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <InfoCard label="Curso" value={perfil.curso} />
                <InfoCard label="Objetivo" value={objetivoEstudoLabel[perfil.objetivoEstudo]} />
                <InfoCard label="Nível" value={nivelConhecimentoLabel[perfil.nivelConhecimento]} />
                <InfoCard label="Modalidade" value={modalidadeEstudoLabel[perfil.modalidadePreferida]} />
              </div>

              <div className="space-y-3">
                <p className="section-title">Disciplinas de interesse</p>
                <p className="text-sm text-apto-text-main">{formatarDisciplinas(perfil.disciplinasInteresse)}</p>
              </div>

              <div className="space-y-3">
                <p className="section-title">Disponibilidade</p>
                <div className="flex flex-wrap gap-2">
                  {perfil.disponibilidade.map((item) => (
                    <Badge key={item} tone="info">
                      {periodoDisponibilidadeLabel[item]}
                    </Badge>
                  ))}
                </div>
              </div>

              {perfil.descricao && (
                <div className="pt-6 border-t border-apto-border space-y-3">
                  <h4 className="font-bold text-apto-text-main uppercase text-[12px] tracking-wider">
                    Observações
                  </h4>
                  <p className="text-apto-text-muted leading-relaxed italic border-l-4 border-apto-border pl-4">
                    "{perfil.descricao}"
                  </p>
                </div>
              )}
            </section>
          )}

          <section className="bg-white p-6 rounded-apto-section border border-apto-border shadow-sm space-y-3">
            <div className="flex items-center gap-2 text-apto-primary">
              <ShieldAlert size={16} />
              <p className="font-semibold text-apto-text-main">Preparação para moderação</p>
            </div>
            <p className="text-sm text-apto-text-muted">
              O app já reserva navegação e estados visuais para denúncia e moderação,
              mas a operação concreta ainda depende das próximas etapas do projeto.
            </p>
          </section>
        </div>
      </div>
    </div>
  );
}

function InfoCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="p-4 rounded-xl border border-apto-border bg-apto-bg/50 space-y-1">
      <span className="text-[10px] font-bold uppercase tracking-wider text-apto-text-muted">
        {label}
      </span>
      <p className="font-bold text-apto-text-main">{value}</p>
    </div>
  );
}
