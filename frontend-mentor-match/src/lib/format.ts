export function iniciais(nome: string): string {
  return nome.trim().split(/\s+/).slice(0, 2).map((parte) => parte[0]?.toUpperCase() ?? "").join("");
}

export function formatDate(valor: string): string {
  return new Intl.DateTimeFormat("pt-BR").format(new Date(valor));
}
