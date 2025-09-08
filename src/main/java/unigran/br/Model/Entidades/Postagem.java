package unigran.br.Model.Entidades;

import javax.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "postagem")
public class Postagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userID;
    private String userNome;
    private Boolean isProdOuServico;
    private Boolean isDoacao;
    private String nomePostagem;

    @Column(columnDefinition = "TEXT")
    private String descricao;
    private String categoria;
    private String categoriaInteresse1;
    private String categoriaInteresse2;
    private String categoriaInteresse3;
    private String cidade;
    private String uf;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "imagem")
    private byte[] imagem;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "imagemS01")
    private byte[] imagemS01;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "imagemS02")
    private byte[] imagemS02;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "imagemS03")
    private byte[] imagemS03;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "imagemS04")
    private byte[] imagemS04;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "imagemS05")
    private byte[] imagemS05;

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

    public String getCategoriaInteresse1() {
        return categoriaInteresse1;
    }
    public void setCategoriaInteresse1(String categoriaInteresse1) {
        this.categoriaInteresse1 = categoriaInteresse1;
    }
    public String getCategoriaInteresse2() {
        return categoriaInteresse2;
    }
    public void setCategoriaInteresse2(String categoriaInteresse2) {
        this.categoriaInteresse2 = categoriaInteresse2;
    }
    public String getCategoriaInteresse3() {
        return categoriaInteresse3;
    }
    public void setCategoriaInteresse3(String categoriaInteresse3) {
        this.categoriaInteresse3 = categoriaInteresse3;
    }

    public byte[] getImagem() {
        return imagem;
    }
    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public Boolean getProdOuServico() {
        return isProdOuServico;
    }
    public void setProdOuServico(Boolean prodOuServico) {
        isProdOuServico = prodOuServico;
    }

    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }
    public void setUf(String uf) {
        this.uf = uf;
    }

    public byte[] getImagemS01() {
        return imagemS01;
    }
    public void setImagemS01(byte[] imagemS01) {
        this.imagemS01 = imagemS01;
    }

    public byte[] getImagemS02() {
        return imagemS02;
    }
    public void setImagemS02(byte[] imagemS02) {
        this.imagemS02 = imagemS02;
    }

    public byte[] getImagemS03() {
        return imagemS03;
    }
    public void setImagemS03(byte[] imagemS03) {
        this.imagemS03 = imagemS03;
    }

    public byte[] getImagemS04() {
        return imagemS04;
    }
    public void setImagemS04(byte[] imagemS04) {
        this.imagemS04 = imagemS04;
    }

    public byte[] getImagemS05() {
        return imagemS05;
    }
    public void setImagemS05(byte[] imagemS05) {
        this.imagemS05 = imagemS05;
    }

    public Long getUserID() {
        return userID;
    }
    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Boolean getDoacao() {
        return isDoacao;
    }
    public void setDoacao(Boolean doacao) {
        isDoacao = doacao;
    }
}