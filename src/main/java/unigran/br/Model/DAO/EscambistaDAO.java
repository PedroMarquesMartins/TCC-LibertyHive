package unigran.br.Model.DAO;

import javax.persistence.*;
import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Escambista;

import java.util.List;

@Repository
public class EscambistaDAO {

    private EntityManagerFactory emf;

    public EscambistaDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
    }

    public void salvarEscambista(Escambista escambista) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(escambista);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Escambista buscarPorCpf(String cpf) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Escambista e WHERE e.cpf = :cpf", Escambista.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void atualizarEscambista(Escambista escambista) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(escambista);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void atualizarUserNome(String userNomeAntigo, String userNomeNovo) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("UPDATE Escambista e SET e.userNome = :novoNome WHERE e.userNome = :antigoNome")
                    .setParameter("novoNome", userNomeNovo)
                    .setParameter("antigoNome", userNomeAntigo)
                    .executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Escambista encontrarEscambistaPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Escambista.class, id);
        } finally {
            em.close();
        }
    }

    public Escambista encontrarPorUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Escambista e WHERE e.userId = :userId", Escambista.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Escambista encontrarPorUserNome(String userNome) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Escambista e WHERE e.userNome = :userNome", Escambista.class)
                    .setParameter("userNome", userNome)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Escambista> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Escambista e", Escambista.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Escambista buscarPorUserId(int userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Escambista e WHERE e.userId = :userId", Escambista.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void removerEscambista(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Escambista escambista = em.find(Escambista.class, id);
            if (escambista != null) {
                em.getTransaction().begin();
                em.remove(escambista);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }

    public void fechar() {
        emf.close();
    }
}
