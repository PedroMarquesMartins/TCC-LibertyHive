package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.Entidades.Postagem;
import unigran.br.JwtUtil;
import unigran.br.Services.LocalidadeService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/postagens")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PostagemController {

    @Autowired
    private PostagemDAO postagemDAO;
    @Autowired
    private CadastroDAO cadastroDAO;
    @Autowired
    private LocalidadeService localidadeService;

    @Autowired
    private JwtUtil jwtUtil;
//Função Salvar postagem, recebendo os parâmetros do Front-End
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> salvarPostagemCompleta(
            @RequestParam("userNome") String userNome,
            @RequestParam("isProdOuServico") Integer isProdOuServico,
            @RequestParam("isDoacao") Integer isDoacao,
            @RequestParam("nomePostagem") String nomePostagem,
            @RequestParam("descricao") String descricao,
            @RequestParam("categoria") String categoria,
            @RequestParam("categoriaInteresse") String categoriaInteresse,
            @RequestParam("cidade") String cidade,
            @RequestParam("uf") String uf,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem,
            @RequestParam(value = "imagensSecundarias", required = false) List<MultipartFile> imagensSecundarias
    ) {
        //VALIDAÇÕES
        if (nomePostagem == null || nomePostagem.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Nome da postagem é obrigatório."));
        if (descricao == null || descricao.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Descrição é obrigatória."));
        if (!localidadeService.getUFsValidas().contains(uf.toUpperCase()))
            return ResponseEntity.badRequest().body(Map.of("error", "UF inválida."));
        if (!localidadeService.getCidadesPorUF(uf).contains(cidade))
            return ResponseEntity.badRequest().body(Map.of("error", "Cidade não pertence à UF informada ou não existe."));

        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(userNome);
        if (cadastro == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Usuário não encontrado."));
//Salvando Postagem
        Postagem postagem = new Postagem();
        postagem.setId(null);
        postagem.setUserNome(userNome);
        postagem.setDoacao(isDoacao != null && isDoacao == 1); //Nova adição
        postagem.setIsProdOuServico(isProdOuServico != null && isProdOuServico == 1);
        postagem.setNomePostagem(nomePostagem);
        postagem.setDescricao(descricao);
        postagem.setCategoria(categoria);
        postagem.setCategoriaInteresse(categoriaInteresse);
        postagem.setCidade(cidade);
        postagem.setUf(uf);
        postagem.setUserID(cadastro.getId());
//Salvando as imagens  (Módulo de controle)
        if (imagem != null && !imagem.isEmpty()) {
            try {
                postagem.setImagem(imagem.getBytes());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Erro ao processar imagem de capa."));
            }
        }
//Salvando as imagens aplicando limite de 5
        if (imagensSecundarias != null) {
            if (imagensSecundarias.size() > 5)
                return ResponseEntity.badRequest().body(Map.of("error", "O limite é de 5 imagens secundárias."));
            try {
                for (int i = 0; i < imagensSecundarias.size(); i++) {
                    MultipartFile file = imagensSecundarias.get(i);
                    if (file != null && !file.isEmpty()) {
                        byte[] imgBytes = file.getBytes();
                        switch (i) {
                            case 0 -> postagem.setImagemS01(imgBytes);
                            case 1 -> postagem.setImagemS02(imgBytes);
                            case 2 -> postagem.setImagemS03(imgBytes);
                            case 3 -> postagem.setImagemS04(imgBytes);
                            case 4 -> postagem.setImagemS05(imgBytes);
                        }
                    }
                }
            } catch (IOException e) {
                return ResponseEntity.status(500).body(Map.of("error", "Erro ao salvar imagens secundárias: " + e.getMessage()));
            }
        }
//Salvando Definitivamente no banco, chamando o DAO com Hibernate
        postagemDAO.salvarPostagem(postagem);
        return ResponseEntity.ok(Map.of("message", "Postagem e imagens salvas com sucesso!", "id", postagem.getId()));
    }
    //Remoção das postagens, chamando o DAO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerPostagem(@PathVariable Long id) {
        Postagem postagem = postagemDAO.encontrarPostagemPorId(id);
        if (postagem == null)
            return ResponseEntity.notFound().build();

        postagemDAO.removerPostagem(id);
        return ResponseEntity.ok(Map.of("message", "Postagem removida com sucesso!"));
    }
//Listagem das postagens
    @GetMapping("/listar")
    public ResponseEntity<List<Postagem>> listarPostagens(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(401).build();

        String token = authHeader.substring(7); //Verifica o token de segurança
        if (!jwtUtil.validarToken(token))
            return ResponseEntity.status(401).build();
//Aplicação de Filtro, buscando apenas as postagens relacionadas ao user
        String userNome = jwtUtil.extrairUserNome(token);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(userNome);
        if (cadastro == null)
            return ResponseEntity.status(401).build();
//Aplicação de Filtro, buscando apenas as postagens relacionadas ao ID
        List<Postagem> postagensUsuario = postagemDAO.listarPorUserID(cadastro.getId());
        return ResponseEntity.ok(postagensUsuario);
    }
}