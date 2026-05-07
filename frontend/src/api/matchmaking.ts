import { api } from "./client";
import type { MatchmakingResponse, UUID } from "./types";

export const matchmakingApi = {
  buscarColegas: (solicitanteId: UUID, topN = 10) =>
    api.get<MatchmakingResponse>(
      `/matchmaking/colegas/${solicitanteId}`,
      { topN },
    ),
};
