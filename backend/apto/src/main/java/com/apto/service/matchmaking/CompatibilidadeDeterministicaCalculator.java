package com.apto.service.matchmaking;

import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.PreferenciaGeneroConvivencia;
import com.apto.model.enums.RotinaEstudos;
import org.springframework.stereotype.Component;

@Component
public class CompatibilidadeDeterministicaCalculator implements CompatibilidadeStrategy {

    private static final String JUSTIFICATIVA_PADRAO =
            "Compatibilidade calculada por critérios determinísticos.";

    @Override
    public ResultadoCompatibilidade calcular(UsuarioUniversitario solicitante, UsuarioUniversitario candidato) {
        PerfilConvivencia perfilSolicitante = solicitante.getPerfilConvivencia();
        PerfilConvivencia perfilCandidato = candidato.getPerfilConvivencia();

        if (perfilSolicitante == null || perfilCandidato == null) {
            return new ResultadoCompatibilidade(
                    0,
                    JUSTIFICATIVA_PADRAO,
                    OrigemCompatibilidade.FALLBACK_DETERMINISTICO
            );
        }

        int soma = 0;
        int criterios = 0;

        soma += scoreOrdinal(perfilSolicitante.getHorarioSono(), perfilCandidato.getHorarioSono());
        criterios++;

        soma += scoreOrdinal(
                perfilSolicitante.getNivelBarulhoAceitavel(),
                perfilCandidato.getNivelBarulhoAceitavel()
        );
        criterios++;

        soma += scoreOrdinal(
                perfilSolicitante.getNivelOrganizacao(),
                perfilCandidato.getNivelOrganizacao()
        );
        criterios++;

        soma += scoreOrdinal(
                perfilSolicitante.getFrequenciaVisitas(),
                perfilCandidato.getFrequenciaVisitas()
        );
        criterios++;

        soma += scoreRotinaEstudos(
                perfilSolicitante.getRotinaEstudos(),
                perfilCandidato.getRotinaEstudos()
        );
        criterios++;

        soma += scoreBooleano(
                perfilSolicitante.getFumante(),
                perfilCandidato.getFumante()
        );
        criterios++;

        soma += scoreBooleano(
                perfilSolicitante.getConsomeAlcool(),
                perfilCandidato.getConsomeAlcool()
        );
        criterios++;

        soma += scoreBooleano(
                perfilSolicitante.getAceitaAnimais(),
                perfilCandidato.getAceitaAnimais()
        );
        criterios++;

        int percentual = criterios == 0 ? 0 : Math.round((float) soma / criterios);

        if (aplicarPenalidadeACombinar(perfilSolicitante, perfilCandidato)) {
            percentual -= 5;
        }

        percentual = clamp(percentual, 0, 100);

        return new ResultadoCompatibilidade(
                percentual,
                JUSTIFICATIVA_PADRAO,
                OrigemCompatibilidade.FALLBACK_DETERMINISTICO
        );
    }

    @Override
    public boolean preferenciaGeneroCompativel(UsuarioUniversitario a, UsuarioUniversitario b) {
        if (a == null || b == null || a.getGenero() == null || b.getGenero() == null) {
            return false;
        }

        PerfilConvivencia perfilA = a.getPerfilConvivencia();
        PerfilConvivencia perfilB = b.getPerfilConvivencia();

        if (perfilA == null || perfilB == null) {
            return false;
        }

        return aceitaGenero(perfilA.getPreferenciaGeneroConvivencia(), b)
                && aceitaGenero(perfilB.getPreferenciaGeneroConvivencia(), a);
    }

    private boolean aceitaGenero(PreferenciaGeneroConvivencia preferencia, UsuarioUniversitario usuario) {
        if (preferencia == null || usuario == null || usuario.getGenero() == null) {
            return false;
        }

        return switch (preferencia) {
            case SEM_PREFERENCIA, A_COMBINAR -> true;
            case APENAS_MULHERES -> usuario.getGenero().name().equals("FEMININO");
            case APENAS_HOMENS -> usuario.getGenero().name().equals("MASCULINO");
        };
    }

    private boolean aplicarPenalidadeACombinar(PerfilConvivencia perfilA, PerfilConvivencia perfilB) {
        return perfilA.getPreferenciaGeneroConvivencia() == PreferenciaGeneroConvivencia.A_COMBINAR
                || perfilB.getPreferenciaGeneroConvivencia() == PreferenciaGeneroConvivencia.A_COMBINAR;
    }

    private int scoreBooleano(Boolean valorA, Boolean valorB) {
        if (valorA == null || valorB == null) {
            return 0;
        }
        return valorA.equals(valorB) ? 100 : 0;
    }

    private <E extends Enum<E>> int scoreOrdinal(E valorA, E valorB) {
        if (valorA == null || valorB == null) {
            return 0;
        }

        int distancia = Math.abs(valorA.ordinal() - valorB.ordinal());

        return switch (distancia) {
            case 0 -> 100;
            case 1 -> 50;
            default -> 0;
        };
    }

    private int scoreRotinaEstudos(RotinaEstudos rotinaA, RotinaEstudos rotinaB) {
        if (rotinaA == null || rotinaB == null) {
            return 0;
        }

        if (rotinaA == rotinaB) {
            return 100;
        }

        if (rotinaA == RotinaEstudos.MISTA || rotinaB == RotinaEstudos.MISTA) {
            return 75;
        }

        return 25;
    }

    private int clamp(int valor, int min, int max) {
        return Math.max(min, Math.min(max, valor));
    }
}
