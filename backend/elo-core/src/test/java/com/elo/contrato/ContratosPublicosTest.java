package com.elo.contrato;

import com.elo.denuncia.CriterioDenuncia;
import com.elo.denuncia.StatusDenuncia;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.elo.moderacao.AcaoModeracaoOferta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContratosPublicosTest {

	@Test
	void devePreservarEstadosFixosDaManifestacaoDeInteresse() {
		assertArrayEquals(
			new String[]{"PENDENTE", "ACEITA", "RECUSADA", "CANCELADA"},
			nomes(StatusManifestacaoInteresse.values()));
	}

	@Test
	void devePreservarEstadosFixosDaDenuncia() {
		assertArrayEquals(
			new String[]{"PENDENTE", "EM_ANALISE", "IMPROCEDENTE", "PROCEDENTE", "ARQUIVADA"},
			nomes(StatusDenuncia.values()));
	}

	@Test
	void deveExporAcoesGenericasDeModeracao() {
		assertArrayEquals(
			new String[]{"NENHUMA", "PAUSAR", "ENCERRAR"},
			nomes(AcaoModeracaoOferta.values()));
	}

	@Test
	void instanciaDeveFornecerCodigoDoCriterioDeDenuncia() {
		CriterioDenuncia criterio = () -> "CRITERIO_DA_INSTANCIA";

		assertEquals("CRITERIO_DA_INSTANCIA", criterio.codigo());
	}

	private String[] nomes(Enum<?>[] valores) {
		return java.util.Arrays.stream(valores)
			.map(Enum::name)
			.toArray(String[]::new);
	}
}
