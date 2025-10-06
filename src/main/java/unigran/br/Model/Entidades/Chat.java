package unigran.br.Model.Entidades;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valorProposto;
    private Boolean bloqueado = false;
    private String userNome01;
    private String userNome02;
    private Long userId01;
    private Long userId02;

    public Chat() {}

    public Chat(Long userId01, Long userId02, String userNome01, String userNome02) {
        this.userId01 = userId01;
        this.userId02 = userId02;
        this.userNome01 = userNome01;
        this.userNome02 = userNome02;
        this.bloqueado = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorProposto() {
        return valorProposto;
    }

    public void setValorProposto(BigDecimal valorProposto) {
        this.valorProposto = valorProposto;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getUserNome01() {
        return userNome01;
    }

    public void setUserNome01(String userNome01) {
        this.userNome01 = userNome01;
    }

    public String getUserNome02() {
        return userNome02;
    }

    public void setUserNome02(String userNome02) {
        this.userNome02 = userNome02;
    }

    public Long getUserId01() {
        return userId01;
    }

    public void setUserId01(Long userId01) {
        this.userId01 = userId01;
    }

    public Long getUserId02() {
        return userId02;
    }

    public void setUserId02(Long userId02) {
        this.userId02 = userId02;
    }
}