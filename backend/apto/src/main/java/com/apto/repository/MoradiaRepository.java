package com.apto.repository;

import com.apto.model.entity.Moradia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MoradiaRepository extends JpaRepository<Moradia, UUID> {

}
