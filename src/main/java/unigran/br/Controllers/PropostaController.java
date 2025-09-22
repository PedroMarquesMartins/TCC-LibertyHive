package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unigran.br.JwtUtil;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.DAO.PropostaDAO;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Postagem;
import unigran.br.Model.Entidades.Proposta;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/propostas")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PropostaController {

    @Autowired
    private PropostaDAO propostaDAO;

    @Autowired
    private PostagemDAO postagemDAO;

    @Autowired
    private CadastroDAO cadastroDAO;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/criar")
    public ResponseEntity<?> criarProposta(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long itemDesejadoId,
            @RequestParam Long itemOferecidoId
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }

        String token = authHeader.substring(7);
        String userNome = jwtUtil.extrairUserNome(token);
        Cadastro usuarioProponente = cadastroDAO.encontrarPorUserNome(userNome);
        if (usuarioProponente == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(itemDesejadoId);
        Postagem itemOferecido = postagemDAO.encontrarPostagemPorId(itemOferecidoId);

        if (itemDesejado == null || itemOferecido == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Um ou ambos os itens não foram encontrados."));
        }

        Long userIdDesejado = itemDesejado.getUserID();
        Long userIdOferecedor = itemOferecido.getUserID();

        Proposta proposta = new Proposta();
        proposta.setStatus(1);
        proposta.setUserId01(userIdOferecedor);
        proposta.setUserId02(userIdDesejado);
        proposta.setItemDesejadoId(itemDesejadoId);
        proposta.setItemOferecidoId(itemOferecidoId);
        proposta.setAvaliarPerfil(3);

        propostaDAO.salvarProposta(proposta);

        return ResponseEntity.ok(Map.of("message", "Proposta criada com sucesso!", "propostaId", proposta.getId()));
    }

    @GetMapping("/listarPropostas")
    public ResponseEntity<?> listarPropostas(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }

        String token = authHeader.substring(7);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (cadastro == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        try {
            Long userId = cadastro.getId();
            List<Proposta> propostas = propostaDAO.listarPorUsuario(userId);

            List<Map<String, Object>> resposta = propostas.stream()
                    .map(p -> {
                        Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(p.getItemDesejadoId());
                        Postagem itemOferecido = postagemDAO.encontrarPostagemPorId(p.getItemOferecidoId());

                        if (itemDesejado == null || itemOferecido == null) return null;

                        boolean podeCancelar = p.getUserId01().equals(userId);
                        boolean podeConcluir = !p.getUserId01().equals(userId) && p.getStatus() == 1;
                        boolean podeRecusar = !p.getUserId01().equals(userId) && p.getStatus() == 1;

                        return Map.of(
                                "idProposta", p.getId(),
                                "status", p.getStatus(),
                                "userId01", p.getUserId01(),
                                "userId02", p.getUserId02(),
                                "itemDesejado", Map.of(
                                        "id", itemDesejado.getId(),
                                        "nomePostagem", itemDesejado.getNomePostagem(),
                                        "descricao", itemDesejado.getDescricao(),
                                        "categoria", itemDesejado.getCategoria(),
                                        "disponibilidade", itemDesejado.getDisponibilidade(),
                                        "cidade", itemDesejado.getCidade(),
                                        "uf", itemDesejado.getUf()
                                ),
                                "itemOferecido", Map.of(
                                        "id", itemOferecido.getId(),
                                        "nomePostagem", itemOferecido.getNomePostagem(),
                                        "descricao", itemOferecido.getDescricao(),
                                        "categoria", itemOferecido.getCategoria(),
                                        "disponibilidade", itemOferecido.getDisponibilidade(),
                                        "cidade", itemOferecido.getCidade(),
                                        "uf", itemOferecido.getUf()
                                ),
                                "podeCancelar", podeCancelar,
                                "podeConcluir", podeConcluir,
                                "podeRecusar", podeRecusar
                        );
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            System.err.println("Erro ao listar propostas: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao listar propostas: " + e.getMessage()));
        }
    }

    @PostMapping("/acao")
    public ResponseEntity<?> atualizarStatusProposta(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long propostaId,
            @RequestParam String acao
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }

        String token = authHeader.substring(7);
        Cadastro usuario = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        Proposta proposta = propostaDAO.encontrarPorId(propostaId);
        if (proposta == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Proposta não encontrada."));
        }

        Long currentUserId = usuario.getId();

        switch (acao.toLowerCase()) {
            case "cancelar":
                if (!proposta.getUserId01().equals(currentUserId)) {
                    return ResponseEntity.status(403).body(Map.of("message", "Somente o usuário que criou a proposta pode cancelar."));
                }
                proposta.setStatus(0);
                break;

            case "concluir":
                if (proposta.getUserId01().equals(currentUserId)) {
                    return ResponseEntity.status(403).body(Map.of("message", "Não é possível concluir sua própria proposta."));
                }
                proposta.setStatus(2);

                Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(Long.valueOf(proposta.getItemDesejadoId()));
                Postagem itemOferecido = postagemDAO.encontrarPostagemPorId(Long.valueOf(proposta.getItemOferecidoId()));
                if (itemDesejado != null) itemDesejado.setDisponibilidade(false);
                if (itemOferecido != null) itemOferecido.setDisponibilidade(false);

                postagemDAO.atualizarPostagem(itemDesejado);
                postagemDAO.atualizarPostagem(itemOferecido);
                break;

            case "recusar":
                if (proposta.getUserId01().equals(currentUserId)) {
                    return ResponseEntity.status(403).body(Map.of("message", "Não é possível recusar sua própria proposta."));
                }
                proposta.setStatus(3);
                break;

            default:
                return ResponseEntity.badRequest().body(Map.of("message", "Ação inválida."));
        }

        propostaDAO.atualizarProposta(proposta);
        return ResponseEntity.ok(Map.of("message", "Proposta atualizada com sucesso!", "status", proposta.getStatus()));
    }
}
