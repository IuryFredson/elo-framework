package com.elo.porta;

/**
 * Porta para converter uma entidade do domínio na resposta definida pela instância.
 */
@FunctionalInterface
public interface MapperResposta<T, R> {

	R paraResposta(T entidade);
}
