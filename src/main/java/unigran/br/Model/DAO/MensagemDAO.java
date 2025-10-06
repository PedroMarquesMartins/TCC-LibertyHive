package unigran.br.Model.DAO;

import javax.persistence.*;

import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Mensagem;

import java.util.List;


@Repository
public class MensagemDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public MensagemDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public void salvar(Mensagem msg) {
        em.getTransaction().begin();
        em.persist(msg);
        em.getTransaction().commit();
    }

    public List<Mensagem> listarPorChat(Long chatId) {
        return em.createQuery(
                        "SELECT m FROM Mensagem m WHERE m.chatId = :chatId ORDER BY m.dataHora ASC",
                        Mensagem.class
                ).setParameter("chatId", chatId)
                .getResultList();
    }

    public void fechar() {
        em.close();
        emf.close();
    }
}
