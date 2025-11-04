package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.ChatDAO;
import unigran.br.Model.DAO.EscambistaDAO;
import unigran.br.Model.DAO.MensagemDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Chat;
import unigran.br.Model.Entidades.Escambista;
import unigran.br.Model.Entidades.Mensagem;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ChatControllerTest extends BaseIntegrationTest {

    private String tokenUsuarioLogado;

    @MockBean
    private ChatDAO chatDAO;
    @MockBean
    private MensagemDAO mensagemDAO;
    @MockBean
    private CadastroDAO cadastroDAO;
    @MockBean
    private EscambistaDAO escambistaDAO;
    private Chat mockChat;
    private Cadastro outroUsuario;

    @BeforeEach
    public void setup() throws Exception {

        when(cadastroDAO.listarTodos())
                .thenReturn(List.of(this.usuarioDeTeste));
        Escambista mockEscambista = new Escambista();
        mockEscambista.setUserNome(TEST_USER_NOME);
        when(escambistaDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(mockEscambista);
        this.tokenUsuarioLogado = getAuthToken(TEST_USER_EMAIL, TEST_USER_PASS);


        outroUsuario = new Cadastro();
        outroUsuario.setId(99L);
        outroUsuario.setUserNome("OutroUser");



        mockChat = new Chat();
        mockChat.setId(1L);
        mockChat.setUserId01(this.usuarioDeTeste.getId());
        mockChat.setUserNome01(this.usuarioDeTeste.getUserNome());
        mockChat.setUserId02(outroUsuario.getId());
        mockChat.setUserNome02(outroUsuario.getUserNome());
        mockChat.setBloqueado(false);

        when(chatDAO.criarOuObterChat(anyLong(), anyLong(), anyString(), anyString())).thenReturn(mockChat);



        when(chatDAO.encontrarPorId(1L)).thenReturn(mockChat);
        when(chatDAO.encontrarPorId(2L)).thenReturn(null);
        when(mensagemDAO.listarPorChat(1L)).thenReturn(Collections.emptyList());

        doAnswer(invocation -> {
            Mensagem msg = invocation.getArgument(0);

            try {
                Field idField = Mensagem.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(msg, 123L);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).when(mensagemDAO).salvar(any(Mensagem.class));
        when(chatDAO.listarPorUsuario(this.usuarioDeTeste.getId())).thenReturn(List.of(mockChat));
    }

    @Test
    void deveCriarOuObterChat() throws Exception {
        mockMvc.perform(post("/chat/criar")
                        .param("userId01", this.usuarioDeTeste.getId().toString())
                        .param("userId02", outroUsuario.getId().toString())
                        .param("userNome01", this.usuarioDeTeste.getUserNome())
                        .param("userNome02", outroUsuario.getUserNome()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userNome01").value(TEST_USER_NOME));
    }

    @Test
    void deveAlternarBloqueio() throws Exception {
        mockChat.setBloqueado(false);
        mockMvc.perform(put("/chat/1/bloquear"))
                .andExpect(status().isOk())
                .andExpect(content().string("Chat bloqueado."));

        mockChat.setBloqueado(true);
        mockMvc.perform(put("/chat/1/bloquear"))
                .andExpect(status().isOk())
                .andExpect(content().string("Chat desbloqueado."));
    }

    @Test
    void naoDeveBloquearChatInexistente() throws Exception {
        mockMvc.perform(put("/chat/2/bloquear"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Chat não encontrado."));
    }

    @Test
    void deveAtualizarValorProposto() throws Exception {
        String jsonBody = "{\"valorProposto\": 150.50}";
        mockMvc.perform(put("/chat/1/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Valor proposto atualizado com sucesso."));
    }

    @Test
    void naoDeveAtualizarValor_ComFormatoInvalido() throws Exception {
        String jsonBody = "{\"valorProposto\": \"abcde\"}";
        mockMvc.perform(put("/chat/1/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Valor proposto inválido."));
    }

    @Test
    void deveObterMensagensPorChat() throws Exception {
        Mensagem msg = new Mensagem(1L, this.usuarioDeTeste.getId(), "Ola");
        when(mensagemDAO.listarPorChat(1L)).thenReturn(List.of(msg));

        mockMvc.perform(get("/chat/1/mensagens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mensagem").value("Ola"));
    }

    @Test
    void deveEnviarMensagem_QuandoAutenticado() throws Exception {
        String jsonBody = "{\"mensagem\": \"Oi, tudo bem?\"}";

        mockMvc.perform(post("/chat/1/mensagem")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123L))
                .andExpect(jsonPath("$.mensagem").value("Oi, tudo bem?"));
    }

    @Test
    void naoDeveEnviarMensagem_ComTokenInvalido() throws Exception {
        String jsonBody = "{\"mensagem\": \"...\"}";
        mockMvc.perform(post("/chat/1/mensagem")
                        .header("Authorization", "Bearer " + "um.token.falso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token inválido."));
    }

    @Test
    void naoDeveEnviarMensagem_QuandoChatBloqueado() throws Exception {
        mockChat.setBloqueado(true);
        String jsonBody = "{\"mensagem\": \"...\"}";

        mockMvc.perform(post("/chat/1/mensagem")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Chat bloqueado. Não é possível enviar mensagens."));

        mockChat.setBloqueado(false);
    }

    @Test
    void deveListarChatsPorUsuario() throws Exception {
        mockMvc.perform(get("/chat/usuario/" + this.usuarioDeTeste.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userNome01").value(TEST_USER_NOME));
    }
}