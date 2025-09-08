package unigran.br.Model.Entidades;

import javax.persistence.*;

@Entity
@Table(name = "configuracao")
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 14, nullable = false, unique = true)
    private String cpf;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "numero_contato", length = 20)
    private String numeroContato;

    @Column(length = 10)
    private String cep;

    @Column(name = "data_nascimento")
    private java.sql.Date dataNascimento;

    @Column(name = "user_nome", length = 255)
    private String userNome;

    @Column(length = 255, nullable = false)
    private String senha;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroContato() {
        return numeroContato;
    }
    public void setNumeroContato(String numeroContato) {
        this.numeroContato = numeroContato;
    }

    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }

    public java.sql.Date getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(java.sql.Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getUserNome() {
        return userNome;
    }
    public void setUserNome(String userNome) {
        this.userNome = userNome;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
}