package unigran.br.Model.DAO;

import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Proposta;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

@Repository
public class PropostaDAO {
    private EntityManagerFactory emf;
    private EntityManager em;

    public PropostaDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public void salvarProposta(Proposta proposta) {
        proposta.setId(null);
        em.getTransaction().begin();
        em.persist(proposta);
        em.getTransaction().commit();
    }

    public void atualizarProposta(Proposta proposta) {
        em.getTransaction().begin();
        em.merge(proposta);
        em.getTransaction().commit();
    }

    public Proposta encontrarPorId(Long id) {
        return em.find(Proposta.class, id);
    }

    public void removerProposta(Long id) {
        Proposta proposta = encontrarPorId(id);
        if (proposta != null) {
            em.getTransaction().begin();
            em.remove(proposta);
            em.getTransaction().commit();
        }
    }

    public List<Proposta> listarTodas() {
        return em.createQuery("SELECT p FROM Proposta p", Proposta.class).getResultList();
    }

    public void fechar() {
        em.close();
        emf.close();
    }

    public List<Proposta> listarPorUsuario(Long userId) {
        return em.createQuery(
                        "SELECT p FROM Proposta p WHERE p.userId01 = :userId OR p.userId02 = :userId",
                        Proposta.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}