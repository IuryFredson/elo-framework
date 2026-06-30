package com.elo.compatibilidade;

import com.elo.perfil.Perfil;
import com.elo.usuario.Usuario;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProvedorCompatibilidadeLlm<U extends Usuario, P extends Perfil> {

    Map<UUID, ResultadoCompatibilidade> calcular(U solicitante, List<U> candidatos);
}
