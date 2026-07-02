package com.apto.dto.response;

import java.util.List;

public record PaginaResponseDTO<T>(
        List<T> conteudo,
        int paginaAtual,
        int totalPaginas,
        long totalElementos,
        int tamanhoPagina
) {}
