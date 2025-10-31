package unigran.br.Model.DAO;

import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Avaliacao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.Map;

@Repository
public class AvaliacaoDAO {

    private final EntityManagerFactory emf;

    public AvaliacaoDAO() {
        this.emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean existePorUsuarioAvaliadorEProposta(Long usuarioAvaliadorId, Long propostaId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Avaliacao a " +
                    "WHERE a.usuarioAvaliadorId = :avaliador AND a.propostaId = :proposta";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("avaliador", usuarioAvaliadorId)
                    .setParameter("proposta", propostaId)
                    .getSingleResult();
            return count != null && count > 0;
        } finally {
            em.close();
        }
    }
    public void removerPorUserId(Long userId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Avaliacao a WHERE a.usuarioAvaliadoId = :userId OR a.usuarioAvaliadorId = :userId")
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

    public void salvarAvaliacao(Avaliacao avaliacao) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(avaliacao);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar avaliação", e);
        } finally {
            em.close();
        }
    }
    public Map<String, Object> calcularMediaPorUsuarioId(Long usuarioId) {
        EntityManager em = getEntityManager();
        try {
            Object[] resultado = em.createQuery(
                            "SELECT AVG(CAST(a.nota AS double)), COUNT(a) " +
                                    "FROM Avaliacao a " +
                                    "WHERE a.usuarioAvaliadoId = :usuarioId", Object[].class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();

            Double media = (Double) resultado[0];
            Long quantidade = (Long) resultado[1];

            return Map.of(
                    "media", (media != null) ? media : 0.0,
                    "quantidade", (quantidade != null) ? quantidade : 0L
            );

        } catch (NoResultException e) {
            return Map.of("media", 0.0, "quantidade", 0L);
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
