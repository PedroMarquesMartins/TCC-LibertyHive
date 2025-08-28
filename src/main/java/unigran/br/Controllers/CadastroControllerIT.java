package unigran.br.Controllers;

import unigran.br.Model.Entidades.Cadastro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CadastroControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;
//Classe de teste de cadastro
    @Test
    void salvarCadastroAPI_DeveRetornarSucesso() {
        Cadastro cadastro = new Cadastro();
        cadastro.setEmail("joao@email.com");
        cadastro.setUserNome("joao123");
        cadastro.setSenha("1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Cadastro> request = new HttpEntity<>(cadastro, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/cadastros", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("sucesso"));
    }
}