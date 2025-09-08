package unigran.br.Model.DAO;

import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.AreaMatchVisto;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

@Repository
public class AreaMatchVistoDAO {
    private EntityManagerFactory emf;
    private EntityManager em;

    public AreaMatchVistoDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public void salvar(AreaMatchVisto visto) {
        em.getTransaction().begin();
        em.persist(visto);
        em.getTransaction().commit();
    }

    public boolean existe(Long userId, Long postagemId) {
        Long count = em.createQuery(
                        "SELECT COUNT(v) FROM AreaMatchVisto v WHERE v.userId = :userId AND v.postagemId = :postagemId",
                        Long.class)
                .setParameter("userId", userId)
                .setParameter("postagemId", postagemId)
                .getSingleResult();
        return count > 0;
    }

    public List<Long> listarIdsVistosPorUsuario(Long userId) {
        return em.createQuery(
                        "SELECT v.postagemId FROM AreaMatchVisto v WHERE v.userId = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void fechar() {
        em.close();
        emf.close();
    }

    public boolean existeRegistro(Long userId, Long postagemId) {
        Long count = em.createQuery(
                        "SELECT COUNT(a) FROM AreaMatchVisto a WHERE a.userId = :userId AND a.postagemId = :postagemId",
                        Long.class
                )
                .setParameter("userId", userId)
                .setParameter("postagemId", postagemId)
                .getSingleResult();
        return count > 0;
    }
}
