package unigran.br.Model.Entidades;

import javax.persistence.*;
@Entity
@Table(name = "avaliacoes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_avaliador_id", "proposta_id"})
})
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_avaliador_id", nullable = false)
    private Long usuarioAvaliadorId;

    @Column(name = "usuario_avaliado_id", nullable = false)
    private Long usuarioAvaliadoId; // <-- CORRETO

    @Column(name = "proposta_id", nullable = false)
    private Long propostaId;

    @Column(nullable = false)
    private Integer nota;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioAvaliadorId() {
        return usuarioAvaliadorId;
    }

    public void setUsuarioAvaliadorId(Long usuarioAvaliadorId) {
        this.usuarioAvaliadorId = usuarioAvaliadorId;
    }

    public Long getUsuarioAvaliadoId() {
        return usuarioAvaliadoId;
    }

    public void setUsuarioAvaliadoId(Long usuarioAvaliadoId) {
        this.usuarioAvaliadoId = usuarioAvaliadoId;
    }

    public Long getPropostaId() {
        return propostaId;
    }

    public void setPropostaId(Long propostaId) {
        this.propostaId = propostaId;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }
}