import { Brain, CheckCircle2, XCircle, Info } from "lucide-react";
import { Button } from "../components/ui/Button";

export default function Matches() {
  return (
    <div className="max-w-4xl mx-auto px-4 py-12 space-y-8">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold text-apto-text-main pb-2">
            Matchmaking Inteligente
          </h1>
          <p className="text-apto-text-muted">
            Sugestões baseadas nos seus hábitos de convivência e preferências.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-[1fr_320px] gap-8">
        <div className="space-y-6">
          <div className="bg-white p-6 rounded-apto-card border border-apto-border shadow-sm space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-apto-primary-light text-apto-primary rounded-xl flex items-center justify-center">
                  <Brain size={24} />
                </div>
                <div>
                  <h3 className="font-bold text-apto-text-main">
                    Análise do Assistente
                  </h3>
                  <p className="text-[10px] text-apto-primary font-bold uppercase tracking-widest">
                    Altíssima Compatibilidade
                  </p>
                </div>
              </div>
              <div className="text-2xl font-black text-apto-primary">98%</div>
            </div>

            <div className="bg-red-50 p-5 rounded-xl border-l-4 border-red-500">
              <p className="text-sm text-red-900 leading-relaxed font-medium">
                "Sua preferência por ambientes silenciosos e foco total em
                estudos de pós-graduação converge perfeitamente com a 'República
                Master'. Os moradores atuais são todos mestrandos com horários
                rígidos de sono (22h - 06h)."
              </p>
            </div>

            <div className="grid grid-cols-2 gap-6 pt-2">
              <div className="space-y-3">
                <p className="text-[12px] font-bold text-apto-text-muted uppercase tracking-wider flex items-center gap-1.5">
                  <CheckCircle2 size={14} className="text-apto-success" />{" "}
                  Convergências
                </p>
                <ul className="text-xs space-y-2 text-apto-text-main">
                  <li className="flex gap-2">
                    <span>•</span> Horário de silêncio rigoroso
                  </li>
                  <li className="flex gap-2">
                    <span>•</span> Divisão igualitária de limpeza
                  </li>
                  <li className="flex gap-2">
                    <span>•</span> Proximidade ao bloco de Eng.
                  </li>
                </ul>
              </div>
              <div className="space-y-3">
                <p className="text-[12px] font-bold text-apto-text-muted uppercase tracking-wider flex items-center gap-1.5">
                  <XCircle size={14} className="text-amber-500" /> Divergências
                </p>
                <ul className="text-xs space-y-2 text-apto-text-main">
                  <li className="flex gap-2">
                    <span>•</span> Não possui vaga de garagem
                  </li>
                  <li className="flex gap-2">
                    <span>•</span> Cozinha pequena
                  </li>
                </ul>
              </div>
            </div>

            <Button className="w-full mt-4 font-bold">
              Ver Imóvel & Contatar
            </Button>
          </div>

          <div className="bg-apto-primary-light p-4 rounded-xl flex gap-3 border border-apto-primary/10">
            <Info className="text-apto-primary shrink-0" size={20} />
            <p className="text-sm text-apto-primary font-medium">
              <strong>Dica:</strong> Manter seu perfil de hábitos atualizado
              aumenta em até 3x a chance de encontrar a moradia perfeita no
              primeiro mês.
            </p>
          </div>
        </div>

        <div className="bg-white rounded-apto-card border border-apto-border shadow-sm overflow-hidden flex flex-col h-fit">
          <div className="aspect-square bg-gray-100 relative">
            <img
              src="https://picsum.photos/seed/dorm1/800/600"
              alt="Quarto"
              className="w-full h-full object-cover"
              referrerPolicy="no-referrer"
            />
            <div className="absolute top-4 right-4 px-3 py-1.5 bg-white shadow-xl rounded-full text-xs font-bold text-apto-text-main border border-apto-border">
              R$ 850 / mês
            </div>
          </div>
          <div className="p-5 space-y-4">
            <div>
              <h3 className="text-lg font-bold text-apto-text-main leading-tight">
                República Master
              </h3>
              <p className="text-apto-text-muted text-[12px] mt-1">
                Vila Mariana, São Paulo
              </p>
            </div>
            <div className="flex flex-wrap gap-2">
              {["Suíte", "Mobiliado", "Wi-fi"].map((tag) => (
                <span
                  key={tag}
                  className="px-2 py-1 bg-apto-bg text-apto-text-muted text-[10px] font-bold uppercase rounded border border-apto-border"
                >
                  {tag}
                </span>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
