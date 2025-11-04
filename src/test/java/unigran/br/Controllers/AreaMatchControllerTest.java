package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import unigran.br.Model.DAO.*;
import unigran.br.Model.Entidades.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AreaMatchControllerTest extends BaseIntegrationTest {
    private String tokenUsuarioLogado;

    @MockBean
    private PostagemDAO postagemDAO;
    @MockBean
    private EscambistaDAO escambistaDAO;
    @MockBean
    private CadastroDAO cadastroDAO;
    @MockBean
    private AreaMatchVistoDAO areaMatchVistoDAO;
    @MockBean
    private FavoritoDAO favoritoDAO;
    @MockBean
    private AvaliacaoDAO avaliacaoDAO;

    private Postagem minhaPostagem;
    private Postagem outraPostagemMatch;
    private Cadastro outroUsuario;

    @BeforeEach
    public void setup() throws Exception {
        when(cadastroDAO.listarTodos()).thenReturn(List.of(this.usuarioDeTeste));
        Escambista mockEscambista = new Escambista();
        mockEscambista.setUserNome(TEST_USER_NOME);
        when(escambistaDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(mockEscambista);
        this.tokenUsuarioLogado = getAuthToken(TEST_USER_EMAIL, TEST_USER_PASS);

        when(cadastroDAO.encontrarPorUserNome(TEST_USER_NOME))
                .thenReturn(this.usuarioDeTeste);

        outroUsuario = new Cadastro();
        outroUsuario.setId(99L);
        outroUsuario.setUserNome("OutroUser");

        minhaPostagem = new Postagem();
        minhaPostagem.setId(1L);
        minhaPostagem.setUserID(this.usuarioDeTeste.getId());
        minhaPostagem.setCategoria("Eletronicos");
        minhaPostagem.setCategoriaInteresse1("Livros");

        outraPostagemMatch = new Postagem();
        outraPostagemMatch.setId(10L);
        outraPostagemMatch.setUserID(outroUsuario.getId());
        outraPostagemMatch.setCategoria("Livros");
        outraPostagemMatch.setCategoriaInteresse1("Eletronicos");
        outraPostagemMatch.setDisponibilidade(true);
        outraPostagemMatch.setUserNome(outroUsuario.getUserNome());
        outraPostagemMatch.setNomePostagem("Livro Raro");

        when(postagemDAO.listarPorUserID(this.usuarioDeTeste.getId()))
                .thenReturn(List.of(minhaPostagem));
        when(postagemDAO.listarTodas())
                .thenReturn(List.of(minhaPostagem, outraPostagemMatch));
        when(areaMatchVistoDAO.listarIdsVistosPorUsuario(this.usuarioDeTeste.getId()))
                .thenReturn(Collections.emptyList());
        when(avaliacaoDAO.calcularMediaPorUsuarioId(outroUsuario.getId()))
                .thenReturn(Map.of("media", 4.5, "total", 1));

        when(postagemDAO.encontrarPostagemPorId(10L)).thenReturn(outraPostagemMatch);
        when(postagemDAO.encontrarPostagemPorId(1L)).thenReturn(minhaPostagem);
        when(areaMatchVistoDAO.existeRegistro(this.usuarioDeTeste.getId(), 10L)).thenReturn(false);
        doAnswer(invocation -> invocation.getArgument(0))
                .when(areaMatchVistoDAO).salvar(any(AreaMatchVisto.class));

        when(favoritoDAO.existeFavorito(this.usuarioDeTeste.getId(), 10L)).thenReturn(false);
        doAnswer(invocation -> invocation.getArgument(0))
                .when(favoritoDAO).salvar(any(Favorito.class));
    }

    @Test
    void naoDeveListarMatches_QuandoItemJaVisto() throws Exception {
        when(areaMatchVistoDAO.listarIdsVistosPorUsuario(this.usuarioDeTeste.getId()))
                .thenReturn(List.of(10L));

        mockMvc.perform(get("/api/area-match/matches")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void naoDeveListarMatches_SemToken() throws Exception {
        mockMvc.perform(get("/api/area-match/matches"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveMarcarNaoInteresse() throws Exception {
        mockMvc.perform(post("/api/area-match/interesse")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenUsuarioLogado)
                        .param("itemOutroUsuarioId", "10")
                        .param("sim", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item marcado como 'passado' e não aparecerá mais."));
    }

    @Test
    void deveMarcarInteresse() throws Exception {
        mockMvc.perform(post("/api/area-match/interesse")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenUsuarioLogado)
                        .param("itemOutroUsuarioId", "10")
                        .param("sim", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Interesse registrado com sucesso (não salvo em vistos)."));
    }

    @Test
    void naoDeveMarcarInteresse_NoProprioItem() throws Exception {
        mockMvc.perform(post("/api/area-match/interesse")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenUsuarioLogado)
                        .param("itemOutroUsuarioId", "1")
                        .param("sim", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Não é possível dar ação no próprio item."));
    }
}