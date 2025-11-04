package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import unigran.br.Model.DAO.*;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Escambista;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EscambistaControllerTest extends BaseIntegrationTest {

    private String tokenUsuarioLogado;

    @MockBean
    private EscambistaDAO escambistaDAO;
    @MockBean
    private CadastroDAO cadastroDAO;
    @MockBean
    private AvaliacaoDAO avaliacaoDAO;
    @MockBean
    private FavoritoDAO favoritoDAO;
    @MockBean
    private AreaMatchVistoDAO areaMatchDAO;
    @MockBean
    private ChatDAO chatDAO;
    @MockBean
    private PropostaDAO propostaDAO;
    @MockBean
    private PostagemDAO postagemDAO;

    private Escambista mockEscambista;

    @BeforeEach
    public void setup() throws Exception {

        mockEscambista = new Escambista();
        mockEscambista.setId(1L);
        mockEscambista.setUserId(this.usuarioDeTeste.getId().intValue());
        mockEscambista.setUserNome(this.usuarioDeTeste.getUserNome());
        mockEscambista.setCpf("123.456.789-00");
        mockEscambista.setContato("+5567999998888");
        mockEscambista.setQuerNotifi(true);

        when(cadastroDAO.listarTodos())
                .thenReturn(List.of(this.usuarioDeTeste));
        when(escambistaDAO.encontrarPorUserNome(TEST_USER_NOME))
                .thenReturn(mockEscambista);


        this.tokenUsuarioLogado = getAuthToken(TEST_USER_EMAIL, TEST_USER_PASS);
        when(escambistaDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(mockEscambista);
        when(cadastroDAO.encontrarCadastroPorId(this.usuarioDeTeste.getId()))
                .thenReturn(this.usuarioDeTeste);
        when(escambistaDAO.encontrarPorUserId(this.usuarioDeTeste.getId().intValue()))
                .thenReturn(mockEscambista);

        doNothing().when(chatDAO).removerChatsEMensagensPorUserId(anyLong());
        doNothing().when(favoritoDAO).removerPorUserId(anyLong());
        doNothing().when(areaMatchDAO).removerPorUserId(anyLong());
        doNothing().when(propostaDAO).cancelarPorUserId(anyLong());
        doNothing().when(postagemDAO).tornarIndisponiveisPorUserId(anyLong());
        doNothing().when(avaliacaoDAO).zerarAvaliacoesPorUserId(anyLong());
        doNothing().when(escambistaDAO).atualizarEscambista(any(Escambista.class));
        doAnswer(invocation -> invocation.getArgument(0))
                .when(cadastroDAO).salvarOuAtualizar(any(Cadastro.class));
    }



    @Test
    void deveBuscarEscambista_QuandoEncontrado() throws Exception {
        mockMvc.perform(get("/api/escambista/porUserNome/" + TEST_USER_NOME)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNome").value(TEST_USER_NOME))
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"));
    }

    @Test
    void naoDeveBuscarEscambista_QuandoNaoEncontrado() throws Exception {
        when(escambistaDAO.encontrarPorUserNome("usuarioInexistente")).thenReturn(null);

        mockMvc.perform(get("/api/escambista/porUserNome/usuarioInexistente")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Perfil de escambista não encontrado."));
    }

    @Test
    void naoDeveBuscarEscambista_SemToken() throws Exception {
        mockMvc.perform(get("/api/escambista/porUserNome/" + TEST_USER_NOME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveAtualizarPerfil_ComDadosValidos() throws Exception {
        String dadosAtualizarJson = """
        {
            "nomeEscambista": "TestUser Atualizado",
            "endereco": "Rua dos Testes, 123",
            "contato": "67999991234"
        }
        """;

        when(escambistaDAO.buscarPorCpf(any())).thenReturn(null);

        mockMvc.perform(put("/api/escambista/atualizarPorUserNome/" + TEST_USER_NOME)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosAtualizarJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Perfil atualizado com sucesso."));
    }

    @Test
    void naoDeveAtualizarPerfil_ComCPFInvalido() throws Exception {
        String dadosAtualizarJson = "{\"cpf\": \"11111111111\"}";

        mockMvc.perform(put("/api/escambista/atualizarPorUserNome/" + TEST_USER_NOME)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosAtualizarJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF inválido."));
    }

    @Test
    void naoDeveAtualizarPerfil_ComTelefoneInvalido() throws Exception {
        String dadosAtualizarJson = "{\"contato\": \"123\"}";

        mockMvc.perform(put("/api/escambista/atualizarPorUserNome/" + TEST_USER_NOME)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosAtualizarJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Telefone inválido."));
    }

    @Test
    void deveExcluirConta_ComSenhaCorreta() throws Exception {
        Long userId = this.usuarioDeTeste.getId();
        String payloadSenha = "{\"senha\": \"" + TEST_USER_PASS + "\"}";
        mockMvc.perform(delete("/api/escambista/excluir/" + userId)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadSenha))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Conta excluída com sucesso. Dados pessoais foram removidos e conta desativada."));
    }

    @Test
    void naoDeveExcluirConta_ComSenhaIncorreta() throws Exception {
        Long userId = this.usuarioDeTeste.getId();
        String payloadSenha = "{\"senha\": \"senhaErrada123\"}";

        mockMvc.perform(delete("/api/escambista/excluir/" + userId)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadSenha))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Senha incorreta."));
    }

    @Test
    void naoDeveExcluirConta_SemToken() throws Exception {
        Long userId = this.usuarioDeTeste.getId();
        String payloadSenha = "{\"senha\": \"" + TEST_USER_PASS + "\"}";

        mockMvc.perform(delete("/api/escambista/excluir/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadSenha))
                .andExpect(status().isUnauthorized());
    }
}