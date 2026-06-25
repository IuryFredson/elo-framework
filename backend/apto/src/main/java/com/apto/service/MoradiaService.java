package com.apto.service;


import com.apto.dto.request.AtualizarMoradiaRequestDTO;
import com.apto.dto.request.CriarMoradiaRequestDTO;
import com.apto.dto.response.MoradiaResponseDTO;
import com.apto.exception.MoradiaAssociadaComAnuncioException;
import com.apto.exception.MoradiaNaoEncontradaException;
import com.apto.mapper.MoradiaMapper;
import com.apto.model.entity.Moradia;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.MoradiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MoradiaService {

    private final MoradiaRepository moradiaRepository;
    private final AnuncioRepository anuncioRepository;
    private final MoradiaMapper moradiaMapper;

    public MoradiaService(MoradiaRepository moradiaRepository,
                          AnuncioRepository anuncioRepository,
                          MoradiaMapper moradiaMapper) {
        this.moradiaRepository = moradiaRepository;
        this.anuncioRepository = anuncioRepository;
        this.moradiaMapper = moradiaMapper;
    }
    public MoradiaResponseDTO criar(CriarMoradiaRequestDTO dto){
        Moradia moradia = new Moradia();

        moradia.setBairro(dto.bairro());
        moradia.setRegrasMoradia(dto.regrasMoradia());
        moradia.setTipoMoradia(dto.tipoMoradia());
        moradia.setAceitaAnimais(dto.aceitaAnimais());
        moradia.setMobiliado(dto.mobiliado());
        moradia.setQuantidadeVagas(dto.quantidadeVagas());
        moradia.setEnderecoResumo(dto.enderecoResumo());

        return moradiaMapper.toResponseDTO(moradiaRepository.save(moradia));
    }

    public List<MoradiaResponseDTO> listarTodos(){
        return moradiaRepository.findAll()
                .stream()
                .map(moradiaMapper::toResponseDTO)
                .toList();
    }

    public MoradiaResponseDTO buscarPorId(UUID id){
        Moradia moradia = buscarEntidadePorId(id);
        return moradiaMapper.toResponseDTO(moradia);

    }

    public Moradia buscarEntidadePorId(UUID id){
        return moradiaRepository.findById(id)
                .orElseThrow( () -> new MoradiaNaoEncontradaException("Moradia não encontrada com o id: " + id));
    }

    public MoradiaResponseDTO atualizar(UUID id, AtualizarMoradiaRequestDTO dto){
        Moradia moradia = buscarEntidadePorId(id);

        moradia.setBairro(dto.bairro());
        moradia.setEnderecoResumo(dto.enderecoResumo());
        moradia.setMobiliado(dto.mobiliado());
        moradia.setAceitaAnimais(dto.aceitaAnimais());
        moradia.setQuantidadeVagas(dto.quantidadeVagas());
        moradia.setRegrasMoradia(dto.regrasMoradia());
        moradia.setTipoMoradia(dto.tipoMoradia());

        return moradiaMapper.toResponseDTO(moradiaRepository.save(moradia));
    }

    public void deletar(UUID id){
        Moradia moradia = buscarEntidadePorId(id);
        if(anuncioRepository.existsByMoradia(moradia)){
            throw new MoradiaAssociadaComAnuncioException("Não é possível remover uma moradia que possui anúncio associado");
        }
        moradiaRepository.delete(moradia);
    }
}
