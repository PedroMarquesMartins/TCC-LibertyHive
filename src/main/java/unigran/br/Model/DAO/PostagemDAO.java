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
    private EntityManager em;

    public PostagemDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public void salvarPostagem(Postagem postagem) {
        postagem.setId(null);
        em.getTransaction().begin();
        em.persist(postagem);
        em.getTransaction().commit();
    }

    public void atualizarPostagem(Postagem postagem) {
        em.getTransaction().begin();
        em.merge(postagem);
        em.getTransaction().commit();
    }

    public Postagem encontrarPostagemPorId(Long id) {
        return em.find(Postagem.class, id);
    }

    public void removerPostagem(Long id) {
        Postagem postagem = encontrarPostagemPorId(id);
        if (postagem != null) {
            em.getTransaction().begin();
            em.remove(postagem);
            em.getTransaction().commit();
        }
    }

    public void fechar() {
        em.close();
        emf.close();
    }

    public List<Postagem> listarTodas() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Postagem> result = em.createQuery("SELECT p FROM Postagem p", Postagem.class)
                    .getResultList();
            return result != null ? result : Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Postagem> listarPorUserNome(String userNome) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Postagem p WHERE p.userNome = :userNome", Postagem.class)
                    .setParameter("userNome", userNome)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Postagem> listarPorUserID(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Postagem p WHERE p.userID = :userId",
                    Postagem.class
            ).setParameter("userId", userId).getResultList();
        } finally {
            em.close();
        }
    }
}