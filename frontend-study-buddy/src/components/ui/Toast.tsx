import { useCallback, useState } from "react";
import type { ReactNode } from "react";
import { CheckCircle2, AlertTriangle, XCircle } from "lucide-react";
import { cn } from "../../lib/utils";
import { ToastContext, type ToastTone } from "./toastContext";

interface ToastItem {
  id: number;
  message: string;
  tone: ToastTone;
}

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const show = useCallback((message: string, tone: ToastTone = "success") => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, tone }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 3500);
  }, []);

  return (
    <ToastContext.Provider value={{ show }}>
      {children}
      <div className="fixed bottom-6 right-6 z-[200] flex flex-col gap-2">
        {toasts.map((t) => {
          const Icon =
            t.tone === "success"
              ? CheckCircle2
              : t.tone === "error"
                ? XCircle
                : AlertTriangle;
          return (
            <div
              key={t.id}
              className={cn(
                "flex items-center gap-3 bg-white shadow-lg rounded-apto-ui border px-4 py-3 min-w-[260px] max-w-sm",
                t.tone === "success" && "border-emerald-200",
                t.tone === "error" && "border-red-200",
                t.tone === "info" && "border-sky-200",
              )}
            >
              <Icon
                size={20}
                className={
                  t.tone === "success"
                    ? "text-emerald-600"
                    : t.tone === "error"
                      ? "text-red-600"
                      : "text-sky-600"
                }
              />
              <span className="text-sm text-apto-text-main">{t.message}</span>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}
