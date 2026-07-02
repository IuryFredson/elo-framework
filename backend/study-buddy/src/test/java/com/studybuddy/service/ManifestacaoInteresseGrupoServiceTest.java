package com.studybuddy.service;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.studybuddy.dto.request.CriarManifestacaoInteresseGrupoRequestDTO;
import com.studybuddy.dto.response.ManifestacaoInteresseGrupoResponseDTO;
import com.studybuddy.exception.AcessoNegadoException;
import com.studybuddy.exception.GrupoEstudoInativoException;
import com.studybuddy.exception.ManifestacaoInteresseDuplicadaException;
import com.studybuddy.exception.ManifestacaoInteresseInvalidaException;
import com.studybuddy.exception.TransicaoInvalidaManifestacaoException;
import com.studybuddy.mapper.ManifestacaoInteresseGrupoMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.entity.ManifestacaoInteresseGrupo;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import com.studybuddy.repository.EstudanteRepository;
import com.studybuddy.repository.GrupoEstudoRepository;
import com.studybuddy.repository.ManifestacaoInteresseGrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManifestacaoInteresseGrupoServiceTest {

    @Mock
    private ManifestacaoInteresseGrupoRepository manifestacaoRepository;

    @Mock
    private GrupoEstudoRepository grupoRepository;

    @Mock
    private EstudanteRepository estudanteRepository;

    private ManifestacaoInteresseGrupoService service;
    private Estudante publicador;
    private Estudante interessado;
    private GrupoEstudo grupo;
    private CriarManifestacaoInteresseGrupoRequestDTO criarDTO;

    @BeforeEach
    void setUp() {
        service = new ManifestacaoInteresseGrupoService(
                manifestacaoRepository,
                grupoRepository,
                estudanteRepository,
                new ManifestacaoInteresseGrupoMapper());
        publicador = estudante("Ana Silva");
        interessado = estudante("Bruno Costa");
        grupo = grupo(publicador, StatusGrupoEstudo.ATIVO);
        criarDTO = new CriarManifestacaoInteresseGrupoRequestDTO(
                grupo.getId(),
                interessado.getId(),
                "Tenho interesse em participar"
        );
    }

    @Test
    void deveCriarManifestacaoPendenteParaGrupoAtivo() {
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(estudanteRepository.findById(interessado.getId())).thenReturn(Optional.of(interessado));
        when(manifestacaoRepository.existsByGrupo_IdAndInteressado_IdAndStatusIn(
                eq(grupo.getId()), eq(interessado.getId()), any(Collection.class))).thenReturn(false);
        when(manifestacaoRepository.save(any(ManifestacaoInteresseGrupo.class))).thenAnswer(invocation -> {
            ManifestacaoInteresseGrupo manifestacao = invocation.getArgument(0);
            manifestacao.setId(UUID.randomUUID());
            return manifestacao;
        });

        ManifestacaoInteresseGrupoResponseDTO response = service.criar(criarDTO);

        assertNotNull(response.id());
        assertEquals(grupo.getId(), response.grupoId());
        assertEquals(interessado.getId(), response.interessadoId());
        assertEquals(publicador.getId(), response.publicadorId());
        assertEquals(StatusManifestacaoInteresse.PENDENTE, response.status());
        assertEquals(criarDTO.mensagem(), response.mensagem());
        assertNotNull(response.dataManifestacao());
        verify(manifestacaoRepository).save(any(ManifestacaoInteresseGrupo.class));
    }

    @Test
    void naoDeveCriarManifestacaoParaGrupoInativo() {
        grupo.setStatus(StatusGrupoEstudo.PAUSADO);
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(estudanteRepository.findById(interessado.getId())).thenReturn(Optional.of(interessado));

        assertThrows(GrupoEstudoInativoException.class, () -> service.criar(criarDTO));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarManifestacaoNoProprioGrupo() {
        CriarManifestacaoInteresseGrupoRequestDTO dto = new CriarManifestacaoInteresseGrupoRequestDTO(
                grupo.getId(),
                publicador.getId(),
                "Quero entrar no proprio grupo"
        );
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(estudanteRepository.findById(publicador.getId())).thenReturn(Optional.of(publicador));

        assertThrows(ManifestacaoInteresseInvalidaException.class, () -> service.criar(dto));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarManifestacaoDuplicadaAtiva() {
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(estudanteRepository.findById(interessado.getId())).thenReturn(Optional.of(interessado));
        when(manifestacaoRepository.existsByGrupo_IdAndInteressado_IdAndStatusIn(
                eq(grupo.getId()), eq(interessado.getId()), any(Collection.class))).thenReturn(true);

        assertThrows(ManifestacaoInteresseDuplicadaException.class, () -> service.criar(criarDTO));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void deveAceitarManifestacaoPendenteQuandoSolicitanteForPublicador() {
        ManifestacaoInteresseGrupo manifestacao = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findById(manifestacao.getId())).thenReturn(Optional.of(manifestacao));
        when(manifestacaoRepository.save(manifestacao)).thenReturn(manifestacao);

        ManifestacaoInteresseGrupoResponseDTO response = service.aceitar(manifestacao.getId(), publicador.getId());

        assertEquals(StatusManifestacaoInteresse.ACEITA, response.status());
        assertNotNull(response.dataResposta());
        verify(manifestacaoRepository).save(manifestacao);
    }

    @Test
    void deveRecusarManifestacaoPendenteQuandoSolicitanteForPublicador() {
        ManifestacaoInteresseGrupo manifestacao = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findById(manifestacao.getId())).thenReturn(Optional.of(manifestacao));
        when(manifestacaoRepository.save(manifestacao)).thenReturn(manifestacao);

        ManifestacaoInteresseGrupoResponseDTO response = service.recusar(manifestacao.getId(), publicador.getId());

        assertEquals(StatusManifestacaoInteresse.RECUSADA, response.status());
        assertNotNull(response.dataResposta());
    }

    @Test
    void naoDeveResponderManifestacaoDeOutroPublicador() {
        ManifestacaoInteresseGrupo manifestacao = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findById(manifestacao.getId())).thenReturn(Optional.of(manifestacao));

        assertThrows(AcessoNegadoException.class,
                () -> service.aceitar(manifestacao.getId(), UUID.randomUUID()));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void deveCancelarManifestacaoPendenteQuandoSolicitanteForInteressado() {
        ManifestacaoInteresseGrupo manifestacao = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findById(manifestacao.getId())).thenReturn(Optional.of(manifestacao));
        when(manifestacaoRepository.save(manifestacao)).thenReturn(manifestacao);

        ManifestacaoInteresseGrupoResponseDTO response = service.cancelar(manifestacao.getId(), interessado.getId());

        assertEquals(StatusManifestacaoInteresse.CANCELADA, response.status());
        assertNotNull(response.dataResposta());
    }

    @Test
    void naoDeveCancelarManifestacaoDeOutroInteressado() {
        ManifestacaoInteresseGrupo manifestacao = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findById(manifestacao.getId())).thenReturn(Optional.of(manifestacao));

        assertThrows(AcessoNegadoException.class,
                () -> service.cancelar(manifestacao.getId(), UUID.randomUUID()));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveResponderManifestacaoForaDePendente() {
        ManifestacaoInteresseGrupo manifestacao = manifestacao(StatusManifestacaoInteresse.ACEITA);
        when(manifestacaoRepository.findById(manifestacao.getId())).thenReturn(Optional.of(manifestacao));

        assertThrows(TransicaoInvalidaManifestacaoException.class,
                () -> service.recusar(manifestacao.getId(), publicador.getId()));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void deveListarManifestacoesPorGrupoQuandoSolicitanteForPublicador() {
        ManifestacaoInteresseGrupo primeira = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        ManifestacaoInteresseGrupo segunda = manifestacao(StatusManifestacaoInteresse.ACEITA);
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(manifestacaoRepository.findByGrupo_IdOrderByDataManifestacaoDesc(grupo.getId()))
                .thenReturn(List.of(primeira, segunda));

        List<ManifestacaoInteresseGrupoResponseDTO> response = service.listarPorGrupo(grupo.getId(), publicador.getId());

        assertEquals(2, response.size());
        assertEquals(primeira.getId(), response.get(0).id());
        assertEquals(segunda.getId(), response.get(1).id());
    }

    @Test
    void naoDeveListarManifestacoesDoGrupoParaOutroEstudante() {
        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));

        assertThrows(AcessoNegadoException.class,
                () -> service.listarPorGrupo(grupo.getId(), interessado.getId()));
    }

    @Test
    void deveListarManifestacoesPorInteressado() {
        ManifestacaoInteresseGrupo primeira = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findByInteressado_IdOrderByDataManifestacaoDesc(interessado.getId()))
                .thenReturn(List.of(primeira));

        List<ManifestacaoInteresseGrupoResponseDTO> response = service.listarPorInteressado(interessado.getId());

        assertEquals(1, response.size());
        assertEquals(interessado.getId(), response.get(0).interessadoId());
    }

    @Test
    void deveBuscarManifestacaoQuandoSolicitanteForInteressadoOuPublicador() {
        ManifestacaoInteresseGrupo manifestacao = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findById(manifestacao.getId())).thenReturn(Optional.of(manifestacao));

        ManifestacaoInteresseGrupoResponseDTO responseInteressado = service.buscarPorId(
                manifestacao.getId(), interessado.getId());
        ManifestacaoInteresseGrupoResponseDTO responsePublicador = service.buscarPorId(
                manifestacao.getId(), publicador.getId());

        assertEquals(manifestacao.getId(), responseInteressado.id());
        assertEquals(manifestacao.getId(), responsePublicador.id());
    }

    @Test
    void deveCancelarPendentesDaOferta() {
        ManifestacaoInteresseGrupo primeira = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        ManifestacaoInteresseGrupo segunda = manifestacao(StatusManifestacaoInteresse.PENDENTE);
        when(manifestacaoRepository.findByGrupo_IdAndStatus(grupo.getId(), StatusManifestacaoInteresse.PENDENTE))
                .thenReturn(List.of(primeira, segunda));

        service.cancelarPendentesDaOferta(grupo.getId());

        assertEquals(StatusManifestacaoInteresse.CANCELADA, primeira.getStatus());
        assertEquals(StatusManifestacaoInteresse.CANCELADA, segunda.getStatus());
        verify(manifestacaoRepository).saveAll(List.of(primeira, segunda));
    }

    private Estudante estudante(String nome) {
        Estudante estudante = new Estudante();
        estudante.setId(UUID.randomUUID());
        estudante.setNome(nome);
        estudante.setEmail(nome.toLowerCase().replace(" ", ".") + "@email.com");
        estudante.setTelefone("85999999999");
        estudante.setAtivo(true);
        estudante.setMatricula(UUID.randomUUID().toString());
        estudante.setInstituicao("Universidade Federal");
        return estudante;
    }

    private GrupoEstudo grupo(Estudante publicador, StatusGrupoEstudo status) {
        GrupoEstudo grupo = new GrupoEstudo();
        grupo.setId(UUID.randomUUID());
        grupo.setTitulo("Grupo de Algoritmos");
        grupo.setDescricao("Encontros para resolver listas de algoritmos");
        grupo.setDisciplina("Algoritmos");
        grupo.setQuantidadeVagas(5);
        grupo.setModalidade(ModalidadeEstudo.ONLINE);
        grupo.setPeriodo(PeriodoDisponibilidade.NOITE);
        grupo.setStatus(status);
        grupo.setDataPublicacao(LocalDate.now());
        grupo.setPublicador(publicador);
        return grupo;
    }

    private ManifestacaoInteresseGrupo manifestacao(StatusManifestacaoInteresse status) {
        ManifestacaoInteresseGrupo manifestacao = new ManifestacaoInteresseGrupo();
        manifestacao.setId(UUID.randomUUID());
        manifestacao.setGrupo(grupo);
        manifestacao.setInteressado(interessado);
        manifestacao.setStatus(status);
        manifestacao.setMensagem("Tenho interesse em participar");
        manifestacao.setDataManifestacao(LocalDateTime.now().minusDays(1));
        return manifestacao;
    }
}
