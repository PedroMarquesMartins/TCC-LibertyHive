package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.EscambistaDAO;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Escambista;
import unigran.br.Services.EmailService;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CadastroControllerTest extends BaseIntegrationTest {

    @MockBean
    private EmailService emailService;
    @MockBean
    private CadastroDAO cadastroDAO;
    @MockBean
    private EscambistaDAO escambistaDAO;
    @MockBean
    private PostagemDAO postagemDAO;

    private String tokenUsuarioLogado;

    @BeforeEach
    public void setup() throws Exception {
        when(cadastroDAO.listarTodos()).thenReturn(List.of(this.usuarioDeTeste));

        Escambista mockEscambista = new Escambista();
        mockEscambista.setUserNome(TEST_USER_NOME);
        when(escambistaDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(mockEscambista);
        this.tokenUsuarioLogado = getAuthToken(TEST_USER_EMAIL, TEST_USER_PASS);




        when(cadastroDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(this.usuarioDeTeste);
        when(cadastroDAO.encontrarPorUserNome("usuarioInexistente")).thenReturn(null);
        when(cadastroDAO.listarTodos()).thenReturn(List.of(this.usuarioDeTeste));

        doAnswer(invocation -> {
            Cadastro c = invocation.getArgument(0);
            try {
                Field idField = Cadastro.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(c, 999L);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).when(cadastroDAO).salvarCadastro(any(Cadastro.class));

        doNothing().when(escambistaDAO).salvarEscambista(any(Escambista.class));
        doNothing().when(emailService).enviarEmailBoasVindas(anyString(), anyString());
        doNothing().when(escambistaDAO).atualizarUserNome(anyString(), anyString());
        doNothing().when(postagemDAO).atualizarUserNomePostagens(anyString(), anyString());
    }

    @Test
    void deveSalvarCadastro_ComDadosValidos() throws Exception {
        when(cadastroDAO.listarTodos()).thenReturn(Collections.emptyList());

        String jsonPayload = """
        {
            "email": "novo@email.com",
            "userNome": "NovoUsuario",
            "senha": "senhaValida123"
        }
        """;

        mockMvc.perform(post("/api/cadastros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cadastro salvo com sucesso!"));
    }

    @Test
    void deveBuscarCadastro_PorUserNome() throws Exception {
        mockMvc.perform(get("/api/cadastros/" + TEST_USER_NOME)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNome").value(TEST_USER_NOME))
                .andExpect(jsonPath("$.email").value(TEST_USER_EMAIL));
    }

    @Test
    void naoDeveBuscarCadastro_QuandoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/cadastros/usuarioInexistente")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarCadastro_EmailESenha() throws Exception {
        String jsonPayload = """
                {
                    "email": "emailAtualizado@email.com",
                    "senha": "novaSenha123"
                }
                """;

        mockMvc.perform(put("/api/cadastros/" + TEST_USER_NOME)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Conta atualizada com sucesso!"));
    }
}