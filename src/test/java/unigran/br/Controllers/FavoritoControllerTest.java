package unigran.br.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.FavoritoDAO;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.Entidades.Favorito;
import unigran.br.Model.Entidades.Postagem;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavoritoControllerTest extends BaseIntegrationTest {

    private String tokenUsuarioLogado;
    @MockBean
    private FavoritoDAO favoritoDAO;

    @MockBean
    private CadastroDAO cadastroDAO;

    @MockBean
    private PostagemDAO postagemDAO;

    @BeforeEach
    public void setup() throws Exception {
        when(cadastroDAO.listarTodos()).thenReturn(List.of(this.usuarioDeTeste));
        when(cadastroDAO.encontrarPorUserNome(TEST_USER_NOME)).thenReturn(this.usuarioDeTeste);
        this.tokenUsuarioLogado = getAuthToken(TEST_USER_EMAIL, TEST_USER_PASS);
        when(favoritoDAO.existeFavorito(anyLong(), anyLong())).thenReturn(false);
        when(favoritoDAO.listarPorUserId(anyLong())).thenReturn(Collections.emptyList());





        Postagem mockPostagem = new Postagem();
        mockPostagem.setId(100L);
        mockPostagem.setDisponibilidade(true);
        mockPostagem.setNomePostagem("Postagem de Teste para Favoritos");

        when(postagemDAO.encontrarPostagemPorId(100L)).thenReturn(mockPostagem);
    }

    @Test
    void deveListarFavoritos_QuandoAutenticado() throws Exception {
        mockMvc.perform(get("/favoritos")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isOk());
    }

    @Test
    void deveFavoritarItem_ComDadosValidos() throws Exception {
        mockMvc.perform(post("/favoritos")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("postagemId", "100")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item favoritado com sucesso!"));
    }

    @Test
    void naoDeveFavoritarItem_QuandoJaFavoritado() throws Exception {
        when(favoritoDAO.existeFavorito(this.usuarioDeTeste.getId(), 100L)).thenReturn(true);

        mockMvc.perform(post("/favoritos")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado)
                        .param("postagemId", "100")
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Este item já foi favoritado por você."));
    }

    @Test
    void deveRemoverFavorito_QuandoForODono() throws Exception {
        Favorito mockFavorito = new Favorito();
        mockFavorito.setId(50L);
        mockFavorito.setUserId(this.usuarioDeTeste.getId());
        mockFavorito.setPostagemId(100L);
        when(favoritoDAO.buscarPorId(50L)).thenReturn(mockFavorito);
        mockMvc.perform(delete("/favoritos/50")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Favorito removido com sucesso!"));
    }

    @Test
    void naoDeveRemoverFavorito_QuandoNaoForODono() throws Exception {
        Favorito mockFavoritoDeOutro = new Favorito();
        mockFavoritoDeOutro.setId(51L);
        mockFavoritoDeOutro.setUserId(99L);
        mockFavoritoDeOutro.setPostagemId(100L);
        when(favoritoDAO.buscarPorId(51L)).thenReturn(mockFavoritoDeOutro);
        mockMvc.perform(delete("/favoritos/51")
                        .header("Authorization", "Bearer " + this.tokenUsuarioLogado))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Você não tem permissão para remover este favorito."));
    }
}