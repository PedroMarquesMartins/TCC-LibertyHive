package unigran.br.Model.Entidades;

import javax.persistence.*;
import org.hibernate.annotations.Type;

@Entity
public class Postagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userNome;
    private Boolean isProdOuServico;
    private String nomePostagem;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String descricao;
    private String categoria;
    private String categoriaInteresse;
    private String cep;
    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "imagem")
    private byte[] imagem;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserNome() {
        return userNome;
    }
    public void setUserNome(String userNome) {
        this.userNome = userNome;
    }
    public Boolean getIsProdOuServico() {
        return isProdOuServico;
    }
    public void setIsProdOuServico(Boolean isProdOuServico) {
        this.isProdOuServico = isProdOuServico;
    }
    public String getNomePostagem() {
        return nomePostagem;
    }
    public void setNomePostagem(String nomePostagem) {
        this.nomePostagem = nomePostagem;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getCategoriaInteresse() {
        return categoriaInteresse;
    }
    public void setCategoriaInteresse(String categoriaInteresse) {
        this.categoriaInteresse = categoriaInteresse;
    }
    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }
    public byte[] getImagem() {
        return imagem;
    }
    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }
}







