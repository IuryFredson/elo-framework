package com.elo.porta;

import java.util.List;
import java.util.Optional;

/**
 * Porta mínima de persistência usada pelos fluxos do framework.
 */
public interface RepositorioCrud<T, ID> {

	T salvar(T entidade);

	Optional<T> buscarPorId(ID id);

	List<T> listarTodos();

	void excluir(T entidade);
}
