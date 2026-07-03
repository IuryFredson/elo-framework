package com.studybuddy.service.matching;

import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudyBuddyMatchingPromptBuilderTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final StudyBuddyMatchingPromptBuilder builder = new StudyBuddyMatchingPromptBuilder(objectMapper);

    @Test
    void deveSerializarSomenteDadosAcademicosEIdentificadorDoCandidato() throws Exception {
        Estudante solicitante = estudante("Ana", "ana@teste.com", "MAT-1", perfil("  Gosta de revisoes.  "));
        Estudante candidato = estudante("Bruno", "bruno@teste.com", "MAT-2", perfil(null));

        String prompt = builder.montarUserPrompt(solicitante, List.of(candidato));
        JsonNode raiz = objectMapper.readTree(prompt);
        JsonNode perfilSolicitante = raiz.get("solicitante");
        JsonNode perfilCandidato = raiz.get("candidatos").get(0);

        assertEquals("Computacao", perfilSolicitante.get("curso").asText());
        assertEquals("Gosta de revisoes.", perfilSolicitante.get("descricao").asText());
        assertFalse(perfilSolicitante.has("candidatoId"));
        assertEquals(candidato.getId().toString(), perfilCandidato.get("candidatoId").asText());
        assertFalse(perfilCandidato.has("descricao"));
        assertFalse(prompt.contains("Ana"));
        assertFalse(prompt.contains("ana@teste.com"));
        assertFalse(prompt.contains("MAT-1"));
        assertTrue(builder.montarSystemPrompt().contains("TODOS os candidatos"));
    }

    private Estudante estudante(String nome, String email, String matricula, PerfilAcademico perfil) {
        Estudante estudante = new Estudante();
        estudante.setId(UUID.randomUUID());
        estudante.setNome(nome);
        estudante.setEmail(email);
        estudante.setTelefone("85999999999");
        estudante.setMatricula(matricula);
        estudante.setPerfilAcademico(perfil);
        return estudante;
    }

    private PerfilAcademico perfil(String descricao) {
        PerfilAcademico perfil = new PerfilAcademico();
        perfil.setCurso("Computacao");
        perfil.setDisciplinasInteresse(Set.of("Algoritmos"));
        perfil.setDisponibilidade(Set.of(PeriodoDisponibilidade.NOITE));
        perfil.setObjetivoEstudo(ObjetivoEstudo.PROVA);
        perfil.setNivelConhecimento(NivelConhecimento.INTERMEDIARIO);
        perfil.setModalidadePreferida(ModalidadeEstudo.ONLINE);
        perfil.setDescricao(descricao);
        return perfil;
    }
}
