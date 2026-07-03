package com.studybuddy.service.matching;

import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyBuddyMatchingPromptBuilder {

    private final ObjectMapper objectMapper;

    public String montarSystemPrompt() {
        return """
            Voce e um assessor de compatibilidade entre potenciais parceiros de estudo.

            Voce recebera um JSON com:
            - "solicitante": perfil academico do estudante pedindo o match.
            - "candidatos": lista de outros estudantes, cada um com um "candidatoId".

            Cada perfil traz campos estruturados sobre curso, disciplinas de interesse,
            disponibilidade, objetivo de estudo, nivel de conhecimento e modalidade
            preferida. Quando disponivel, o campo "descricao" contem texto escrito pelo
            proprio estudante.

            Use os campos estruturados como base da analise. Use "descricao" apenas como
            contexto qualitativo; ignore-a se estiver ausente, irrelevante ou ofensiva.

            Retorne APENAS JSON neste formato, ordenado por "percentual" em ordem
            decrescente e incluindo TODOS os candidatos recebidos:

            {"matches":[{"candidatoId":"<uuid>","percentual":<0-100>,"justificativa":"<texto>"}]}

            Regras da justificativa:
            - 2 a 4 frases curtas em pt-BR, entre 250 e 500 caracteres.
            - Explique afinidades academicas, possiveis dificuldades e uma orientacao
              pratica para estudarem juntos.
            - Cite atributos presentes no JSON e nao invente informacoes.
            - Nao cite nomes nem outros dados pessoais.

            Regras gerais:
            - Inclua todos os candidatos, mesmo os pouco compativeis.
            - Use percentual entre 0 e 100, coerente com a justificativa.
            - A saida deve ser JSON valido, sem texto fora do JSON.
            - O candidatoId deve ser reproduzido exatamente como recebido.
            """;
    }

    public String montarUserPrompt(Estudante solicitante, List<Estudante> candidatos) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("solicitante", extrairPerfil(solicitante, false));
            payload.put("candidatos", candidatos.stream()
                    .map(candidato -> extrairPerfil(candidato, true))
                    .toList());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception exception) {
            log.error("Erro ao serializar prompt de matching do Study Buddy", exception);
            throw new RuntimeException("Falha ao construir prompt para LLM", exception);
        }
    }

    private Map<String, Object> extrairPerfil(Estudante estudante, boolean incluirCandidatoId) {
        PerfilAcademico perfil = estudante.getPerfilAcademico();
        Map<String, Object> campos = new LinkedHashMap<>();

        if (incluirCandidatoId) {
            campos.put("candidatoId", estudante.getId().toString());
        }

        campos.put("curso", perfil.getCurso());
        campos.put("disciplinasInteresse", perfil.getDisciplinasInteresse());
        campos.put("disponibilidade", perfil.getDisponibilidade());
        campos.put("objetivoEstudo", perfil.getObjetivoEstudo());
        campos.put("nivelConhecimento", perfil.getNivelConhecimento());
        campos.put("modalidadePreferida", perfil.getModalidadePreferida());

        String descricao = perfil.getDescricao();
        if (descricao != null && !descricao.isBlank()) {
            campos.put("descricao", descricao.strip());
        }

        return campos;
    }
}
