package com.apto.dto.request;

import com.apto.model.enums.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AtualizarPerfilRequestDTO(

        @NotBlank String nome,
        @Email @NotBlank String email,
        @Email @NotBlank String emailInstitucional,
        @NotBlank String telefone,
        @NotBlank String curso,

        @NotNull LocalDate dataNascimento,
        @NotNull Genero genero,

        @NotNull HorarioSono horarioSono,
        @NotNull NivelBarulho nivelBarulhoAceitavel,
        @NotNull FrequenciaVisitas frequenciaVisitas,
        @NotNull NivelOrganizacao nivelOrganizacao,
        @NotNull RotinaEstudos rotinaEstudos,

        @NotNull Boolean consomeAlcool,
        @NotNull Boolean fumante,
        @NotNull Boolean aceitaAnimais,

        @NotNull PreferenciaGeneroConvivencia preferenciaGeneroConvivencia,

        String descricaoLivre
) {}