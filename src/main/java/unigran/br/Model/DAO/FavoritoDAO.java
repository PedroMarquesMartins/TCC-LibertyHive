package unigran.br.Model.DAO;

import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Favorito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

@Repository
public class FavoritoDAO {
    private EntityManagerFactory emf;
    private EntityManager em;

    public FavoritoDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public void salvar(Favorito favorito) {
        try {
            em.getTransaction().begin();
            em.persist(favorito);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public Favorito buscarPorId(Long id) {
        return em.find(Favorito.class, id);
    }

    public List<Favorito> listarPorUserId(Long userId) {
        return em.createQuery("SELECT f FROM Favorito f WHERE f.userId = :userId", Favorito.class)
                .setParameter("userId", userId)
                .getResultList();
    }
    public boolean existeFavorito(Long userId, Long postagemId) {
        long count = em.createQuery("SELECT COUNT(f) FROM Favorito f WHERE f.userId = :userId AND f.postagemId = :postagemId", Long.class)
                .setParameter("userId", userId)
                .setParameter("postagemId", postagemId)
                .getSingleResult();
        return count > 0;
    }

    public void remover(Long id) {
        Favorito favorito = buscarPorId(id);
        if (favorito != null) {
            try {
                em.getTransaction().begin();
                em.remove(favorito);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }

    public void fechar() {
        em.close();
        emf.close();
    }
}
