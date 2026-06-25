package com.apto.repository;

import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.apto.model.entity.Usuario;
import com.apto.model.enums.StatusDenuncia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DenunciaRepository extends JpaRepository<Denuncia, UUID> {
    List<Denuncia> findByAnuncio(Anuncio anuncio);
    List<Denuncia> findByDenunciante(Usuario denunciante);
    List<Denuncia> findByStatusDenuncia(StatusDenuncia statusDenuncia);
}
