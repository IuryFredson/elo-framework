import { Search as SearchIcon, MapPin, Star, Filter } from "lucide-react";
import { Button } from "../components/ui/Button";

export default function Search() {
  const mockListings = [
    {
      id: "1",
      title: "Quarto em República Estudantil",
      price: 650,
      host: "Ricardo Lima",
      reputation: 4.8,
      location: "Butantã, SP",
      tags: ["Cozinha Equipada", "Lavanderia"],
    },
    {
      id: "2",
      title: "Estúdio Moderno próximo ao Campus",
      price: 1200,
      host: "Imobiliária Prime",
      reputation: 4.2,
      location: "Vila Mariana, SP",
      tags: ["Portaria 24h", "Academia"],
    },
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <div className="flex flex-col md:flex-row gap-4">
        <div className="flex-1 bg-white p-2 rounded-xl border border-apto-border shadow-sm flex items-center px-4 gap-3">
          <SearchIcon className="text-apto-text-muted" size={20} />
          <input
            type="text"
            placeholder="Buscar por universidade, bairro ou tipo..."
            className="flex-1 bg-transparent border-none focus:ring-0 text-apto-text-main text-sm"
          />
        </div>
        <Button
          variant="secondary"
          className="flex items-center gap-2 font-bold px-5"
        >
          <Filter size={18} />
          Filtros
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {mockListings.map((listing) => (
          <div
            key={listing.id}
            className="bg-white rounded-apto-card border border-apto-border shadow-sm overflow-hidden flex flex-col group cursor-pointer hover:shadow-lg transition-all"
          >
            <div className="aspect-[4/3] bg-gray-100 relative overflow-hidden">
              <img
                src={`https://picsum.photos/seed/apt${listing.id}/600/450`}
                alt="Imóvel"
                className="w-full h-full object-cover transition-transform group-hover:scale-105 duration-500"
                referrerPolicy="no-referrer"
              />
              <div className="absolute top-4 right-4 px-3 py-1.5 bg-white shadow-xl rounded-full text-xs font-bold text-apto-text-main border border-apto-border">
                R$ {listing.price} / mês
              </div>
            </div>
            <div className="p-5 space-y-4">
              <div>
                <h3 className="font-bold text-apto-text-main group-hover:text-apto-primary transition-colors leading-tight">
                  {listing.title}
                </h3>
                <div className="flex items-center gap-1 text-apto-text-muted text-[12px] mt-1.5 font-medium">
                  <MapPin size={14} />
                  <span>{listing.location}</span>
                </div>
              </div>

              <div className="flex items-center justify-between pt-4 border-t border-apto-border">
                <div className="flex items-center gap-3">
                  <div className="w-9 h-9 rounded-xl bg-apto-primary-light flex items-center justify-center text-apto-primary text-xs font-bold">
                    {listing.host[0]}
                  </div>
                  <div>
                    <p className="text-xs font-bold text-apto-text-main leading-none mb-1">
                      {listing.host}
                    </p>
                    <div className="flex items-center gap-1 text-amber-500">
                      <Star size={10} fill="currentColor" />
                      <span className="text-[10px] font-bold">
                        {listing.reputation}
                      </span>
                    </div>
                  </div>
                </div>
                <Button
                  variant="ghost"
                  size="sm"
                  className="text-apto-primary font-bold hover:bg-apto-primary-light"
                >
                  Ver Match
                </Button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
