package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import unigran.br.JwtUtil;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.EscambistaDAO;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.DAO.PropostaDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Escambista;
import unigran.br.Model.Entidades.Postagem;
import unigran.br.Model.Entidades.Proposta;
import unigran.br.Services.EmailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private EscambistaDAO escambistaDAO;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailService emailService;

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

        if (itemDesejado.getUserID().equals(usuarioProponente.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Você não pode propor negócio para o seu próprio item."));
        }

        if (Boolean.FALSE.equals(itemDesejado.getDisponibilidade())) {
            return ResponseEntity.badRequest().body(Map.of("message", "O item desejado não está disponível."));
        }

        boolean isDoacao = Boolean.TRUE.equals(itemDesejado.getDoacao());

        boolean existe;
        Postagem itemOferecido = null;

        if (isDoacao) {
            existe = propostaDAO.existsByItemDesejadoIdAndUserId01(itemDesejadoId, usuarioProponente.getId());
        } else {
            if (itemOferecidoId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "É necessário informar um item oferecido para troca."));
            }

            itemOferecido = postagemDAO.encontrarPostagemPorId(itemOferecidoId);
            if (itemOferecido == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Item oferecido não encontrado."));
            }

            if (!itemOferecido.getUserID().equals(usuarioProponente.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Você só pode oferecer itens que são seus."));
            }
            if (Boolean.FALSE.equals(itemOferecido.getDisponibilidade())) {
                return ResponseEntity.badRequest().body(Map.of("message", "O item oferecido não está disponível."));
            }

            existe = propostaDAO.existsByItemDesejadoIdAndItemOferecidoId(itemDesejadoId, itemOferecidoId);
        }

        if (existe) {
            return ResponseEntity.badRequest().body(Map.of("message", "Você já enviou esta proposta."));
        }

        Proposta proposta = new Proposta();
        proposta.setStatus(1);
        proposta.setUserId01(usuarioProponente.getId());
        proposta.setUserId02(itemDesejado.getUserID());
        proposta.setItemDesejadoId(itemDesejadoId);
        proposta.setItemOferecidoId(itemOferecidoId);
        proposta.setAvaliarPerfil(3);

        propostaDAO.salvarProposta(proposta);

        try {
            Cadastro usuarioReceptor = cadastroDAO.encontrarCadastroPorId(proposta.getUserId02());
            Escambista escambistaReceptor = escambistaDAO.encontrarPorUserId(Math.toIntExact(proposta.getUserId02()));

            if (usuarioReceptor != null && escambistaReceptor != null && Boolean.TRUE.equals(escambistaReceptor.getQuerNotifi())) {
                String nomeReceptor = usuarioReceptor.getUserNome();
                String emailReceptor = usuarioReceptor.getEmail();
                String nomeProponente = usuarioProponente.getUserNome();
                String nomeItemDesejado = itemDesejado.getNomePostagem();

                emailService.enviarAlertaNovaProposta(emailReceptor, nomeReceptor, nomeProponente, nomeItemDesejado);
            }
        } catch (Exception e) {
        }

        return ResponseEntity.ok(Map.of("message", "Proposta criada com sucesso!", "propostaId", proposta.getId()));
    }

    @GetMapping("/listarPropostas")
    @Transactional(readOnly = true)
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

                String receptorNome = itemDesejado.getUserNome();
                String proponenteNome = null;
                if (itemOferecido != null && itemOferecido.getUserNome() != null) {
                    proponenteNome = itemOferecido.getUserNome();
                } else {
                    try {
                        Cadastro c = cadastroDAO.encontrarCadastroPorId(p.getUserId01());
                        if (c != null) proponenteNome = c.getUserNome();
                    } catch (Exception ex) {
                    }
                }

                String statusLabel;
                switch (Objects.requireNonNullElse(p.getStatus(), -1)) {
                    case 1: statusLabel = "Pendente"; break;
                    case 0: statusLabel = "Cancelada"; break;
                    case 2: statusLabel = "Concluída"; break;
                    case 3: statusLabel = "Recusada"; break;
                    default: statusLabel = "Desconhecido"; break;
                }

                Map<String, Object> propostaMap = new HashMap<>();
                propostaMap.put("idProposta", p.getId());
                propostaMap.put("status", p.getStatus());
                propostaMap.put("statusLabel", statusLabel);
                propostaMap.put("userId01", p.getUserId01());
                propostaMap.put("userId02", p.getUserId02());
                propostaMap.put("enviadoPeloUsuarioLogado", p.getUserId01().equals(userId));
                propostaMap.put("proponenteNome", proponenteNome);
                propostaMap.put("receptorNome", receptorNome);

                Map<String, Object> itemDesejadoMap = new HashMap<>();
                itemDesejadoMap.put("id", itemDesejado.getId());
                itemDesejadoMap.put("nomePostagem", itemDesejado.getNomePostagem());
                itemDesejadoMap.put("descricao", itemDesejado.getDescricao());
                itemDesejadoMap.put("categoria", itemDesejado.getCategoria());
                itemDesejadoMap.put("disponibilidade", itemDesejado.getDisponibilidade());
                itemDesejadoMap.put("cidade", itemDesejado.getCidade());
                itemDesejadoMap.put("uf", itemDesejado.getUf());
                itemDesejadoMap.put("userNome", itemDesejado.getUserNome());
                if (itemDesejado.getImagem() != null) {
                    itemDesejadoMap.put("imagem", itemDesejado.getImagem());
                }
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
                    itemOferecidoMap.put("userNome", itemOferecido.getUserNome());
                    if (itemOferecido.getImagem() != null) {
                        itemOferecidoMap.put("imagem", itemOferecido.getImagem());
                    }
                    propostaMap.put("itemOferecido", itemOferecidoMap);
                } else {
                    propostaMap.put("itemOferecido", null);
                }

                propostaMap.put("podeCancelar", podeCancelar);
                propostaMap.put("podeConcluir", podeConcluir);
                propostaMap.put("podeRecusar", podeRecusar);

                return propostaMap;
            }).filter(Objects::nonNull).collect(Collectors.toList());

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro interno ao listar propostas."));
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

                if (itemDesejado != null) {
                    itemDesejado.setDisponibilidade(false);
                    postagemDAO.atualizarPostagem(itemDesejado);
                }
                if (itemOferecido != null) {
                    itemOferecido.setDisponibilidade(false);
                    postagemDAO.atualizarPostagem(itemOferecido);
                }

                try {
                    Cadastro usuarioProponente = cadastroDAO.encontrarCadastroPorId(proposta.getUserId01());
                    Escambista escambistaProponente = escambistaDAO.encontrarPorUserId(Math.toIntExact(proposta.getUserId01()));

                    if (usuarioProponente != null && escambistaProponente != null && Boolean.TRUE.equals(escambistaProponente.getQuerNotifi()) && itemDesejado != null) {
                        String emailProponente = usuarioProponente.getEmail();
                        String nomeProponente = usuarioProponente.getUserNome();
                        String nomeItemDesejado = itemDesejado.getNomePostagem();
                        String nomeItemOferecido = (itemOferecido != null) ? itemOferecido.getNomePostagem() : null;

                        emailService.enviarNotificacaoPropostaAceita(emailProponente, nomeProponente, nomeItemDesejado, nomeItemOferecido);
                    }
                } catch (Exception e) {

                }
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