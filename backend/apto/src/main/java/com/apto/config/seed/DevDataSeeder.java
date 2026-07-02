package com.apto.config.seed;

import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Denuncia;
import com.apto.model.entity.Locador;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.ReputacaoAnunciante;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.FrequenciaVisitas;
import com.apto.model.enums.Genero;
import com.apto.model.enums.HorarioSono;
import com.apto.model.enums.NivelBarulho;
import com.apto.model.enums.NivelOrganizacao;
import com.apto.model.enums.PreferenciaGeneroConvivencia;
import com.apto.model.enums.RotinaEstudos;
import com.apto.model.enums.StatusAnuncio;
import com.elo.denuncia.StatusDenuncia;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.DenunciaRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.ManifestacaoInteresseRepository;
import com.apto.repository.MoradiaRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import com.apto.repository.ReputacaoAnuncianteRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final UsuarioUniversitarioRepository universitarioRepository;
    private final LocadorRepository locadorRepository;
    private final MoradiaRepository moradiaRepository;
    private final AnuncioRepository anuncioRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ManifestacaoInteresseRepository manifestacaoRepository;
    private final DenunciaRepository denunciaRepository;
    private final PerfilAnuncianteRepository perfilAnuncianteRepository;
    private final ReputacaoAnuncianteRepository reputacaoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[DevDataSeeder] populando banco de desenvolvimento...");

        var perfisLocadores = seedLocadores();

        var moradias = seedMoradias();
        var anuncios = seedAnuncios(perfisLocadores, moradias);
        var universitarios = seedUniversitarios();
        seedAvaliacoesEReputacao(universitarios, perfisLocadores, moradias, anuncios);
        seedManifestacoes(universitarios, anuncios);
        seedDenuncias(universitarios, anuncios);

        log.info("[DevDataSeeder] seed concluído.");
    }

    private List<PerfilAnunciante> seedLocadores() {
        Locador rita = new Locador();
        rita.setNome("Rita Albuquerque");
        rita.setEmail("rita.alb@imobiliariaboa.com");
        rita.setTelefone("11999990001");
        rita.setDocumentoIdentificacao("12345678900");
        rita.setNomeExibicaoOuRazao("Rita Imóveis");

        Locador joao = new Locador();
        joao.setNome("João Pereira");
        joao.setEmail("joao.pereira@locacoes.com");
        joao.setTelefone("11999990002");
        joao.setDocumentoIdentificacao("23456789011");
        joao.setNomeExibicaoOuRazao("JP Locações");

        Locador beto = new Locador();
        beto.setNome("Beto Souza");
        beto.setEmail("beto.souza@email.com");
        beto.setTelefone("11999990003");
        beto.setDocumentoIdentificacao("34567890122");
        beto.setNomeExibicaoOuRazao("Beto Aluga");

        List<Locador> salvos = locadorRepository.saveAll(List.of(rita, joao, beto));
        List<PerfilAnunciante> perfis = salvos.stream().map(locador -> {
            PerfilAnunciante perfil = new PerfilAnunciante();
            perfil.setUsuario(locador);
            perfil.setAtivo(true);
            return perfilAnuncianteRepository.save(perfil);
        }).toList();

        return perfis;
    }

    private List<Moradia> seedMoradias() {
        Moradia republicaCentro = new Moradia();
        republicaCentro.setTipoMoradia(TipoMoradia.REPUBLICA);
        republicaCentro.setBairro("Centro");
        republicaCentro.setEnderecoResumo("Rua das Acácias, próx. ao campus");
        republicaCentro.setMobiliado(true);
        republicaCentro.setAceitaAnimais(false);
        republicaCentro.setQuantidadeVagas(4);
        republicaCentro.setRegrasMoradia("Silêncio após 22h. Limpeza coletiva aos sábados.");

        Moradia aptoVilaUni = new Moradia();
        aptoVilaUni.setTipoMoradia(TipoMoradia.APARTAMENTO);
        aptoVilaUni.setBairro("Vila Universitária");
        aptoVilaUni.setEnderecoResumo("Av. Universitária, 1500");
        aptoVilaUni.setMobiliado(true);
        aptoVilaUni.setAceitaAnimais(true);
        aptoVilaUni.setQuantidadeVagas(2);
        aptoVilaUni.setRegrasMoradia("Sem festas. Visitas até 23h.");

        Moradia casaJardim = new Moradia();
        casaJardim.setTipoMoradia(TipoMoradia.CASA);
        casaJardim.setBairro("Jardim das Rosas");
        casaJardim.setEnderecoResumo("Rua das Hortênsias, 250");
        casaJardim.setMobiliado(false);
        casaJardim.setAceitaAnimais(true);
        casaJardim.setQuantidadeVagas(3);
        casaJardim.setRegrasMoradia("Não fumar dentro de casa.");

        Moradia quartoCentro = new Moradia();
        quartoCentro.setTipoMoradia(TipoMoradia.QUARTO);
        quartoCentro.setBairro("Centro");
        quartoCentro.setEnderecoResumo("Rua Sete de Setembro, 80");
        quartoCentro.setMobiliado(true);
        quartoCentro.setAceitaAnimais(false);
        quartoCentro.setQuantidadeVagas(1);
        quartoCentro.setRegrasMoradia("Quarto individual com banheiro privativo.");

        return moradiaRepository.saveAll(List.of(republicaCentro, aptoVilaUni, casaJardim, quartoCentro));
    }

    private List<Anuncio> seedAnuncios(List<PerfilAnunciante> perfis, List<Moradia> moradias) {
        Anuncio republicaAtivo = new Anuncio();
        republicaAtivo.setTitulo("Vaga em república perto do campus");
        republicaAtivo.setDescricao("República com 4 estudantes, ambiente tranquilo, mobiliada.");
        republicaAtivo.setValorMensal(new BigDecimal("750.00"));
        republicaAtivo.setTipoAnuncio(TipoAnuncio.VAGA_COMPARTILHADA);
        republicaAtivo.setStatus(StatusAnuncio.ATIVO);
        republicaAtivo.setDataPublicacao(LocalDate.now().minusDays(10));
        republicaAtivo.setAnunciante(perfis.get(0)); // Rita
        republicaAtivo.setMoradia(moradias.get(0));

        Anuncio aptoAtivo = new Anuncio();
        aptoAtivo.setTitulo("Apartamento mobiliado para 2 pessoas");
        aptoAtivo.setDescricao("Apartamento de 2 quartos na Vila Universitária, aceita pets.");
        aptoAtivo.setValorMensal(new BigDecimal("1800.00"));
        aptoAtivo.setTipoAnuncio(TipoAnuncio.IMOVEL_COMPLETO);
        aptoAtivo.setStatus(StatusAnuncio.ATIVO);
        aptoAtivo.setDataPublicacao(LocalDate.now().minusDays(3));
        aptoAtivo.setAnunciante(perfis.get(1)); // João
        aptoAtivo.setMoradia(moradias.get(1));

        Anuncio casaAtivo = new Anuncio();
        casaAtivo.setTitulo("Casa para dividir no Jardim das Rosas");
        casaAtivo.setDescricao("Casa ampla com quintal, 3 vagas, dividir entre estudantes.");
        casaAtivo.setValorMensal(new BigDecimal("950.00"));
        casaAtivo.setTipoAnuncio(TipoAnuncio.VAGA_COMPARTILHADA);
        casaAtivo.setStatus(StatusAnuncio.ATIVO);
        casaAtivo.setDataPublicacao(LocalDate.now().minusDays(20));
        casaAtivo.setAnunciante(perfis.get(2)); // Beto
        casaAtivo.setMoradia(moradias.get(2));

        Anuncio quartoPausado = new Anuncio();
        quartoPausado.setTitulo("Quarto individual no Centro");
        quartoPausado.setDescricao("Quarto com banheiro privativo, mobiliado.");
        quartoPausado.setValorMensal(new BigDecimal("1100.00"));
        quartoPausado.setTipoAnuncio(TipoAnuncio.IMOVEL_COMPLETO);
        quartoPausado.setStatus(StatusAnuncio.PAUSADO);
        quartoPausado.setDataPublicacao(LocalDate.now().minusDays(40));
        quartoPausado.setAnunciante(perfis.get(0)); // Rita (segundo anúncio dela)
        quartoPausado.setMoradia(moradias.get(3));

        return anuncioRepository.saveAll(List.of(republicaAtivo, aptoAtivo, casaAtivo, quartoPausado));
    }

    private List<UsuarioUniversitario> seedUniversitarios() {
        UsuarioUniversitario ana = montarUniversitario(
                "Ana Lima", "ana.lima@usuario.com", "ana.lima@univ.edu.br",
                "Engenharia de Software", LocalDate.of(2002, 4, 12), Genero.FEMININO,
                HorarioSono.CEDO, NivelBarulho.BAIXO, FrequenciaVisitas.RARAMENTE,
                NivelOrganizacao.ALTO, RotinaEstudos.CASA,
                false, false, false, PreferenciaGeneroConvivencia.SEM_PREFERENCIA,
                "Sou caseira, durmo cedo e gosto de manter a casa em ordem. " +
                        "Cozinho com frequência e prefiro ambientes silenciosos para estudar à noite. " +
                        "Não fumo, não bebo e não tenho animais.");

        UsuarioUniversitario bruno = montarUniversitario(
                "Bruno Santos", "bruno.santos@usuario.com", "bruno.santos@univ.edu.br",
                "Ciência da Computação", LocalDate.of(2001, 9, 3), Genero.MASCULINO,
                HorarioSono.CEDO, NivelBarulho.BAIXO, FrequenciaVisitas.RARAMENTE,
                NivelOrganizacao.ALTO, RotinaEstudos.CASA,
                false, false, false, PreferenciaGeneroConvivencia.SEM_PREFERENCIA,
                "Estudo bastante em casa, prefiro silêncio à noite e gosto de cozinhar. " +
                        "Sou metódico com limpeza compartilhada.");

        UsuarioUniversitario carla = montarUniversitario(
                "Carla Mendes", "carla.mendes@usuario.com", "carla.mendes@univ.edu.br",
                "Artes Cênicas", LocalDate.of(2003, 1, 25), Genero.FEMININO,
                HorarioSono.TARDE, NivelBarulho.ALTO, FrequenciaVisitas.FREQUENTEMENTE,
                NivelOrganizacao.BAIXO, RotinaEstudos.BIBLIOTECA,
                true, true, true, PreferenciaGeneroConvivencia.SEM_PREFERENCIA,
                "Vida social agitada, recebo amigos com frequência e adoro festas em casa. " +
                        "Tenho um cachorro e durmo tarde.");

        UsuarioUniversitario daniel = montarUniversitario(
                "Daniel Rocha", "daniel.rocha@usuario.com", "daniel.rocha@univ.edu.br",
                "Direito", LocalDate.of(2000, 7, 18), Genero.MASCULINO,
                HorarioSono.MEDIO, NivelBarulho.MEDIO, FrequenciaVisitas.AS_VEZES,
                NivelOrganizacao.MEDIO, RotinaEstudos.MISTA,
                true, false, false, PreferenciaGeneroConvivencia.SEM_PREFERENCIA,
                null);

        UsuarioUniversitario eduarda = montarUniversitario(
                "Eduarda Prado", "eduarda.prado@usuario.com", "eduarda.prado@univ.edu.br",
                "Medicina", LocalDate.of(2002, 11, 7), Genero.FEMININO,
                HorarioSono.MEDIO, NivelBarulho.BAIXO, FrequenciaVisitas.AS_VEZES,
                NivelOrganizacao.ALTO, RotinaEstudos.MISTA,
                false, false, false, PreferenciaGeneroConvivencia.APENAS_MULHERES,
                "Estudo bastante e prefiro morar com outras mulheres por questão de conforto. " +
                        "Sou organizada e gosto de manter rotina.");

        UsuarioUniversitario felipe = montarUniversitario(
                "Felipe Andrade", "felipe.andrade@usuario.com", "felipe.andrade@univ.edu.br",
                "Arquitetura", LocalDate.of(2001, 2, 15), Genero.MASCULINO,
                HorarioSono.MEDIO, NivelBarulho.MEDIO, FrequenciaVisitas.AS_VEZES,
                NivelOrganizacao.MEDIO, RotinaEstudos.MISTA,
                false, true, true, PreferenciaGeneroConvivencia.A_COMBINAR,
                "Sou flexível, topo conversar sobre regras de convivência antes de fechar.");

        return universitarioRepository.saveAll(List.of(ana, bruno, carla, daniel, eduarda, felipe));
    }

    private UsuarioUniversitario montarUniversitario(
            String nome, String email, String emailInstitucional, String curso,
            LocalDate dataNascimento, Genero genero,
            HorarioSono horarioSono, NivelBarulho nivelBarulho, FrequenciaVisitas frequenciaVisitas,
            NivelOrganizacao nivelOrganizacao, RotinaEstudos rotinaEstudos,
            boolean consomeAlcool, boolean fumante, boolean aceitaAnimais,
            PreferenciaGeneroConvivencia preferencia,
            String descricaoLivre) {

        PerfilConvivencia perfil = new PerfilConvivencia();
        perfil.setHorarioSono(horarioSono);
        perfil.setNivelBarulhoAceitavel(nivelBarulho);
        perfil.setFrequenciaVisitas(frequenciaVisitas);
        perfil.setNivelOrganizacao(nivelOrganizacao);
        perfil.setRotinaEstudos(rotinaEstudos);
        perfil.setConsomeAlcool(consomeAlcool);
        perfil.setFumante(fumante);
        perfil.setAceitaAnimais(aceitaAnimais);
        perfil.setPreferenciaGeneroConvivencia(preferencia);
        perfil.setDescricaoLivre(descricaoLivre);

        UsuarioUniversitario u = new UsuarioUniversitario();
        u.setNome(nome);
        u.setEmail(email);
        u.setTelefone("11900000000");
        u.setEmailInstitucional(emailInstitucional);
        u.setCurso(curso);
        u.setDataNascimento(dataNascimento);
        u.setGenero(genero);
        u.setPerfilConvivencia(perfil);
        return u;
    }

    private void seedAvaliacoesEReputacao(
            List<UsuarioUniversitario> universitarios,
            List<PerfilAnunciante> perfis,
            List<Moradia> moradias,
            List<Anuncio> anuncios) {

        // Avaliações para o perfil da Rita (perfis.get(0))
        Avaliacao a1 = montarAvaliacao(
                universitarios.get(0), perfis.get(0), moradias.get(0), anuncios.get(0),
                5, 5, 5, 4, 5, "Rita é super atenciosa e a república é bem cuidada.", true);
        Avaliacao a2 = montarAvaliacao(
                universitarios.get(1), perfis.get(0), moradias.get(0), anuncios.get(0),
                4, 5, 4, 4, 5, "Boa experiência, ambiente tranquilo.", true);
        Avaliacao a3 = montarAvaliacao(
                universitarios.get(2), perfis.get(0), moradias.get(3), anuncios.get(3),
                2, 3, 2, 2, 3, "Avaliação retirada após resolução do problema.", false);

        // Avaliação para o perfil do João (perfis.get(1))
        Avaliacao a4 = montarAvaliacao(
                universitarios.get(3), perfis.get(1), moradias.get(1), anuncios.get(1),
                3, 3, 4, 3, 3, "Apartamento ok, comunicação podia ser mais ágil.", true);

        // Avaliações para o perfil do Beto (perfis.get(2))
        Avaliacao a5 = montarAvaliacao(
                universitarios.get(4), perfis.get(2), moradias.get(2), anuncios.get(2),
                2, 2, 2, 2, 3, "Casa precisa de manutenção.", true);
        Avaliacao a6 = montarAvaliacao(
                universitarios.get(5), perfis.get(2), moradias.get(2), anuncios.get(2),
                3, 2, 3, 2, 3, "Atende, mas com ressalvas.", true);

        avaliacaoRepository.saveAll(List.of(a1, a2, a3, a4, a5, a6));

        reputacaoRepository.saveAll(List.of(
                montarReputacao(perfis.get(0), 4.5, 2, 4.5, 5.0, 4.5, 4.0, 5.0),
                montarReputacao(perfis.get(1), 3.2, 1, 3.0, 3.0, 4.0, 3.0, 3.0),
                montarReputacao(perfis.get(2), 2.4, 2, 2.5, 2.0, 2.5, 2.0, 3.0)
        ));
    }

    // locador → perfilAnunciante
    private Avaliacao montarAvaliacao(
            UsuarioUniversitario avaliador, PerfilAnunciante anuncianteAvaliado,
            Moradia moradia, Anuncio anuncio,
            int notaGeral, int notaComunicacao, int notaFidelidade,
            int notaEstado, int notaCusto,
            String comentario, boolean ativa) {

        Avaliacao a = new Avaliacao();
        a.setAvaliador(avaliador);
        a.setAnuncianteAvaliado(anuncianteAvaliado);
        a.setMoradia(moradia);
        a.setAnuncio(anuncio);
        a.setNotaGeral(notaGeral);
        a.setNotaComunicacao(notaComunicacao);
        a.setNotaFidelidadeAnuncio(notaFidelidade);
        a.setNotaEstadoMoradia(notaEstado);
        a.setNotaCustoBeneficio(notaCusto);
        a.setComentario(comentario);
        a.setDataCriacao(LocalDateTime.now().minusDays(5));
        a.setAtiva(ativa);
        return a;
    }

    // locador → perfilAnunciante
    private ReputacaoAnunciante montarReputacao(
            PerfilAnunciante perfilAnunciante, double score, int total,
            double mediaGeral, double mediaComunicacao, double mediaFidelidade,
            double mediaEstado, double mediaCusto) {

        ReputacaoAnunciante r = new ReputacaoAnunciante();
        r.setPerfilAnunciante(perfilAnunciante);
        r.setReputacaoScore(score);
        r.setTotalAvaliacoes(total);
        r.setMediaGeral(mediaGeral);
        r.setMediaComunicacao(mediaComunicacao);
        r.setMediaFidelidadeAnuncio(mediaFidelidade);
        r.setMediaEstadoMoradia(mediaEstado);
        r.setMediaCustoBeneficio(mediaCusto);
        r.setUltimaAtualizacao(LocalDateTime.now());
        return r;
    }

    private void seedManifestacoes(List<UsuarioUniversitario> universitarios, List<Anuncio> anuncios) {
        ManifestacaoInteresse m1 = new ManifestacaoInteresse();
        m1.setAnuncio(anuncios.get(0));
        m1.setInteressado(universitarios.get(0));
        m1.setStatus(StatusManifestacaoInteresse.PENDENTE);
        m1.setMensagem("Tenho interesse na vaga, posso visitar essa semana?");
        m1.setDataManifestacao(LocalDateTime.now().minusDays(2));

        ManifestacaoInteresse m2 = new ManifestacaoInteresse();
        m2.setAnuncio(anuncios.get(1));
        m2.setInteressado(universitarios.get(3));
        m2.setStatus(StatusManifestacaoInteresse.ACEITA);
        m2.setMensagem("Ótimo apartamento, gostaria de fechar.");
        m2.setDataManifestacao(LocalDateTime.now().minusDays(7));
        m2.setDataResposta(LocalDateTime.now().minusDays(6));

        ManifestacaoInteresse m3 = new ManifestacaoInteresse();
        m3.setAnuncio(anuncios.get(2));
        m3.setInteressado(universitarios.get(2));
        m3.setStatus(StatusManifestacaoInteresse.RECUSADA);
        m3.setMensagem("Tenho um cachorro grande, aceitam?");
        m3.setDataManifestacao(LocalDateTime.now().minusDays(15));
        m3.setDataResposta(LocalDateTime.now().minusDays(14));

        manifestacaoRepository.saveAll(List.of(m1, m2, m3));
    }

    private void seedDenuncias(List<UsuarioUniversitario> universitarios, List<Anuncio> anuncios) {
        Denuncia d1 = new Denuncia();
        d1.setDenunciante(universitarios.get(2));
        d1.setAnuncio(anuncios.get(2));
        d1.setTitulo("Anúncio com foto desatualizada");
        d1.setCorpo("As fotos da casa não correspondem ao estado atual do imóvel.");
        d1.setCriterio(com.apto.model.enums.CriterioDenunciaApto.ANUNCIO_ENGANOSO);
        d1.setStatusDenuncia(StatusDenuncia.PENDENTE);
        d1.setCriadoEm(LocalDateTime.now().minusDays(1));

        Denuncia d2 = new Denuncia();
        d2.setDenunciante(universitarios.get(4));
        d2.setAnuncio(anuncios.get(2));
        d2.setTitulo("Cobrança de taxa não declarada");
        d2.setCorpo("Após a manifestação de interesse, foi cobrada uma taxa que não constava no anúncio.");
        d2.setCriterio(com.apto.model.enums.CriterioDenunciaApto.PRECO_ABUSIVO);
        d2.setStatusDenuncia(StatusDenuncia.PROCEDENTE);
        d2.setCriadoEm(LocalDateTime.now().minusDays(8));
        d2.setStatusAtualizadoEm(LocalDateTime.now().minusDays(2));

        denunciaRepository.saveAll(List.of(d1, d2));
    }
}
