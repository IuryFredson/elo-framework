package com.mentormatch.config.seed;

import com.elo.denuncia.StatusDenuncia;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.mentormatch.model.entity.Aluno;
import com.mentormatch.model.entity.DenunciaSessaoMentoria;
import com.mentormatch.model.entity.Mentor;
import com.mentormatch.model.entity.ParticipanteMentoria;
import com.mentormatch.model.entity.PerfilMentoria;
import com.mentormatch.model.entity.SessaoMentoria;
import com.mentormatch.model.entity.SolicitacaoMentoria;
import com.mentormatch.model.enums.CriterioDenunciaMentorMatch;
import com.mentormatch.model.enums.ModalidadeMentoria;
import com.mentormatch.model.enums.NivelConhecimento;
import com.mentormatch.model.enums.PeriodoDisponibilidade;
import com.mentormatch.model.enums.StatusSessaoMentoria;
import com.mentormatch.repository.AlunoRepository;
import com.mentormatch.repository.DenunciaSessaoMentoriaRepository;
import com.mentormatch.repository.MentorRepository;
import com.mentormatch.repository.SessaoMentoriaRepository;
import com.mentormatch.repository.SolicitacaoMentoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Popula o banco com um conjunto mínimo de dados para demonstrar as
 * funcionalidades do Mentor Match: perfis de mentores e alunos, sessões
 * publicadas (com busca), solicitações em diferentes estados, matching por
 * aluno e o fluxo de denúncia/moderação.
 *
 * <p>Ativo apenas no profile {@code dev}, onde o schema é recriado a cada
 * inicialização ({@code application-dev.properties}).
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final MentorRepository mentorRepository;
    private final AlunoRepository alunoRepository;
    private final SessaoMentoriaRepository sessaoRepository;
    private final SolicitacaoMentoriaRepository solicitacaoRepository;
    private final DenunciaSessaoMentoriaRepository denunciaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (mentorRepository.count() > 0 || alunoRepository.count() > 0) {
            log.info("[DevDataSeeder] dados já presentes, seed ignorado.");
            return;
        }

        log.info("[DevDataSeeder] populando banco de desenvolvimento...");

        List<Mentor> mentores = seedMentores();
        List<Aluno> alunos = seedAlunos();
        List<SessaoMentoria> sessoes = seedSessoes(mentores);
        seedSolicitacoes(alunos, sessoes);
        seedDenuncias(alunos, sessoes);

        log.info("[DevDataSeeder] seed concluído: {} mentores, {} alunos, {} sessões.",
                mentores.size(), alunos.size(), sessoes.size());
    }

    private List<Mentor> seedMentores() {
        Mentor helena = new Mentor();
        preencherUsuario(helena, "Dra. Helena Costa", "helena.costa@mentormatch.dev", "11990000001");
        helena.setPerfilMentoria(perfil(
                Set.of("Engenharia de Software", "Arquitetura de Software"),
                Set.of("Compartilhar experiência de carreira", "Preparar para entrevistas técnicas"),
                NivelConhecimento.ESPECIALISTA,
                Set.of(ModalidadeMentoria.ONLINE, ModalidadeMentoria.HIBRIDA),
                Set.of(PeriodoDisponibilidade.NOITE, PeriodoDisponibilidade.FIM_DE_SEMANA),
                Set.of("Português", "Inglês"),
                "Engenheira de software sênior com 12 anos de experiência em sistemas distribuídos. "
                        + "Gosto de ajudar pessoas em início de carreira a estruturar seus estudos."));

        Mentor ricardo = new Mentor();
        preencherUsuario(ricardo, "Prof. Ricardo Alves", "ricardo.alves@mentormatch.dev", "11990000002");
        ricardo.setPerfilMentoria(perfil(
                Set.of("Ciência de Dados", "Machine Learning", "Estatística"),
                Set.of("Aprofundar fundamentos", "Orientar projetos práticos"),
                NivelConhecimento.AVANCADO,
                Set.of(ModalidadeMentoria.ONLINE),
                Set.of(PeriodoDisponibilidade.TARDE, PeriodoDisponibilidade.NOITE),
                Set.of("Português"),
                "Professor e cientista de dados. Foco em fundamentos sólidos de estatística e ML."));

        Mentor marina = new Mentor();
        preencherUsuario(marina, "Marina Duarte", "marina.duarte@mentormatch.dev", "11990000003");
        marina.setPerfilMentoria(perfil(
                Set.of("UX Design", "Design de Produto"),
                Set.of("Montar portfólio", "Transição de carreira"),
                NivelConhecimento.INTERMEDIARIO,
                Set.of(ModalidadeMentoria.PRESENCIAL, ModalidadeMentoria.HIBRIDA),
                Set.of(PeriodoDisponibilidade.MANHA, PeriodoDisponibilidade.FLEXIVEL),
                Set.of("Português", "Espanhol"),
                "Designer de produto ajudando quem quer entrar na área de UX a construir um portfólio real."));

        return mentorRepository.saveAll(List.of(helena, ricardo, marina));
    }

    private List<Aluno> seedAlunos() {
        Aluno lucas = new Aluno();
        preencherUsuario(lucas, "Lucas Ferreira", "lucas.ferreira@aluno.dev", "11980000001");
        lucas.setPerfilMentoria(perfil(
                Set.of("Engenharia de Software"),
                Set.of("Preparar para entrevistas técnicas"),
                NivelConhecimento.INICIANTE,
                Set.of(ModalidadeMentoria.ONLINE),
                Set.of(PeriodoDisponibilidade.NOITE),
                Set.of("Português"),
                "Estudante de ADS buscando primeiro estágio como dev backend."));

        Aluno beatriz = new Aluno();
        preencherUsuario(beatriz, "Beatriz Nunes", "beatriz.nunes@aluno.dev", "11980000002");
        beatriz.setPerfilMentoria(perfil(
                Set.of("Ciência de Dados", "Machine Learning"),
                Set.of("Aprofundar fundamentos"),
                NivelConhecimento.INICIANTE,
                Set.of(ModalidadeMentoria.ONLINE),
                Set.of(PeriodoDisponibilidade.TARDE),
                Set.of("Português"),
                "Formada em estatística, migrando para ciência de dados."));

        Aluno gabriel = new Aluno();
        preencherUsuario(gabriel, "Gabriel Souza", "gabriel.souza@aluno.dev", "11980000003");
        gabriel.setPerfilMentoria(perfil(
                Set.of("UX Design"),
                Set.of("Montar portfólio", "Transição de carreira"),
                NivelConhecimento.INICIANTE,
                Set.of(ModalidadeMentoria.PRESENCIAL, ModalidadeMentoria.HIBRIDA),
                Set.of(PeriodoDisponibilidade.MANHA),
                Set.of("Português"),
                "Trabalho com marketing e quero fazer a transição para UX."));

        Aluno julia = new Aluno();
        preencherUsuario(julia, "Julia Martins", "julia.martins@aluno.dev", "11980000004");
        julia.setPerfilMentoria(perfil(
                Set.of("Engenharia de Software", "Arquitetura de Software"),
                Set.of("Compartilhar experiência de carreira"),
                NivelConhecimento.INTERMEDIARIO,
                Set.of(ModalidadeMentoria.ONLINE, ModalidadeMentoria.HIBRIDA),
                Set.of(PeriodoDisponibilidade.NOITE, PeriodoDisponibilidade.FIM_DE_SEMANA),
                Set.of("Português", "Inglês"),
                "Desenvolvedora júnior querendo evoluir para posições plenas."));

        return alunoRepository.saveAll(List.of(lucas, beatriz, gabriel, julia));
    }

    private List<SessaoMentoria> seedSessoes(List<Mentor> mentores) {
        Mentor helena = mentores.get(0);
        Mentor ricardo = mentores.get(1);
        Mentor marina = mentores.get(2);

        SessaoMentoria arquitetura = sessao(
                "Fundamentos de Arquitetura de Software",
                "Encontros para discutir padrões arquiteturais, trade-offs e como estudar o tema de forma prática.",
                "Engenharia de Software", NivelConhecimento.INTERMEDIARIO,
                ModalidadeMentoria.ONLINE, PeriodoDisponibilidade.NOITE, 3,
                StatusSessaoMentoria.ATIVA, LocalDate.now().minusDays(12), helena);

        SessaoMentoria entrevistas = sessao(
                "Preparação para entrevistas técnicas",
                "Simulados de entrevista, revisão de currículo e estratégias de estudo para processos seletivos.",
                "Engenharia de Software", NivelConhecimento.INICIANTE,
                ModalidadeMentoria.HIBRIDA, PeriodoDisponibilidade.FIM_DE_SEMANA, 2,
                StatusSessaoMentoria.ATIVA, LocalDate.now().minusDays(6), helena);

        SessaoMentoria ml = sessao(
                "Introdução a Machine Learning",
                "Do zero aos primeiros modelos: fundamentos de estatística, regressão e boas práticas.",
                "Ciência de Dados", NivelConhecimento.INICIANTE,
                ModalidadeMentoria.ONLINE, PeriodoDisponibilidade.TARDE, 4,
                StatusSessaoMentoria.ATIVA, LocalDate.now().minusDays(3), ricardo);

        SessaoMentoria portfolio = sessao(
                "Portfólio de UX do zero",
                "Construção de um estudo de caso completo para quem está entrando na área de UX.",
                "UX Design", NivelConhecimento.INICIANTE,
                ModalidadeMentoria.PRESENCIAL, PeriodoDisponibilidade.MANHA, 2,
                StatusSessaoMentoria.ATIVA, LocalDate.now().minusDays(1), marina);

        SessaoMentoria estatistica = sessao(
                "Estatística aplicada a dados",
                "Sessão pausada temporariamente enquanto ajusto a agenda do semestre.",
                "Ciência de Dados", NivelConhecimento.INTERMEDIARIO,
                ModalidadeMentoria.ONLINE, PeriodoDisponibilidade.NOITE, 3,
                StatusSessaoMentoria.PAUSADA, LocalDate.now().minusDays(20), ricardo);

        return sessaoRepository.saveAll(List.of(arquitetura, entrevistas, ml, portfolio, estatistica));
    }

    private void seedSolicitacoes(List<Aluno> alunos, List<SessaoMentoria> sessoes) {
        Aluno lucas = alunos.get(0);
        Aluno beatriz = alunos.get(1);
        Aluno gabriel = alunos.get(2);
        Aluno julia = alunos.get(3);

        SessaoMentoria arquitetura = sessoes.get(0);
        SessaoMentoria entrevistas = sessoes.get(1);
        SessaoMentoria ml = sessoes.get(2);
        SessaoMentoria portfolio = sessoes.get(3);

        SolicitacaoMentoria s1 = solicitacao(arquitetura, lucas, StatusManifestacaoInteresse.PENDENTE,
                "Tenho interesse em entender melhor arquitetura antes das entrevistas. Posso participar?",
                LocalDateTime.now().minusDays(2), null);

        SolicitacaoMentoria s2 = solicitacao(entrevistas, julia, StatusManifestacaoInteresse.ACEITA,
                "Estou me preparando para processos de nível pleno e gostaria de fazer simulados.",
                LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4));

        SolicitacaoMentoria s3 = solicitacao(ml, beatriz, StatusManifestacaoInteresse.PENDENTE,
                "Venho da estatística e quero entrar em ML com o pé direito.",
                LocalDateTime.now().minusDays(1), null);

        SolicitacaoMentoria s4 = solicitacao(portfolio, gabriel, StatusManifestacaoInteresse.ACEITA,
                "Preciso montar meu primeiro portfólio para a transição de carreira.",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(10));

        SolicitacaoMentoria s5 = solicitacao(ml, lucas, StatusManifestacaoInteresse.RECUSADA,
                "Será que consigo acompanhar mesmo focado em backend?",
                LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3));

        solicitacaoRepository.saveAll(List.of(s1, s2, s3, s4, s5));
    }

    private void seedDenuncias(List<Aluno> alunos, List<SessaoMentoria> sessoes) {
        DenunciaSessaoMentoria pendente = new DenunciaSessaoMentoria();
        pendente.setDenunciante(alunos.get(2)); // Gabriel
        pendente.setSessao(sessoes.get(4)); // Estatística aplicada (pausada)
        pendente.setTitulo("Descrição não corresponde ao conteúdo");
        pendente.setCorpo("A sessão promete conteúdo introdutório, mas o material enviado é bem mais avançado.");
        pendente.setCriterio(CriterioDenunciaMentorMatch.INFORMACAO_FALSA);
        pendente.setStatusDenuncia(StatusDenuncia.PENDENTE);
        pendente.setCriadoEm(LocalDateTime.now().minusDays(2));

        DenunciaSessaoMentoria procedente = new DenunciaSessaoMentoria();
        procedente.setDenunciante(alunos.get(0)); // Lucas
        procedente.setSessao(sessoes.get(0)); // Arquitetura
        procedente.setTitulo("Cobrança fora da plataforma");
        procedente.setCorpo("Fui solicitado a pagar por fora para garantir a vaga na sessão.");
        procedente.setCriterio(CriterioDenunciaMentorMatch.USO_INDEVIDO);
        procedente.setStatusDenuncia(StatusDenuncia.PROCEDENTE);
        procedente.setCriadoEm(LocalDateTime.now().minusDays(9));
        procedente.setStatusAtualizadoEm(LocalDateTime.now().minusDays(7));

        denunciaRepository.saveAll(List.of(pendente, procedente));
    }

    private void preencherUsuario(ParticipanteMentoria usuario, String nome, String email, String telefone) {
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setAtivo(true);
    }

    private PerfilMentoria perfil(
            Set<String> areas, Set<String> objetivos, NivelConhecimento nivel,
            Set<ModalidadeMentoria> modalidades, Set<PeriodoDisponibilidade> disponibilidades,
            Set<String> idiomas, String descricao) {
        PerfilMentoria p = new PerfilMentoria();
        p.getAreas().addAll(areas);
        p.getObjetivos().addAll(objetivos);
        p.setNivelConhecimento(nivel);
        p.getModalidades().addAll(modalidades);
        p.getDisponibilidades().addAll(disponibilidades);
        p.getIdiomas().addAll(idiomas);
        p.setDescricao(descricao);
        return p;
    }

    private SessaoMentoria sessao(
            String titulo, String descricao, String area, NivelConhecimento nivel,
            ModalidadeMentoria modalidade, PeriodoDisponibilidade periodo, int capacidade,
            StatusSessaoMentoria status, LocalDate dataPublicacao, Mentor publicador) {
        SessaoMentoria s = new SessaoMentoria();
        s.setTitulo(titulo);
        s.setDescricao(descricao);
        s.setArea(area);
        s.setNivelAtendido(nivel);
        s.setModalidade(modalidade);
        s.setPeriodo(periodo);
        s.setCapacidade(capacidade);
        s.setStatus(status);
        s.setDataPublicacao(dataPublicacao);
        s.setPublicador(publicador);
        return s;
    }

    private SolicitacaoMentoria solicitacao(
            SessaoMentoria sessao, Aluno interessado, StatusManifestacaoInteresse status,
            String mensagem, LocalDateTime dataSolicitacao, LocalDateTime dataResposta) {
        SolicitacaoMentoria s = new SolicitacaoMentoria();
        s.setSessao(sessao);
        s.setInteressado(interessado);
        s.setStatus(status);
        s.setMensagem(mensagem);
        s.setDataSolicitacao(dataSolicitacao);
        s.setDataResposta(dataResposta);
        return s;
    }
}
