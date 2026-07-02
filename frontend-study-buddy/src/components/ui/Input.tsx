import { forwardRef } from "react";
import type { InputHTMLAttributes, TextareaHTMLAttributes } from "react";
import { cn } from "../../lib/utils";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  hint?: string;
}

const baseField =
  "w-full h-10 px-3 rounded-apto-ui border border-apto-border bg-white text-apto-text-main placeholder:text-apto-text-muted focus:outline-none focus:ring-2 focus:ring-apto-primary/40 focus:border-apto-primary disabled:opacity-50";

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, hint, className, id, ...props }, ref) => {
    const inputId = id ?? props.name;
    return (
      <div className="space-y-1.5">
        {label && (
          <label
            htmlFor={inputId}
            className="text-sm font-medium text-apto-text-main"
          >
            {label}
          </label>
        )}
        <input
          ref={ref}
          id={inputId}
          className={cn(baseField, error && "border-red-500", className)}
          {...props}
        />
        {hint && !error && (
          <p className="text-xs text-apto-text-muted">{hint}</p>
        )}
        {error && <p className="text-xs text-red-600">{error}</p>}
      </div>
    );
  },
);
Input.displayName = "Input";

interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
  hint?: string;
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ label, error, hint, className, id, ...props }, ref) => {
    const inputId = id ?? props.name;
    return (
      <div className="space-y-1.5">
        {label && (
          <label
            htmlFor={inputId}
            className="text-sm font-medium text-apto-text-main"
          >
            {label}
          </label>
        )}
        <textarea
          ref={ref}
          id={inputId}
          className={cn(
            baseField,
            "h-auto py-2 min-h-[88px] resize-y",
            error && "border-red-500",
            className,
          )}
          {...props}
        />
        {hint && !error && (
          <p className="text-xs text-apto-text-muted">{hint}</p>
        )}
        {error && <p className="text-xs text-red-600">{error}</p>}
      </div>
    );
  },
);
Textarea.displayName = "Textarea";
