package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import unigran.br.Model.DAO.*;
import unigran.br.Model.Entidades.Avaliacao;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Escambista;
import unigran.br.Model.Entidades.Postagem;
import unigran.br.Model.Entidades.Proposta;
import unigran.br.Services.EmailService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PropostaControllerTest extends BaseIntegrationTest {

    private String tokenUsuarioLogado;

    @MockBean
    private PropostaDAO propostaDAO;
    @MockBean
    private PostagemDAO postagemDAO;
    @MockBean
    private CadastroDAO cadastroDAO;
    @MockBean
    private EscambistaDAO escambistaDAO;
    @MockBean
    private AvaliacaoDAO avaliacaoDAO;
    @MockBean
    private EmailService emailService;

    private Cadastro outroUsuario;
    private Escambista mockEscambistaProponente;
    private Escambista mockEscambistaReceptor;
    private Postagem itemDesejado;
    private Postagem itemOferecido;

    @BeforeEach
    public void setup() throws Exception {
        when(cadastroDAO.listarTodos()).thenReturn(List.of(this.usuarioDeTeste));
        when(cadastroDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(this.usuarioDeTeste);
        outroUsuario = new Cadastro();
        outroUsuario.setId(99L);
        outroUsuario.setUserNome("UsuarioReceptor");
        outroUsuario.setEmail("receptor@email.com");

        when(cadastroDAO.encontrarCadastroPorId(this.usuarioDeTeste.getId())).thenReturn(this.usuarioDeTeste);
        when(cadastroDAO.encontrarCadastroPorId(99L)).thenReturn(outroUsuario);
        this.tokenUsuarioLogado = getAuthToken(TEST_USER_EMAIL, TEST_USER_PASS);

        mockEscambistaProponente = new Escambista();
        mockEscambistaProponente.setCpf("12345678900");
        mockEscambistaProponente.setQuerNotifi(true);
        mockEscambistaReceptor = new Escambista();
        mockEscambistaReceptor.setCpf("98765432100");
        mockEscambistaReceptor.setQuerNotifi(true);
        when(escambistaDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(mockEscambistaProponente);
        when(escambistaDAO.encontrarPorUserId(Math.toIntExact(this.usuarioDeTeste.getId()))).thenReturn(mockEscambistaProponente);
        when(escambistaDAO.encontrarPorUserId(Math.toIntExact(99L))).thenReturn(mockEscambistaReceptor);

        itemDesejado = new Postagem();
        itemDesejado.setId(10L);
        itemDesejado.setUserID(99L);
        itemDesejado.setDisponibilidade(true);
        itemDesejado.setDoacao(false);
        itemDesejado.setNomePostagem("Item Desejado Teste");

        itemOferecido = new Postagem();
        itemOferecido.setId(20L);
        itemOferecido.setUserID(this.usuarioDeTeste.getId());
        itemOferecido.setDisponibilidade(true);
        itemOferecido.setNomePostagem("Item Oferecido Teste");

        when(postagemDAO.encontrarPostagemPorId(10L)).thenReturn(itemDesejado);
        when(postagemDAO.encontrarPostagemPorId(20L)).thenReturn(itemOferecido);

        when(propostaDAO.listarPorUsuario(anyLong())).thenReturn(Collections.emptyList());
        when(avaliacaoDAO.calcularMediaPorUsuarioId(anyLong())).thenReturn(Map.of("media", 0.0, "total", 0));
        when(avaliacaoDAO.existePorUsuarioAvaliadorEProposta(anyLong(), anyLong())).thenReturn(false);

        doNothing().when(emailService).enviarAlertaNovaProposta(anyString(), anyString(), anyString(), anyString());
        doNothing().when(emailService).enviarNotificacaoPropostaAceita(anyString(), anyString(), anyString(), anyString());
        doAnswer(invocation -> {
            Proposta p = invocation.getArgument(0);
            p.setId(999L);
            return null;
        }).when(propostaDAO).salvarProposta(any(Proposta.class));
    }

    @Test
    void naoDeveListarPropostas_SemToken() throws Exception {
        mockMvc.perform(get("/api/propostas/listarPropostas")).andExpect(status().isUnauthorized());
    }

    @Test
    void deveListarPropostas_QuandoAutenticado() throws Exception {
        mockMvc.perform(get("/api/propostas/listarPropostas").header("Authorization", "Bearer " + this.tokenUsuarioLogado)).andExpect(status().isOk());
    }

    @Test
    void deveCriarPropostaDeTroca_ComDadosValidos() throws Exception {
        when(propostaDAO.existsByItemDesejadoIdAndItemOferecidoId(10L, 20L)).thenReturn(false);
        mockMvc.perform(post("/api/propostas/criar")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("itemDesejadoId", "10")
                        .param("itemOferecidoId", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Proposta criada com sucesso!"));
    }

    @Test
    void naoDeveCriarProposta_ParaItemProprio() throws Exception {
        mockMvc.perform(post("/api/propostas/criar")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("itemDesejadoId", "20")
                        .param("itemOferecidoId", "20")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Você não pode propor negócio para o seu próprio item."));
    }

    @Test
    void naoDeveCriarProposta_SemItemOferecido_ParaTroca() throws Exception {
        mockMvc.perform(post("/api/propostas/criar")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("itemDesejadoId", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("É necessário informar um item oferecido para troca."));
    }

    @Test
    void deveCancelarProposta_QuandoForOProponente() throws Exception {
        Proposta propostaPendente = new Proposta();
        propostaPendente.setId(10L);
        propostaPendente.setUserId01(this.usuarioDeTeste.getId());
        propostaPendente.setStatus(1);

        when(propostaDAO.encontrarPorId(10L)).thenReturn(propostaPendente);

        mockMvc.perform(post("/api/propostas/acao")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("propostaId", "10")
                        .param("acao", "cancelar")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    void naoDeveRecusarProposta_QuandoForOProponente() throws Exception {
        Proposta propostaPendente = new Proposta();
        propostaPendente.setId(10L);
        propostaPendente.setUserId01(this.usuarioDeTeste.getId());
        propostaPendente.setStatus(1);
        when(propostaDAO.encontrarPorId(10L)).thenReturn(propostaPendente);
        mockMvc.perform(post("/api/propostas/acao")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("propostaId", "10")
                        .param("acao", "recusar")
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Não é possível recusar sua própria proposta."));
    }
}