package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unigran.br.JwtUtil;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.FavoritoDAO;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Favorito;
import unigran.br.Model.Entidades.Postagem;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoDAO favoritoDAO;

    @Autowired
    private CadastroDAO cadastroDAO;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostagemDAO postagemDAO;

    @PostMapping
    public ResponseEntity<?> favoritar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long postagemId
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }

        String token = authHeader.substring(7);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (cadastro == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        try {
            if (favoritoDAO.existeFavorito(cadastro.getId(), postagemId)) {
                return ResponseEntity.status(409).body(Map.of("message", "Este item já foi favoritado por você."));
            }

            Favorito favorito = new Favorito();
            favorito.setUserId(cadastro.getId());
            favorito.setPostagemId(postagemId);
            favoritoDAO.salvar(favorito);

            return ResponseEntity.ok(Map.of("message", "Item favoritado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao favoritar: " + e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<?> listarFavoritos(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }

        String token = authHeader.substring(7);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (cadastro == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        try {
            List<Favorito> favoritos = favoritoDAO.listarPorUserId(cadastro.getId());
            List<Map<String, Object>> resposta = favoritos.stream()
                    .map(fav -> {
                        Postagem p = postagemDAO.encontrarPostagemPorId(fav.getPostagemId());
                        if (p == null || !Boolean.TRUE.equals(p.getDisponibilidade())) {
                            return null;
                        }
                        return Map.of(
                                "id", fav.getId(),
                                "postagemId", fav.getPostagemId(),
                                "userId", fav.getUserId(),
                                "postagem", Map.of(
                                        "id", p.getId(),
                                        "nomePostagem", p.getNomePostagem(),
                                        "descricao", p.getDescricao(),
                                        "categoria", p.getCategoria(),
                                        "isProdOuServico", p.getIsProdOuServico(),
                                        "doacao", p.getDoacao(),
                                        "cidade", p.getCidade(),
                                        "uf", p.getUf(),
                                        "imagem", p.getImagem()
                                )
                        );
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            System.err.println("Erro ao listar favoritos: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao listar favoritos: " + e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerFavorito(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable Long id
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }

        String token = authHeader.substring(7);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (cadastro == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        try {
            Favorito favorito = favoritoDAO.buscarPorId(id);
            if (favorito == null || !favorito.getUserId().equals(cadastro.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Você não tem permissão para remover este favorito."));
            }

            favoritoDAO.remover(id);
            return ResponseEntity.ok(Map.of("message", "Favorito removido com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao remover favorito: " + e.getMessage()));
        }
    }
}
