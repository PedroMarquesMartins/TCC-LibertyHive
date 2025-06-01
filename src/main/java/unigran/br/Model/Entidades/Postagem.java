package unigran.br.Model.Entidades;

import javax.persistence.*;

@Entity
public class Postagem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userNome;
    private String nomePostagem;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descricaoTexto;

    private String categoriaNome;
    private String cep;

    //@Lob
    //@Column(columnDefinition = "BYTEA")
    //private byte[] imagem;


    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
/*
    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
    this.imagem = imagem;
    }*/

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

    public String getNomePostagem() {
        return nomePostagem;
    }

    public void setNomePostagem(String nomePostagem) {
        this.nomePostagem = nomePostagem;
    }

    public String getDescricaoTexto() {
        return descricaoTexto;
    }

    public void setDescricaoTexto(String descricaoTexto) {
        this.descricaoTexto = descricaoTexto;
    }

    public String getCategoriaNome() {
        return categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }
}
/*




+++++++++++++++++++++++++++++++++++++++++++++
Postgre

CREATE TABLE postagem (
    id SERIAL PRIMARY KEY,
    imagem BYTEA
);


Fluxo JS:

const inputFile = document.querySelector('#inputFile'); // input type="file"
const nome = "Minha Postagem";
const usuarioAutor = "usuario123";
const descricao = "Descrição da imagem";

const formData = new FormData();
formData.append("nome", nome);
formData.append("usuarioAutor", usuarioAutor);
formData.append("descricao", descricao);
formData.append("imagem", inputFile.files[0]); // arquivo binário

fetch("http://localhost:8080/api/postagens", {
  method: "POST",
  body: formData
})
.then(res => res.json())
.then(data => console.log(data))
.catch(err => console.error(err));

JAVA

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PostMapping("/postagens")
public ResponseEntity<?> salvarPostagem(
        @RequestParam("nome") String nome,
        @RequestParam("usuarioAutor") String usuarioAutor,
        @RequestParam("descricao") String descricao,
        @RequestParam("imagem") MultipartFile imagem) {

    Postagem postagem = new Postagem();
    postagem.setNome(nome);
    postagem.setUsuarioAutor(usuarioAutor);
    postagem.setDescricao(descricao);
    try {
        postagem.setImagem(imagem.getBytes());
    } catch (IOException e) {
        return ResponseEntity.status(500).body("Erro ao processar imagem");
    }

    postagemDAO.salvarPostagem(postagem); // seu DAO/Repository para salvar

    return ResponseEntity.ok(Map.of("message", "Postagem salva com sucesso"));
}
*/