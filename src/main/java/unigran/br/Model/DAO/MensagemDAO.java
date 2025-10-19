package unigran.br.Model.DAO;

import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Mensagem;

import javax.persistence.*;
import java.util.List;

@Repository
public class MensagemDAO {

    private EntityManagerFactory emf;

    public MensagemDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
    }

    public void salvar(Mensagem msg) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(msg);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Mensagem> listarPorChat(Long chatId) {
        EntityManager em = emf.createEntityManager();
        List<Mensagem> mensagens = null;
        try {
            mensagens = em.createQuery(
                            "SELECT m FROM Mensagem m WHERE m.chatId = :chatId ORDER BY m.dataHora ASC",
                            Mensagem.class
                    ).setParameter("chatId", chatId)
                    .getResultList();
        } finally {
            em.close();
        }
        return mensagens;
    }

    public void fechar() {
        if (emf.isOpen()) emf.close();
    }
}
