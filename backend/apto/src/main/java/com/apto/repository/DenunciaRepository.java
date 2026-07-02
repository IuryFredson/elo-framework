package com.apto.repository;

import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.elo.usuario.Usuario;
import com.elo.denuncia.StatusDenuncia;
import com.elo.persistencia.RepositorioBase;

import java.util.List;
import java.util.UUID;

public interface DenunciaRepository extends RepositorioBase<Denuncia, UUID> {
    List<Denuncia> findByAnuncio(Anuncio anuncio);
    List<Denuncia> findByDenunciante(Usuario denunciante);
    List<Denuncia> findByStatusDenuncia(StatusDenuncia statusDenuncia);
}

