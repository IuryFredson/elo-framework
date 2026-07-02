import { ShieldAlert, Flag, Clock3 } from "lucide-react";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";

const FILA_PREPARADA = [
  {
    id: "mock-1",
    titulo: "Grupo com descrição potencialmente inadequada",
    status: "Preparado",
    detalhe: "Espaço reservado para revisão manual da moderação quando o backend existir.",
  },
  {
    id: "mock-2",
    titulo: "Sinalização de comportamento em grupo",
    status: "Preparado",
    detalhe: "A UI já acomoda denúncias, triagem e decisão, sem ligar regra real agora.",
  },
];

export default function Moderacao() {
  return (
    <div className="max-w-5xl mx-auto px-4 py-8 pb-20 space-y-6">
      <header className="flex items-center gap-3">
        <ShieldAlert className="text-apto-primary" size={28} />
        <div>
          <h1 className="text-2xl font-bold text-apto-text-main">Moderação preparada</h1>
          <p className="text-sm text-apto-text-muted">
            Estrutura visual pronta para denúncia e análise, ainda sem backend funcional.
          </p>
        </div>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard label="Entradas previstas" value="3" descricao="menu, botão de reporte e fila" />
        <StatCard label="Estados visuais" value="4" descricao="pendente, triagem, decisão e arquivado" />
        <StatCard label="Backend conectado" value="0" descricao="etapa futura" />
      </div>

      <section className="bg-white rounded-apto-section border border-apto-border p-6 space-y-4">
        <div className="flex items-center justify-between flex-wrap gap-3">
          <div>
            <h2 className="text-lg font-bold text-apto-text-main">Fila de análise</h2>
            <p className="text-sm text-apto-text-muted">
              Os cards abaixo demonstram como a navegação e os estados foram preparados.
            </p>
          </div>
          <Button variant="secondary" size="sm" disabled>
            Ações futuras
          </Button>
        </div>

        <div className="space-y-3">
          {FILA_PREPARADA.map((item) => (
            <div key={item.id} className="border border-apto-border rounded-apto-ui p-4">
              <div className="flex items-start justify-between gap-3 mb-2">
                <div>
                  <h3 className="font-bold text-apto-text-main">{item.titulo}</h3>
                  <p className="text-sm text-apto-text-muted">{item.detalhe}</p>
                </div>
                <Badge tone="warning">{item.status}</Badge>
              </div>
              <div className="flex items-center gap-4 text-xs text-apto-text-muted">
                <span className="inline-flex items-center gap-1">
                  <Flag size={12} />
                  Denúncia preparada na UI
                </span>
                <span className="inline-flex items-center gap-1">
                  <Clock3 size={12} />
                  Sem integração operacional ainda
                </span>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

function StatCard({ label, value, descricao }: { label: string; value: string; descricao: string }) {
  return (
    <div className="bg-white rounded-apto-section border border-apto-border p-5">
      <p className="section-title">{label}</p>
      <p className="text-3xl font-bold text-apto-text-main mt-2">{value}</p>
      <p className="text-sm text-apto-text-muted mt-2">{descricao}</p>
    </div>
  );
}
