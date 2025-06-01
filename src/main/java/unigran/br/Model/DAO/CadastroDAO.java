package unigran.br.Model.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import unigran.br.Model.Entidades.Cadastro;
import org.springframework.stereotype.Repository;

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

    public void fechar(){
        em.close();
        emf.close();
    }
}