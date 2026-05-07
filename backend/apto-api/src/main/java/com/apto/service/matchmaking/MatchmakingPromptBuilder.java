package com.apto.service.matchmaking;

import com.apto.model.entity.UsuarioUniversitario;
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
public class MatchmakingPromptBuilder {

    private final ObjectMapper objectMapper;

    public String montarSystemPrompt() {
        return """
            Você é um assessor de compatibilidade entre potenciais colegas de moradia estudantil.

            Você receberá um JSON com:
            - "solicitante": perfil do usuário pedindo o match.
            - "candidatos": lista de outros usuários, cada um com um "candidatoId".

            Cada perfil traz campos estruturados sobre convivência (horário de sono,
            nível de barulho aceitável, frequência de visitas, organização, rotina de
            estudos, álcool, fumo, animais, preferência de gênero) e, quando disponível,
            um campo "descricaoLivre" com texto escrito pelo próprio usuário.

            Use os campos estruturados como BASE da análise. Use "descricaoLivre" como
            contexto qualitativo que pode reforçar ou matizar a leitura — nunca para
            substituir os dados objetivos. Se a descrição livre estiver ausente, for
            irrelevante ou ofensiva, ignore-a e baseie-se apenas nos campos estruturados.

            Retorne APENAS JSON neste formato, ordenado por "percentual" em ordem
            decrescente, incluindo TODOS os candidatos recebidos:

            {"matches":[{"candidatoId":"<uuid>","percentual":<0-100>,"justificativa":"<texto>"}]}

            Regras da justificativa:
            - 2 a 4 frases curtas em pt-BR, entre 250 e 500 caracteres.
            - Estrutura sugerida: (1) o que aproxima os dois perfis, (2) onde podem
              surgir atritos, (3) uma orientação prática curta sobre como conviver
              bem dadas essas semelhanças e diferenças.
            - Cite atributos concretos (ex.: "ambos preferem ambientes silenciosos à
              noite") em vez de termos genéricos como "perfis parecidos".
            - Tom respeitoso, descritivo e útil. Não invente dados que não estejam
              no JSON recebido.
            - Não cite nomes — você só conhece "candidatoId".

            Regras gerais:
            - Inclua TODOS os candidatos recebidos, sem exceção, mesmo os muito incompatíveis.
            - "percentual" entre 0 e 100, coerente com o conteúdo da justificativa.
            - Saída deve ser JSON válido, sem nenhum texto fora do JSON.
            - NUNCA envie o ID ou outra informação sensível do usuário na resposta.
            """;
    }

    public String montarUserPrompt(UsuarioUniversitario solicitante, List<UsuarioUniversitario> candidatos) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("solicitante", extrairCamposDeterministicos(solicitante, false));

            List<Map<String, Object>> listaCandidatos = candidatos.stream()
                    .map(c -> extrairCamposDeterministicos(c, true))
                    .toList();

            payload.put("candidatos", listaCandidatos);

            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Erro ao serializar prompt de matchmaking", e);
            throw new RuntimeException("Falha ao construir prompt para LLM", e);
        }
    }

    private Map<String, Object> extrairCamposDeterministicos(UsuarioUniversitario usuario, boolean incluirCandidatoId) {
        var perfil = usuario.getPerfilConvivencia();
        Map<String, Object> campos = new LinkedHashMap<>();

        if (incluirCandidatoId) {
            campos.put("candidatoId", usuario.getId().toString());
        }

        campos.put("genero", usuario.getGenero());
        campos.put("horarioSono", perfil.getHorarioSono());
        campos.put("nivelBarulhoAceitavel", perfil.getNivelBarulhoAceitavel());
        campos.put("frequenciaVisitas", perfil.getFrequenciaVisitas());
        campos.put("nivelOrganizacao", perfil.getNivelOrganizacao());
        campos.put("rotinaEstudos", perfil.getRotinaEstudos());
        campos.put("consomeAlcool", perfil.getConsomeAlcool());
        campos.put("fumante", perfil.getFumante());
        campos.put("aceitaAnimais", perfil.getAceitaAnimais());
        campos.put("preferenciaGeneroConvivencia", perfil.getPreferenciaGeneroConvivencia());

        String descricao = perfil.getDescricaoLivre();
        if (descricao != null && !descricao.isBlank()) {
            campos.put("descricaoLivre", descricao.strip());
        }

        return campos;
    }
}