import { api } from "./client";
import type {
  ModerarDenunciaRequest,
  ModeracaoResponse,
  UUID,
} from "./types";

export const moderacaoApi = {
  moderar: (denunciaId: UUID, body: ModerarDenunciaRequest) =>
    api.patch<ModeracaoResponse>(
      `/moderacoes/denuncias/${denunciaId}`,
      body,
    ),
};
