package unigran.br.TesteAPI.TestesDAO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import unigran.br.Model.Entidades.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TesteEntidadesTest {

    private static GenericDAO dao;
    private static ObjectMapper mapper;

    private static Long cadastroId;
    private static Long escambistaId;
    private static Long postagemId;
    private static Long chatId;
    private static Long mensagemId;
    private static Long propostaId;
    private static Long avaliacaoId;
    private static Long favoritoId;
    private static Long areaMatchVistoId;

    @BeforeAll
    public static void setup() {
        dao = new GenericDAO();
        mapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    public void testSalvarCadastro() {
        Cadastro cad = new Cadastro();
        cad.setUserNome("Matheus Rocha");
        cad.setEmail("matheus.rocha@email.com");
        cad.setSenha("senha123");
        dao.salvar(cad);
        cadastroId = cad.getId();
        Assertions.assertNotNull(cadastroId, "Cadastro deve ter sido criado");
    }

    @Test
    @Order(2)
    public void testSalvarEscambista() {
        Assertions.assertNotNull(cadastroId, "Cadastro deve existir antes do escambista");

        Escambista esc = new Escambista();
        esc.setUserId(cadastroId.intValue());
        esc.setUserNome("Matheus Rocha");
        esc.setNomeEscambista("André Silva");
        esc.setContato("98888-7777");
        esc.setEndereco("Rua B, 456");
        esc.setDataNasc(LocalDate.of(1992, 5, 20));
        esc.setQuerNotifi(true);
        dao.salvar(esc);
        escambistaId = esc.getId();
        Assertions.assertNotNull(escambistaId, "Escambista deve ter sido criado");
    }

    @Test
    @Order(3)
    public void testSalvarPostagem() {
        Assertions.assertNotNull(cadastroId, "Cadastro deve existir antes da postagem");

        Postagem post = new Postagem();
        post.setUserID(cadastroId);
        post.setUserNome("Matheus Rocha");
        post.setNomePostagem("Oferta Especial");
        post.setDescricao("Descrição da postagem de exemplo");
        post.setCategoria("Livros");
        post.setDoacao(false);
        post.setIsProdOuServico(true);
        dao.salvar(post);
        postagemId = post.getId();
        Assertions.assertNotNull(postagemId, "Postagem deve ter sido criada");
    }

    @Test
    @Order(4)
    public void testSalvarChatEMensagem() {
        Assertions.assertNotNull(cadastroId, "Cadastro deve existir antes de criar chat e mensagem");

        Chat chat = new Chat();
        chat.setUserId01(cadastroId);
        chat.setUserId02(3L);
        chat.setUserNome01("Matheus Rocha");
        chat.setUserNome02("Paula Andrade");
        chat.setValorProposto(new BigDecimal("150.75"));
        dao.salvar(chat);
        chatId = chat.getId();
        Assertions.assertNotNull(chatId, "Chat deve ter sido criado");

        Mensagem msg = new Mensagem(chatId, cadastroId, "Mensagem de teste do chat");
        dao.salvar(msg);
        mensagemId = msg.getId();
        Assertions.assertNotNull(mensagemId, "Mensagem deve ter sido criada");
    }

    @Test
    @Order(6)
    public void testSalvarProposta() {
        Assertions.assertNotNull(postagemId, "Postagem deve existir antes da proposta");

        Proposta prop = new Proposta();
        prop.setStatus(1);
        prop.setUserId01(cadastroId);
        prop.setUserId02(4L);
        prop.setItemDesejadoId(postagemId);
        prop.setItemOferecidoId(null);
        dao.salvar(prop);
        propostaId = prop.getId();
        Assertions.assertNotNull(propostaId, "Proposta deve ter sido criada");
    }

    @Test
    @Order(7)
    public void testSalvarAvaliacao() {
        Assertions.assertNotNull(propostaId, "Proposta deve existir antes da avaliação");

        Avaliacao aval = new Avaliacao();
        aval.setUsuarioAvaliadorId(cadastroId);
        aval.setUsuarioAvaliadoId(4L);
        aval.setPropostaId(propostaId);
        aval.setNota(5);
        dao.salvar(aval);
        avaliacaoId = aval.getId();
        Assertions.assertNotNull(avaliacaoId, "Avaliação deve ter sido criada");
    }

    @Test
    @Order(8)
    public void testSalvarFavorito() {
        Assertions.assertNotNull(postagemId, "Postagem deve existir antes do favorito");

        Favorito fav = new Favorito();
        fav.setUserId(cadastroId);
        fav.setPostagemId(postagemId);
        dao.salvar(fav);
        favoritoId = fav.getId();
        Assertions.assertNotNull(favoritoId, "Favorito deve ter sido criado");
    }

    @Test
    @Order(9)
    public void testSalvarAreaMatchVisto() {
        Assertions.assertNotNull(postagemId, "Postagem deve existir antes do AreaMatchVisto");

        AreaMatchVisto area = new AreaMatchVisto(cadastroId, postagemId);
        dao.salvar(area);
        areaMatchVistoId = area.getId();
        Assertions.assertNotNull(areaMatchVistoId, "AreaMatchVisto deve ter sido criado");
    }

    @Test
    @Order(11)
    public void testDesserializacaoChat() throws JsonProcessingException {
        String json = "{\"userId01\":1,\"userId02\":3,\"userNome01\":\"Matheus Rocha\",\"userNome02\":\"Paula Andrade\",\"bloqueado\":false}";
        Chat chat = mapper.readValue(json, Chat.class);
        Assertions.assertEquals("Matheus Rocha", chat.getUserNome01());
        Assertions.assertFalse(chat.getBloqueado());
    }

    @AfterAll
    public static void finalizar() {
        dao.fechar();
    }
}