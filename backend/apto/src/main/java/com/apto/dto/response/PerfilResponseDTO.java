package com.apto.dto.response;

import com.apto.model.enums.*;

import java.time.LocalDate;
import java.util.UUID;

public record PerfilResponseDTO(

        UUID idUsuario,
        String nome,
        String email,
        String emailInstitucional,
        String telefone,
        String curso,
        LocalDate dataNascimento,
        Genero genero,

        HorarioSono horarioSono,
        NivelBarulho nivelBarulhoAceitavel,
        FrequenciaVisitas frequenciaVisitas,
        NivelOrganizacao nivelOrganizacao,
        RotinaEstudos rotinaEstudos,
        Boolean consomeAlcool,
        Boolean fumante,
        Boolean aceitaAnimais,
        PreferenciaGeneroConvivencia preferenciaGeneroConvivencia,
        String descricaoLivre

) {}