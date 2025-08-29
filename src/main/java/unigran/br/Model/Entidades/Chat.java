package unigran.br.Model.Entidades;

import javax.persistence.*;

@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    private Double valorProposto;

    private Boolean bloqueado;

    @Column(length = 255)
    private String userNome01;

    @Column(length = 255)
    private String userNome02;

    private Integer userId01;

    private Integer userId02;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Double getValorProposto() {
        return valorProposto;
    }

    public void setValorProposto(Double valorProposto) {
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

    public Integer getUserId01() {
        return userId01;
    }

    public void setUserId01(Integer userId01) {
        this.userId01 = userId01;
    }

    public Integer getUserId02() {
        return userId02;
    }

    public void setUserId02(Integer userId02) {
        this.userId02 = userId02;
    }
}
