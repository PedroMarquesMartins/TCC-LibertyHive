package unigran.br.Model.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Postagem;

import java.util.Collections;
import java.util.List;

@Repository
public class PostagemDAO {

    private EntityManagerFactory emf;

    public PostagemDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void salvarPostagem(Postagem postagem) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            if (postagem.getId() == null) {
                em.persist(postagem);
            } else {
                em.merge(postagem);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    public Postagem encontrarPostagemPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Postagem.class, id);
        } finally {
            em.close();
        }
    }
    public void atualizarPostagem(Postagem postagem) { EntityManager em = getEntityManager(); try { em.getTransaction().begin(); em.merge(postagem); em.getTransaction().commit(); } catch (Exception e) { if (em.getTransaction().isActive()) { em.getTransaction().rollback(); } throw e; } finally { em.close(); } }

    public void removerPostagem(Long id) {
        EntityManager em = getEntityManager();
        try {
            Postagem postagem = em.find(Postagem.class, id);
            if (postagem != null) {
                em.getTransaction().begin();
                em.remove(postagem);
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void removerPorUserId(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Postagem p WHERE p.userID = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Postagem> listarTodas() {
        EntityManager em = getEntityManager();
        try {
            List<Postagem> result = em.createQuery("SELECT p FROM Postagem p", Postagem.class)
                    .getResultList();
            return result != null ? result : Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Postagem> listarPorUserNome(String userNome) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Postagem p WHERE p.userNome = :userNome", Postagem.class)
                    .setParameter("userNome", userNome)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizarUserNomePostagens(String userNomeAntigo, String userNomeNovo) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("UPDATE Postagem p SET p.userNome = :novoNome WHERE p.userNome = :antigoNome")
                    .setParameter("novoNome", userNomeNovo)
                    .setParameter("antigoNome", userNomeAntigo)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Postagem> listarPorUserID(Long userId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Postagem p WHERE p.userID = :userId",
                    Postagem.class
            ).setParameter("userId", userId).getResultList();
        } finally {
            em.close();
        }
    }

    public void fechar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public void tornarIndisponiveisPorUserId(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("UPDATE Postagem p SET p.disponibilidade = false WHERE p.userID = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

}
