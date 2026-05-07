import { useContext } from "react";
import { ToastContext, type ToastContextValue } from "./toastContext";

export function useToast(): ToastContextValue {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast precisa de ToastProvider");
  return ctx;
}
