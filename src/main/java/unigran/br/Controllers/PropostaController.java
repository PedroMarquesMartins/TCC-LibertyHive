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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            @RequestParam(required = false) Long itemOferecidoId
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }

        String token = authHeader.substring(7);
        Cadastro usuarioProponente = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (usuarioProponente == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(itemDesejadoId);
        if (itemDesejado == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Item desejado não encontrado."));
        }

        boolean isDoacao = Boolean.TRUE.equals(itemDesejado.getDoacao());

        boolean existe;
        if (isDoacao) {
            existe = propostaDAO.existsByItemDesejadoIdAndUserId01(itemDesejadoId, usuarioProponente.getId());
        } else {
            if (itemOferecidoId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "É necessário informar um item oferecido para troca."));
            }
            existe = propostaDAO.existsByItemDesejadoIdAndItemOferecidoId(itemDesejadoId, itemOferecidoId);
        }

        if (existe) {
            return ResponseEntity.badRequest().body(Map.of("message", "Você já enviou esta proposta."));
        }

        Postagem itemOferecido = null;
        if (!isDoacao) {
            itemOferecido = postagemDAO.encontrarPostagemPorId(itemOferecidoId);
            if (itemOferecido == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Item oferecido não encontrado."));
            }
        }
        Proposta proposta = new Proposta();
        proposta.setStatus(1);
        proposta.setUserId01(usuarioProponente.getId());
        proposta.setUserId02(itemDesejado.getUserID());
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

            List<Map<String, Object>> resposta = propostas.stream().map(p -> {
                Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(p.getItemDesejadoId());
                Postagem itemOferecido = (p.getItemOferecidoId() != null)
                        ? postagemDAO.encontrarPostagemPorId(p.getItemOferecidoId())
                        : null;

                if (itemDesejado == null) return null;

                boolean podeCancelar = p.getUserId01().equals(userId);
                boolean podeConcluir = !p.getUserId01().equals(userId) && p.getStatus() == 1;
                boolean podeRecusar = !p.getUserId01().equals(userId) && p.getStatus() == 1;

                Map<String, Object> propostaMap = new HashMap<>();
                propostaMap.put("idProposta", p.getId());
                propostaMap.put("status", p.getStatus());
                propostaMap.put("userId01", p.getUserId01());
                propostaMap.put("userId02", p.getUserId02());

                Map<String, Object> itemDesejadoMap = new HashMap<>();
                itemDesejadoMap.put("id", itemDesejado.getId());
                itemDesejadoMap.put("nomePostagem", itemDesejado.getNomePostagem());
                itemDesejadoMap.put("descricao", itemDesejado.getDescricao());
                itemDesejadoMap.put("categoria", itemDesejado.getCategoria());
                itemDesejadoMap.put("disponibilidade", itemDesejado.getDisponibilidade());
                itemDesejadoMap.put("cidade", itemDesejado.getCidade());
                itemDesejadoMap.put("uf", itemDesejado.getUf());

                propostaMap.put("itemDesejado", itemDesejadoMap);

                if (itemOferecido != null) {
                    Map<String, Object> itemOferecidoMap = new HashMap<>();
                    itemOferecidoMap.put("id", itemOferecido.getId());
                    itemOferecidoMap.put("nomePostagem", itemOferecido.getNomePostagem());
                    itemOferecidoMap.put("descricao", itemOferecido.getDescricao());
                    itemOferecidoMap.put("categoria", itemOferecido.getCategoria());
                    itemOferecidoMap.put("disponibilidade", itemOferecido.getDisponibilidade());
                    itemOferecidoMap.put("cidade", itemOferecido.getCidade());
                    itemOferecidoMap.put("uf", itemOferecido.getUf());

                    propostaMap.put("itemOferecido", itemOferecidoMap);
                } else {
                    propostaMap.put("itemOferecido", null);
                }

                propostaMap.put("podeCancelar", podeCancelar);
                propostaMap.put("podeConcluir", podeConcluir);
                propostaMap.put("podeRecusar", podeRecusar);

                return propostaMap;
            }).filter(Objects::nonNull).toList();

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            System.err.println("Erro ao listar propostas: " + e.getMessage());
            e.printStackTrace();
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

                Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(proposta.getItemDesejadoId());
                Postagem itemOferecido = (proposta.getItemOferecidoId() != null)
                        ? postagemDAO.encontrarPostagemPorId(proposta.getItemOferecidoId())
                        : null;

                if (itemDesejado != null) itemDesejado.setDisponibilidade(false);
                if (itemOferecido != null) itemOferecido.setDisponibilidade(false);

                postagemDAO.atualizarPostagem(itemDesejado);
                if (itemOferecido != null) postagemDAO.atualizarPostagem(itemOferecido);
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
