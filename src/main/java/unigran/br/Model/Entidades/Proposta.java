package unigran.br.Model.Entidades;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    private LocalDateTime dataHora = LocalDateTime.now();

    public Proposta() {}

    public Proposta(Integer status, Long userId01, Long userId02, Long itemDesejadoId, Long itemOferecidoId) {
        this.status = status;
        this.userId01 = userId01;
        this.userId02 = userId02;
        this.itemDesejadoId = itemDesejadoId;
        this.itemOferecidoId = itemOferecidoId;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getUserId01() { return userId01; }
    public void setUserId01(Long userId01) { this.userId01 = userId01; }

    public Long getUserId02() { return userId02; }
    public void setUserId02(Long userId02) { this.userId02 = userId02; }

    public Long getItemDesejadoId() { return itemDesejadoId; }
    public void setItemDesejadoId(Long itemDesejadoId) { this.itemDesejadoId = itemDesejadoId; }

    public Long getItemOferecidoId() { return itemOferecidoId; }
    public void setItemOferecidoId(Long itemOferecidoId) { this.itemOferecidoId = itemOferecidoId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
