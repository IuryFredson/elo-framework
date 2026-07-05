package com.elo.web.dto;

import java.util.List;

public record PaginaResponseDTO<T>(
        List<T> conteudo,
        int pagina,
        int totalPaginas,
        long totalElementos,
        int tamanho
) {
}
