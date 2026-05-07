import { cn } from "../../lib/utils";
import { iniciais } from "../../lib/format";

interface AvatarProps {
  nome: string;
  src?: string | null;
  size?: "sm" | "md" | "lg" | "xl";
  className?: string;
}

const sizes = {
  sm: "w-8 h-8 text-xs",
  md: "w-10 h-10 text-sm",
  lg: "w-16 h-16 text-lg",
  xl: "w-24 h-24 text-2xl",
};

export function Avatar({ nome, src, size = "md", className }: AvatarProps) {
  if (src) {
    return (
      <img
        src={src}
        alt={nome}
        referrerPolicy="no-referrer"
        className={cn(
          "rounded-2xl object-cover bg-apto-primary-light",
          sizes[size],
          className,
        )}
      />
    );
  }
  return (
    <div
      className={cn(
        "rounded-2xl bg-apto-primary-light text-apto-primary font-bold flex items-center justify-center select-none",
        sizes[size],
        className,
      )}
      aria-label={nome}
    >
      {iniciais(nome)}
    </div>
  );
}
