package unigran.br.Model.DAO;

import unigran.br.Model.Entidades.Chat;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ChatDAO {
    private final EntityManagerFactory emf;

    public ChatDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
    }

    // --- Criar novo EntityManager para cada operação ---
    private EntityManager em() {
        return emf.createEntityManager();
    }

    public Chat criarOuObterChat(Long userId01, Long userId02, String userNome01, String userNome02) {
        EntityManager em = em();
        try {
            Chat chatExistente = encontrarChatPorUsuarios(userId01, userId02);
            if (chatExistente != null) return chatExistente;

            Chat novoChat = new Chat();
            novoChat.setUserId01(userId01);
            novoChat.setUserId02(userId02);
            novoChat.setUserNome01(userNome01);
            novoChat.setUserNome02(userNome02);
            novoChat.setBloqueado(false);
            novoChat.setValorProposto(null);

            em.getTransaction().begin();
            em.persist(novoChat);
            em.getTransaction().commit();
            return novoChat;
        } finally {
            em.close();
        }
    }

    public Chat encontrarChatPorUsuarios(Long userId01, Long userId02) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT c FROM Chat c WHERE (c.userId01 = :id1 AND c.userId02 = :id2) OR (c.userId01 = :id2 AND c.userId02 = :id1)",
                            Chat.class
                    )
                    .setParameter("id1", userId01)
                    .setParameter("id2", userId02)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Chat encontrarPorId(Long id) {
        EntityManager em = em();
        try {
            return em.find(Chat.class, id);
        } finally {
            em.close();
        }
    }

    public List<Chat> listarPorUsuario(Long userId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT c FROM Chat c WHERE c.userId01 = :id OR c.userId02 = :id",
                            Chat.class
                    )
                    .setParameter("id", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizarUserNome(String nomeAntigo, String nomeNovo) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("UPDATE Chat c SET c.userNome01 = :novo WHERE c.userNome01 = :antigo")
                    .setParameter("novo", nomeNovo)
                    .setParameter("antigo", nomeAntigo)
                    .executeUpdate();

            em.createQuery("UPDATE Chat c SET c.userNome02 = :novo WHERE c.userNome02 = :antigo")
                    .setParameter("novo", nomeNovo)
                    .setParameter("antigo", nomeAntigo)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void atualizarValorProposto(Long chatId, Double valor) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Chat chat = em.find(Chat.class, chatId);
            if (chat != null) {
                chat.setValorProposto(BigDecimal.valueOf(valor));
                em.merge(chat);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // --- Este é o método que faltava funcionar corretamente ---
    public void atualizar(Chat chat) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(chat);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void bloquearUsuario(Long chatId) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Chat chat = em.find(Chat.class, chatId);
            if (chat != null) {
                chat.setBloqueado(true);
                em.merge(chat);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void removerChatsEMensagensPorUserId(Long userId) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Mensagem m WHERE m.userId = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();

            em.createQuery("DELETE FROM Chat c WHERE c.userId01 = :userId OR c.userId02 = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    public void fechar() {
        emf.close();
    }
}
