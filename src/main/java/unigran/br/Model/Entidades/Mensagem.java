package unigran.br.Model.Entidades;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private Long userId;
    private String mensagem;
    private LocalDateTime dataHora = LocalDateTime.now();

    public Mensagem() {}

    public Mensagem(Long chatId, Long userId, String mensagem) {
        this.chatId = chatId;
        this.userId = userId;
        this.mensagem = mensagem;
    }

    public Long getId() { return id; }
    public Long getChatId() { return chatId; }
    public Long getUserId() { return userId; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataHora() { return dataHora; }
}
