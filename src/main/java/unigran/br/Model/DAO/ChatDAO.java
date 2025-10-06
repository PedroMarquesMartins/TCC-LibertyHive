package unigran.br.Model.DAO;

import unigran.br.Model.Entidades.Chat;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ChatDAO {
    private EntityManagerFactory emf;
    private EntityManager em;

    public ChatDAO() {
        emf = Persistence.createEntityManagerFactory("meuBancoDeDados");
        em = emf.createEntityManager();
    }

    public Chat criarOuObterChat(Long userId01, Long userId02, String userNome01, String userNome02) {
        Chat chatExistente = encontrarChatPorUsuarios(userId01, userId02);
        if (chatExistente != null) {
            return chatExistente;
        }

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
    }

    public Chat encontrarChatPorUsuarios(Long userId01, Long userId02) {
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
        }
    }

    public Chat encontrarPorId(Long id) {
        return em.find(Chat.class, id);
    }

    public List<Chat> listarPorUsuario(Long userId) {
        return em.createQuery(
                        "SELECT c FROM Chat c WHERE c.userId01 = :id OR c.userId02 = :id",
                        Chat.class
                )
                .setParameter("id", userId)
                .getResultList();
    }

    public void bloquearUsuario(Long chatId) {
        Chat chat = encontrarPorId(chatId);
        if (chat != null) {
            em.getTransaction().begin();
            chat.setBloqueado(true);
            em.merge(chat);
            em.getTransaction().commit();
        }
    }

    public void atualizarValorProposto(Long chatId, Double valor) {
        Chat chat = encontrarPorId(chatId);
        if (chat != null) {
            em.getTransaction().begin();
            chat.setValorProposto(BigDecimal.valueOf(valor));
            em.merge(chat);
            em.getTransaction().commit();
        }
    }

    public void atualizar(Chat chat) {
        em.getTransaction().begin();
        em.merge(chat);
        em.getTransaction().commit();
    }

    public void fechar() {
        em.close();
        emf.close();
    }
}
