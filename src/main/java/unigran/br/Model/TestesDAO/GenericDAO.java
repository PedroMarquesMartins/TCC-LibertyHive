package unigran.br.Model.TestesDAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

//Classe genérica para testar o Mapeamento com o Banco de Dados PostgreSQL
public class GenericDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GenericDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public <T> void salvar(T entidade) {
        em.getTransaction().begin();
        em.persist(entidade);
        em.getTransaction().commit();
    }

    public <T> T encontrarPorId(Class<T> classe, Long id) {
        return em.find(classe, id);
    }

    public void fechar() {
        em.close();
        emf.close();
    }
}