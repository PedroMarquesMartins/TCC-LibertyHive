package unigran.br.Model.Entidades;

import javax.persistence.*;

@Entity
@Table(name = "area_match_vistos")
public class AreaMatchVisto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long postagemId;

    public AreaMatchVisto() {}

    public AreaMatchVisto(Long userId, Long postagemId) {
        this.userId = userId;
        this.postagemId = postagemId;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPostagemId() { return postagemId; }
    public void setPostagemId(Long postagemId) { this.postagemId = postagemId; }
}
