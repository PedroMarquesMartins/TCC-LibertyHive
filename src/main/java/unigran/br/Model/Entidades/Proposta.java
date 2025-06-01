package unigran.br.Model.Entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Geração automática do ID
    private Long id;
    private Integer status;
    private String itemDesejado;
    private String itemOferecido;
    private Integer avaliarPerfil; //???????????

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

    public String getItemDesejado() {
        return itemDesejado;
    }

    public void setItemDesejado(String itemDesejado) {
        this.itemDesejado = itemDesejado;
    }

    public String getItemOferecido() {
        return itemOferecido;
    }

    public void setItemOferecido(String itemOferecido) {
        this.itemOferecido = itemOferecido;
    }

    public Integer getAvaliarPerfil() {
        return avaliarPerfil;
    }

    public void setAvaliarPerfil(Integer avaliarPerfil) {
        this.avaliarPerfil = avaliarPerfil;
    }
}