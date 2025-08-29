package unigran.br.Model.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.stereotype.Repository;
import unigran.br.Model.Entidades.Cadastro;

import java.util.List;

@Repository
public class CadastroDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public CadastroDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public void salvarCadastro(Cadastro cadastro) {
        em.getTransaction().begin();
        em.persist(cadastro);
        em.getTransaction().commit();
    }

    public Cadastro encontrarCadastroPorId(Long id) {
        return em.find(Cadastro.class, id);
    }

    public void fechar() {
        em.close();
        emf.close();
    }
    public Cadastro encontrarPorUserNome(String userNome) {
        try {
            return em.createQuery("SELECT c FROM Cadastro c WHERE c.userNome = :userNome", Cadastro.class)
                    .setParameter("userNome", userNome)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    public void removerCadastro(Long id) {
        Cadastro cadastro = em.find(Cadastro.class, id);
        if (cadastro != null) {
            em.getTransaction().begin();
            em.remove(cadastro);
            em.getTransaction().commit();
        }
    }
    public List<Cadastro> listarTodos() {
        return em.createQuery("SELECT c FROM Cadastro c", Cadastro.class).getResultList();
    }
}