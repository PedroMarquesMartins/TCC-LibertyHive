package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.Entidades.Postagem;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/postagens")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PostagemController {

    @Autowired
    private PostagemDAO postagemDAO;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> salvarPostagem(
            @RequestParam("userNome") String userNome,
            @RequestParam("isProdOuServico") Integer isProdOuServico,
            @RequestParam("nomePostagem") String nomePostagem,
            @RequestParam("descricao") String descricao,
            @RequestParam("categoria") String categoria,
            @RequestParam("categoriaInteresse") String categoriaInteresse,
            @RequestParam("cep") String cep,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem
    ) {
        if (nomePostagem == null || nomePostagem.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nome da postagem é obrigatório."));
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Descrição é obrigatória."));
        }
        if (cep == null || !cep.matches("\\d{8}")) {
            return ResponseEntity.badRequest().body(Map.of("error", "CEP inválido."));
        }

        Postagem postagem = new Postagem();
        postagem.setUserNome(userNome);
        postagem.setIsProdOuServico(isProdOuServico != null && isProdOuServico == 1);
        postagem.setNomePostagem(nomePostagem);
        postagem.setDescricao(descricao);
        postagem.setCategoria(categoria);
        postagem.setCategoriaInteresse(categoriaInteresse);
        postagem.setCep(cep);

        if (imagem != null && !imagem.isEmpty()) {
            try {
                postagem.setImagem(imagem.getBytes());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Erro ao processar imagem."));
            }
        }

        postagemDAO.salvarPostagem(postagem);

        return ResponseEntity.ok(Map.of("message", "Postagem salva com sucesso!"));
    }

    @GetMapping
    public List<Postagem> listarPostagens() {
        return postagemDAO.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Postagem postagem = postagemDAO.encontrarPostagemPorId(id);
        if (postagem == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Postagem não encontrada."));
        }
        return ResponseEntity.ok(postagem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerPostagem(@PathVariable Long id) {
        postagemDAO.removerPostagem(id);
        return ResponseEntity.ok(Map.of("message", "Postagem removida com sucesso."));
    }

    @GetMapping("/usuario/{userNome}")
public List<Postagem> listarPorUsuario(@PathVariable String userNome) {
    List<Postagem> todas = postagemDAO.listarTodas();

    return todas.stream()
            .filter(p -> p.getUserNome().equalsIgnoreCase(userNome))
            .toList();
}
}