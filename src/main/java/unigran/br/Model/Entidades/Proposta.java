package unigran.br.Model.Entidades;

import javax.persistence.*;

@Entity
@Table(name = "proposta")
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer status;

    private Long userId01;
    private Long userId02;

    private Long itemDesejadoId;
    private Long itemOferecidoId;


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

    public Long getItemDesejadoId() {
        return itemDesejadoId;
    }
    public void setItemDesejadoId(Long itemDesejadoId) {
        this.itemDesejadoId = itemDesejadoId;
    }

    public Long getItemOferecidoId() {
        return itemOferecidoId;
    }
    public void setItemOferecidoId(Long itemOferecidoId) {
        this.itemOferecidoId = itemOferecidoId;
    }

}