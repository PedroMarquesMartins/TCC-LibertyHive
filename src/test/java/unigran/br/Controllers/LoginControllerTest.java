package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import unigran.br.Model.DAO.CadastroDAO;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest extends BaseIntegrationTest {

    @MockBean
    private CadastroDAO cadastroDAO;

    @BeforeEach
    public void setupMock() {
        when(cadastroDAO.listarTodos())
                .thenReturn(List.of(this.usuarioDeTeste));
    }

    @Test
    void deveLogarComSucesso_ComEmailESenhaCorretos() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "user", TEST_USER_EMAIL,
                "senha", TEST_USER_PASS
        );

        mockMvc.perform(post("/api/login").contentType("application/json").content(objectMapper.writeValueAsString(loginRequest))).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.token").exists()).andExpect(jsonPath("$.userNome").value(TEST_USER_NOME));
    }

    @Test
    void deveFalharLogin_ComSenhaIncorreta() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "user", TEST_USER_EMAIL,
                "senha", "senhaErrada"
        );

        mockMvc.perform(post("/api/login").contentType("application/json").content(objectMapper.writeValueAsString(loginRequest))).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.success").value(false)).andExpect(jsonPath("$.message").value("Usuário ou senha inválidos."));
    }
}