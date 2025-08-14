package unigran.br.Model.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Escambista;

import java.util.List;

@Repository
public class EscambistaDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public EscambistaDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public void salvarEscambista(Escambista escambista) {
        em.getTransaction().begin();
        em.persist(escambista);
        em.getTransaction().commit();
    }

    public void atualizarEscambista(Escambista escambista) {
        em.getTransaction().begin();
        em.merge(escambista);
        em.getTransaction().commit();
    }

    public Escambista encontrarEscambistaPorId(Long id) {
        return em.find(Escambista.class, id);
    }

    public void removerEscambista(Long id) {
        Escambista escambista = encontrarEscambistaPorId(id);
        if (escambista != null) {
            em.getTransaction().begin();
            em.remove(escambista);
            em.getTransaction().commit();
        }
    }

    public List<Escambista> listarTodos() {
        return em.createQuery("SELECT e FROM Escambista e", Escambista.class).getResultList();
    }

    public void fechar() {
        em.close();
        emf.close();
    }
}