import { Star } from "lucide-react";
import { cn } from "../../lib/utils";

interface StarRatingProps {
  value: number;
  onChange?: (value: number) => void;
  size?: number;
  readOnly?: boolean;
  label?: string;
}

export function StarRating({
  value,
  onChange,
  size = 20,
  readOnly,
  label,
}: StarRatingProps) {
  const interactive = !readOnly && !!onChange;
  return (
    <div className="space-y-1">
      {label && (
        <span className="text-sm font-medium text-apto-text-main">{label}</span>
      )}
      <div className="flex items-center gap-1">
        {[1, 2, 3, 4, 5].map((n) => {
          const filled = n <= value;
          return (
            <button
              key={n}
              type="button"
              disabled={!interactive}
              onClick={() => onChange?.(n)}
              className={cn(
                "transition-transform",
                interactive && "hover:scale-110 cursor-pointer",
                !interactive && "cursor-default",
              )}
              aria-label={`${n} de 5`}
            >
              <Star
                size={size}
                className={
                  filled
                    ? "text-amber-400 fill-amber-400"
                    : "text-apto-border fill-transparent"
                }
              />
            </button>
          );
        })}
        <span className="ml-2 text-sm font-bold text-apto-text-main">
          {value > 0 ? value.toFixed(1) : "—"}
        </span>
      </div>
    </div>
  );
}
