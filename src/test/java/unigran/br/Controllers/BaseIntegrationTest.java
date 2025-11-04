package unigran.br.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.Entidades.Cadastro;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired(required = false)
    protected CadastroDAO cadastroDAO;

    @Autowired
    protected PasswordEncoder passwordEncoder;
    protected static final String TEST_USER_EMAIL = "testuser@email.com";
    protected static final String TEST_USER_NOME = "TestUser";
    protected static final String TEST_USER_PASS = "senha123";


    protected Cadastro usuarioDeTeste;

    @BeforeEach
    public void setupTestUser() {
        Cadastro novoUsuario = new Cadastro();
        novoUsuario.setUserNome(TEST_USER_NOME);
        novoUsuario.setEmail(TEST_USER_EMAIL);
        novoUsuario.setSenha(passwordEncoder.encode(TEST_USER_PASS));
        novoUsuario.setStatusConta(true);
        novoUsuario.setId(1L);

        this.usuarioDeTeste = novoUsuario;
    }

    protected String getAuthToken(String user, String senha) throws Exception {
        Map<String, String> loginRequest = Map.of(
                "user", user,
                "senha", senha
        );





        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        return (String) responseMap.get("token");
    }
}