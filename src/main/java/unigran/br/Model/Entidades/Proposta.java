package unigran.br.Model.Entidades;

import javax.persistence.*;

@Entity
@Table(name = "proposta") // garante o nome exato da tabela
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer status;

    @Column(name = "userId01", nullable = false)
    private Integer userId01;

    @Column(name = "userId02")
    private Integer userId02;

    @Column(name = "itemDesejadoId", nullable = false)
    private Integer itemDesejadoId;

    @Column(name = "itemOferecidoId", nullable = false)
    private Integer itemOferecidoId;

    private Integer avaliarPerfil;

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getItemDesejadoId() {
        return itemDesejadoId;
    }
    public void setItemDesejadoId(Integer itemDesejadoId) {
        this.itemDesejadoId = itemDesejadoId;
    }

    public Integer getItemOferecidoId() {
        return itemOferecidoId;
    }
    public void setItemOferecidoId(Integer itemOferecidoId) {
        this.itemOferecidoId = itemOferecidoId;
    }

    public Integer getAvaliarPerfil() {
        return avaliarPerfil;
    }
    public void setAvaliarPerfil(Integer avaliarPerfil) {
        this.avaliarPerfil = avaliarPerfil;
    }
}