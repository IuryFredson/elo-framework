package com.apto.service.matchmaking;

import com.apto.model.entity.UsuarioUniversitario;

public interface CompatibilidadeStrategy {

    ResultadoCompatibilidade calcular(UsuarioUniversitario solicitante, UsuarioUniversitario candidato);

    boolean preferenciaGeneroCompativel(UsuarioUniversitario solicitante, UsuarioUniversitario candidato);
}
