import { api } from "./client";
import type { ReputacaoLocadorResponse, UUID } from "./types";

export const reputacaoApi = {
  doLocador: (locadorId: UUID) =>
    api.get<ReputacaoLocadorResponse>(`/reputacao/locador/${locadorId}`),
};
