package unigran.br.Model.Entidades;

import javax.persistence.*;

@Entity
@Table(name = "escambista") // garante que mapeia exatamente essa tabela
public class Escambista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // porque no banco está "not null"
    private Integer userId;

    private String userNome;
    private String nomeEscambista;
    private Integer avaliacao;
    private String contato;
    private String email;
    private String endereco;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserNome() {
        return userNome;
    }

    public void setUserNome(String userNome) {
        this.userNome = userNome;
    }

    public String getNomeEscambista() {
        return nomeEscambista;
    }

    public void setNomeEscambista(String nomeEscambista) {
        this.nomeEscambista = nomeEscambista;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}