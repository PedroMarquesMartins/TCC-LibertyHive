package unigran.br.Model.TestesDAO;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import unigran.br.Model.Entidades.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TesteEntidadesTest {

    @Test
    @Transactional
    public void testSalvarEntidades() {
        GenericDAO dao = new GenericDAO();

        try {
            Chat chat = new Chat();
            chat.setMensagem("Mensagem teste");
            chat.setValorProposto(99.99);
            chat.setBloqueado(false);
            dao.salvar(chat);
            assertNotNull(chat);

            Escambista esc = new Escambista();
            esc.setUserNome("usuario01");
            esc.setNomeEscambista("Carlos");
            esc.setAvaliacao(4);
            esc.setContato("9999-9999");
            esc.setEmail("carlos@email.com");
            esc.setEndereco("Rua A, 123");
            dao.salvar(esc);
            assertNotNull(esc);

            /*
            Refazer teste!!!!


            Postagem post = new Postagem();
            post.setUserNome("usuario01");
            post.setNomePostagem("Oferta");
            post.setDescricaoTexto("Descrição da postagem");
            post.setCategoriaNome("Eletrônicos");
            post.setCep("12345-678");
            dao.salvar(post);
            assertNotNull(post);
*/


            Proposta prop = new Proposta();
            prop.setStatus(1);
            prop.setItemDesejadoId(4);
            prop.setItemOferecidoId(3);
            prop.setAvaliarPerfil(5);
            dao.salvar(prop);
            assertNotNull(prop);

        } catch (Exception e) {
            fail("Erro ao salvar entidades: " + e.getMessage());
        } finally {
            dao.fechar();
        }
    }
}