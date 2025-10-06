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

    public PropostaDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void salvarProposta(Proposta proposta) {
        EntityManager em = getEntityManager();
        proposta.setId(null);
        try {
            em.getTransaction().begin();
            em.persist(proposta);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void atualizarProposta(Proposta proposta) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(proposta);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Proposta encontrarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proposta.class, id);
        } finally {
            em.close();
        }
    }

    public void removerProposta(Long id) {
        EntityManager em = getEntityManager();
        try {
            Proposta proposta = em.find(Proposta.class, id);
            if (proposta != null) {
                em.getTransaction().begin();
                em.remove(proposta);
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Proposta> listarTodas() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Proposta p", Proposta.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Proposta> listarPorUsuario(Long userId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Proposta p WHERE p.userId01 = :userId OR p.userId02 = :userId",
                            Proposta.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByItemDesejadoIdAndItemOferecidoId(Long itemDesejadoId, Long itemOferecidoId) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Proposta p WHERE p.itemDesejadoId = :itemDesejadoId AND p.itemOferecidoId = :itemOferecidoId",
                            Long.class)
                    .setParameter("itemDesejadoId", itemDesejadoId)
                    .setParameter("itemOferecidoId", itemOferecidoId)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }

    public boolean existsByItemDesejadoIdAndUserId01(Long itemDesejadoId, Long userId01) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Proposta p WHERE p.itemDesejadoId = :itemDesejadoId AND p.userId01 = :userId01",
                            Long.class)
                    .setParameter("itemDesejadoId", itemDesejadoId)
                    .setParameter("userId01", userId01)
                    .getSingleResult();
            return count != null && count > 0;
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