package com.elo.compatibilidade.llm;

import com.elo.compatibilidade.ResultadoCompatibilidade;

import java.util.Map;
import java.util.UUID;

public interface CompatibilidadeLlmParser {

    Map<UUID, ResultadoCompatibilidade> parse(String conteudoJson);
}
