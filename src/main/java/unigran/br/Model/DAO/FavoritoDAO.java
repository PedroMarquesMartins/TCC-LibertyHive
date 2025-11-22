package unigran.br.Model.DAO;

import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Favorito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Collections;
import java.util.List;

@Repository
public class FavoritoDAO {

    private EntityManagerFactory emf;

    public FavoritoDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void salvar(Favorito favorito) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            if (favorito.getId() == null) {
                em.persist(favorito);
            } else {
                em.merge(favorito);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Favorito buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Favorito.class, id);
        } finally {
            em.close();
        }
    }

    public List<Favorito> listarPorUserId(Long userId) {
        EntityManager em = getEntityManager();
        try {
            List<Favorito> lista = em.createQuery(
                    "SELECT f FROM Favorito f WHERE f.userId = :userId",
                    Favorito.class
            ).setParameter("userId", userId).getResultList();

            return lista != null ? lista : Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public boolean existeFavorito(Long userId, Long postagemId) {
        EntityManager em = getEntityManager();
        try {
            long count = em.createQuery(
                            "SELECT COUNT(f) FROM Favorito f WHERE f.userId = :userId AND f.postagemId = :postagemId",
                            Long.class
                    )
                    .setParameter("userId", userId)
                    .setParameter("postagemId", postagemId)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    public void remover(Long id) {
        EntityManager em = getEntityManager();
        try {
            Favorito favorito = em.find(Favorito.class, id);
            if (favorito != null) {
                em.getTransaction().begin();
                em.remove(favorito);
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
            em.createQuery("DELETE FROM Favorito f WHERE f.userId = :userId")
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

    public void removerPorPostagemId(Long postagemId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Favorito f WHERE f.postagemId = :postagemId")
                    .setParameter("postagemId", postagemId)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void fechar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
