package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import unigran.br.Model.DAO.AvaliacaoDAO;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.Entidades.Postagem;
import unigran.br.Services.LocalidadeService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostagemControllerTest extends BaseIntegrationTest {
    private String tokenUsuarioLogado;

    @MockBean
    private CadastroDAO cadastroDAO;

    @MockBean
    private LocalidadeService localidadeService;

    @MockBean
    private PostagemDAO postagemDAO;

    @MockBean
    private AvaliacaoDAO avaliacaoDAO;

    @BeforeEach
    public void setup() throws Exception {
        when(cadastroDAO.listarTodos()).thenReturn(List.of(this.usuarioDeTeste));
        when(cadastroDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(this.usuarioDeTeste);
        this.tokenUsuarioLogado = getAuthToken(TEST_USER_EMAIL, TEST_USER_PASS);
        when(localidadeService.getUFsValidas()).thenReturn(Set.of("MS", "SP", "RJ"));
        when(localidadeService.getCidadesPorUF("MS")).thenReturn(Set.of("Dourados", "Campo Grande"));
        when(avaliacaoDAO.calcularMediaPorUsuarioId(anyLong())).thenReturn(Map.of("media", 5.0, "total", 1));

        when(postagemDAO.listarPorUserID(any())).thenReturn(Collections.emptyList());
        doAnswer(invocation -> {
            Postagem p = invocation.getArgument(0);
            p.setId(999L);
            return null;
        }).when(postagemDAO).salvarPostagem(any(Postagem.class));

        Postagem postagemFalsa = new Postagem();
        postagemFalsa.setId(1L);
        postagemFalsa.setUserNome(TEST_USER_NOME);
        when(postagemDAO.encontrarPostagemPorId(1L)).thenReturn(postagemFalsa);
    }

    @Test
    void deveSalvarPostagemCompleta_ComDadosValidos() throws Exception {
        MockMultipartFile imagemCapa = new MockMultipartFile(
                "imagem",
                "capa.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteudo-da-imagem-fake".getBytes()
        );

        mockMvc.perform(multipart("/api/postagens").file(imagemCapa)
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("userNome", TEST_USER_NOME)
                        .param("isProdOuServico", "1")
                        .param("isDoacao", "0")
                        .param("nomePostagem", "Produto Teste")
                        .param("descricao", "Descrição do produto")
                        .param("categoria", "Tecnologia")
                        .param("categoriaInteresse1", "Videogames")
                        .param("cidade", "Dourados")
                        .param("uf", "MS")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(999L))
                .andExpect(jsonPath("$.message").value("Postagem e imagens salvas com sucesso!"));
    }


    @Test
    void deveListarPostagensDoUsuario_QuandoAutenticado() throws Exception {
        mockMvc.perform(get("/api/postagens/listar")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveListarPostagens_QuandoTokenForInvalido() throws Exception {
        mockMvc.perform(get("/api/postagens/listar")
                        .header("Authorization", "Bearer " + "um.token.falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoDeveListarPostagens_SemToken() throws Exception {
        mockMvc.perform(get("/api/postagens/listar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoDeveSalvarPostagem_QuandoUFForInvalida() throws Exception {
        mockMvc.perform(multipart("/api/postagens")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("userNome", TEST_USER_NOME)
                        .param("isProdOuServico", "1")
                        .param("isDoacao", "0")
                        .param("nomePostagem", "Produto Teste")
                        .param("descricao", "Descrição do produto")
                        .param("categoria", "Tecnologia")
                        .param("categoriaInteresse1", "Videogames")
                        .param("cidade", "Dourados")
                        .param("uf", "XX")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("UF inválida."));
    }

    @Test
    void deveDeletarPostagem_QuandoEnviadoIdValido() throws Exception {
        Postagem postagemFalsa = new Postagem();
        postagemFalsa.setId(1L);
        postagemFalsa.setUserNome(TEST_USER_NOME);
        when(postagemDAO.encontrarPostagemPorId(1L)).thenReturn(postagemFalsa);
        mockMvc.perform(delete("/api/postagens/1")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Postagem removida com sucesso!"));
    }
}