package unigran.br.Model.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
    public Escambista buscarPorCpf(String cpf) {
        try {
            return em.createQuery("SELECT e FROM Escambista e WHERE e.cpf = :cpf", Escambista.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void atualizarEscambista(Escambista escambista) {
        em.getTransaction().begin();
        em.merge(escambista);
        em.getTransaction().commit();
    }
    public void atualizarUserNome(String userNomeAntigo, String userNomeNovo) {
        em.getTransaction().begin();
        em.createQuery("UPDATE Escambista e SET e.userNome = :novoNome WHERE e.userNome = :antigoNome")
                .setParameter("novoNome", userNomeNovo)
                .setParameter("antigoNome", userNomeAntigo)
                .executeUpdate();
        em.getTransaction().commit();
    }
    public Escambista encontrarEscambistaPorId(Long id) {
        return em.find(Escambista.class, id);
    }

    public Escambista encontrarPorUserId(Integer userId) {
        try {
            return em.createQuery("SELECT e FROM Escambista e WHERE e.userId = :userId", Escambista.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public Escambista encontrarPorUserNome(String userNome) {
        try {
            return em.createQuery("SELECT e FROM Escambista e WHERE e.userNome = :userNome", Escambista.class)
                    .setParameter("userNome", userNome)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public List<Escambista> listarTodos() {
        return em.createQuery("SELECT e FROM Escambista e", Escambista.class).getResultList();
    }
    public Escambista buscarPorUserId(int userId) {
        try {
            return em.createQuery(
                            "SELECT e FROM Escambista e WHERE e.userId = :userId", Escambista.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    public void removerEscambista(Long id) {
        Escambista escambista = encontrarEscambistaPorId(id);
        if (escambista != null) {
            em.getTransaction().begin();
            em.remove(escambista);
            em.getTransaction().commit();
        }
    }

    public void fechar() {
        em.close();
        emf.close();
    }
}