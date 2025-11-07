package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import unigran.br.JwtUtil;
import unigran.br.Model.DAO.*;
import unigran.br.Model.Entidades.*;
import unigran.br.Services.EmailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//Controller Rest e rota base da API
@RestController
@RequestMapping("/api/propostas")
@CrossOrigin(origins = "http://127.0.0.1:5500")//Permite as requisições
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
    private AvaliacaoDAO avaliacaoDAO;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailService emailService;



    //Retorna o user autenticado com o token jwt
    private Cadastro getAuthenticatedUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
    }

    //Criação de proposta
    @PostMapping("/criar")
    public ResponseEntity<?> criarProposta(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long itemDesejadoId,
            @RequestParam(required = false) Long itemOferecidoId
    ){ //Validação de token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou ausente."));
        }
        Cadastro usuarioProponente = getAuthenticatedUser(authHeader);
        if (usuarioProponente == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Usuário não encontrado."));
        }

        //Verifica item desejado
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
//Caso for doação, a logica muda
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
//Salva a proposta
        Proposta proposta = new Proposta();
        proposta.setStatus(1);
        proposta.setUserId01(usuarioProponente.getId());
        proposta.setUserId02(itemDesejado.getUserID());
        proposta.setItemDesejadoId(itemDesejadoId);
        proposta.setItemOferecidoId(itemOferecidoId);
        propostaDAO.salvarProposta(proposta);
 //Envio pelo e-mail viia API
        try {
            Cadastro usuarioReceptor = cadastroDAO.encontrarCadastroPorId(proposta.getUserId02());
            Escambista escambistaReceptor = escambistaDAO.encontrarPorUserId(Math.toIntExact(proposta.getUserId02()));
            if (usuarioReceptor != null && escambistaReceptor != null && Boolean.TRUE.equals(escambistaReceptor.getQuerNotifi())) {
                emailService.enviarAlertaNovaProposta(
                        usuarioReceptor.getEmail(),
                        usuarioReceptor.getUserNome(),
                        usuarioProponente.getUserNome(),
                        itemDesejado.getNomePostagem()
                );
            }
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(Map.of("message", "Proposta criada com sucesso!", "propostaId", proposta.getId()));
    }

    @GetMapping("/listarPropostas")
    @Transactional(readOnly = true)
    public ResponseEntity<?> listarPropostas(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Cadastro cadastro = getAuthenticatedUser(authHeader);
        if (cadastro == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou usuário não encontrado."));
        }

        try {
            Long userId = cadastro.getId();
            List<Proposta> propostas = propostaDAO.listarPorUsuario(userId);

            //Mapeia propostas pra um formato simplificado
            List<Map<String, Object>> resposta = propostas.stream()
                    .map(p -> {
                        Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(p.getItemDesejadoId());
                        Postagem itemOferecido = (p.getItemOferecidoId() != null)
                                ? postagemDAO.encontrarPostagemPorId(p.getItemOferecidoId())
                                : null;

                        if (itemDesejado == null) return null;
                        if ((Boolean.FALSE.equals(itemDesejado.getDisponibilidade())) ||
                                (itemOferecido != null && Boolean.FALSE.equals(itemOferecido.getDisponibilidade()))) {
                            if (p.getStatus() == 1) {
                                p.setStatus(3);
                                propostaDAO.atualizarProposta(p);
                            }
                        }

                        boolean podeCancelar = p.getUserId01().equals(userId) && p.getStatus() == 1;
                        boolean podeConcluir = !p.getUserId01().equals(userId) && p.getStatus() == 1;
                        boolean podeRecusar = !p.getUserId01().equals(userId) && p.getStatus() == 1;

                        String receptorNome = itemDesejado.getUserNome();
                        String proponenteNome = (itemOferecido != null && itemOferecido.getUserNome() != null)
                                ? itemOferecido.getUserNome()
                                : cadastroDAO.encontrarCadastroPorId(p.getUserId01()).getUserNome();

                        String statusLabel = switch (Objects.requireNonNullElse(p.getStatus(), -1)) {
                            case 1 -> "Pendente";
                            case 0 -> "Cancelada";
                            case 2 -> "Concluída";
                            case 3 -> "Recusada";
                            default -> "Desconhecido";
                        };

                        Map<String, Object> propostaMap = new HashMap<>();
                        propostaMap.put("idProposta", p.getId());
                        propostaMap.put("status", p.getStatus());
                        propostaMap.put("statusLabel", statusLabel);
                        propostaMap.put("userId01", p.getUserId01());
                        propostaMap.put("userId02", p.getUserId02());
                        propostaMap.put("enviadoPeloUsuarioLogado", p.getUserId01().equals(userId));
                        propostaMap.put("proponenteNome", proponenteNome);
                        propostaMap.put("receptorNome", receptorNome);
                        propostaMap.put("itemDesejado", itemDesejado);
                        propostaMap.put("itemOferecido", itemOferecido);
                        propostaMap.put("podeCancelar", podeCancelar);
                        propostaMap.put("podeConcluir", podeConcluir);
                        propostaMap.put("podeRecusar", podeRecusar);
                        propostaMap.put("avaliacaoUsuario", avaliacaoDAO.calcularMediaPorUsuarioId(itemDesejado.getUserID()).getOrDefault("media", 0));
                        propostaMap.put("dataHora", p.getDataHora());

                        return propostaMap;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Erro interno ao listar propostas."));
        }
    }
    @PostMapping("/avaliar")
    public ResponseEntity<?> avaliarUsuario(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody Map<String, Object> body
    ) {
        Cadastro usuarioAvaliador = getAuthenticatedUser(authHeader);

        if (usuarioAvaliador == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou usuário não encontrado."));
        }
        Escambista escambistaAtual = escambistaDAO.encontrarPorUserNome(usuarioAvaliador.getUserNome());
        if (escambistaAtual == null || escambistaAtual.getCpf() == null || escambistaAtual.getCpf().trim().isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Você precisa ter um CPF cadastrado para avaliar."));
        }
        if (body == null || !body.containsKey("propostaId") || !body.containsKey("nota") || !body.containsKey("usuarioAvaliadoId")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Parâmetros 'propostaId', 'nota' e 'usuarioAvaliadoId' são obrigatórios."));
        }
        Long propostaId;
        Long idUsuarioAvaliado;
        Integer nota;
        try {
            propostaId = Long.valueOf(body.get("propostaId").toString());
            idUsuarioAvaliado = Long.valueOf(body.get("usuarioAvaliadoId").toString());
            nota = Integer.valueOf(body.get("nota").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Parâmetros inválidos."));
        }
        Proposta proposta = propostaDAO.encontrarPorId(propostaId);
        if (proposta == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Proposta não encontrada."));
        }
        if (!Objects.equals(proposta.getStatus(), 2)) {
            return ResponseEntity.badRequest().body(Map.of("message", "A avaliação só é permitida para propostas concluídas."));
        }
        if (avaliacaoDAO.existePorUsuarioAvaliadorEProposta(usuarioAvaliador.getId(), propostaId)) {
            return ResponseEntity.status(409).body(Map.of("message", "Você já avaliou este usuário nesta proposta."));
        }
        Avaliacao novaAvaliacao = new Avaliacao();
        novaAvaliacao.setUsuarioAvaliadorId(usuarioAvaliador.getId());
        novaAvaliacao.setUsuarioAvaliadoId(idUsuarioAvaliado);
        novaAvaliacao.setPropostaId(propostaId);
        novaAvaliacao.setNota(nota);
        try {
            avaliacaoDAO.salvarAvaliacao(novaAvaliacao);
            return ResponseEntity.ok(Map.of("message", "Avaliação registrada com sucesso!", "avaliacaoId", novaAvaliacao.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Erro interno ao salvar a avaliação: " + e.getMessage()));
        }
    }
    @GetMapping("/avaliacoes/{usuarioId}")
    public ResponseEntity<?> obterMediaDeAvaliacoes(@PathVariable Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "ID de usuário inválido."));
        }
        try {
            Map<String, Object> resultado = avaliacaoDAO.calcularMediaPorUsuarioId(usuarioId);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao buscar as avaliações do usuário: " + e.getMessage()));
        }
    }

    @PostMapping("/acao")
    public ResponseEntity<?> atualizarStatusProposta(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long propostaId,
            @RequestParam String acao
    ){
        Cadastro usuario = getAuthenticatedUser(authHeader);
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido ou usuário não encontrado."));
        }

        Proposta proposta = propostaDAO.encontrarPorId(propostaId);
        if (proposta == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Proposta não encontrada."));
        }

        Long currentUserId = usuario.getId();
        switch (acao.toLowerCase()) {
            case "cancelar" -> {
                if (!proposta.getUserId01().equals(currentUserId))
                    return ResponseEntity.status(403).body(Map.of("message", "Somente o criador pode cancelar."));
                proposta.setStatus(0);
            }
            case "concluir" -> {
                if (proposta.getUserId01().equals(currentUserId))
                    return ResponseEntity.status(403).body(Map.of("message", "Não é possível concluir sua própria proposta."));

                Postagem itemDesejado = postagemDAO.encontrarPostagemPorId(proposta.getItemDesejadoId());
                Postagem itemOferecido = (proposta.getItemOferecidoId() != null)
                        ? postagemDAO.encontrarPostagemPorId(proposta.getItemOferecidoId())
                        : null;

                if (itemDesejado == null || Boolean.FALSE.equals(itemDesejado.getDisponibilidade())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "O item desejado não está mais disponível."));
                }
                if (itemOferecido != null && Boolean.FALSE.equals(itemOferecido.getDisponibilidade())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "O item oferecido não está mais disponível."));
                }
                proposta.setStatus(2);
                itemDesejado.setDisponibilidade(false);
                postagemDAO.atualizarPostagem(itemDesejado);  //Ta dando erro aqui agora

                if (itemOferecido != null) {
                    itemOferecido.setDisponibilidade(false);
                    postagemDAO.atualizarPostagem(itemOferecido); //Ta dando erro aqui agora (corrigido ok)
                }
                propostaDAO.recusarOutrasPropostasPendentes(itemDesejado.getId(), itemOferecido != null ? itemOferecido.getId() : null, proposta.getId());

                try {
                    Cadastro usuarioProponente = cadastroDAO.encontrarCadastroPorId(proposta.getUserId01());
                    Escambista escambistaProponente = escambistaDAO.encontrarPorUserId(Math.toIntExact(proposta.getUserId01()));
                    if (usuarioProponente != null && escambistaProponente != null && Boolean.TRUE.equals(escambistaProponente.getQuerNotifi())) {
                        emailService.enviarNotificacaoPropostaAceita(
                                usuarioProponente.getEmail(),
                                usuarioProponente.getUserNome(),
                                itemDesejado != null ? itemDesejado.getNomePostagem() : "",
                                itemOferecido != null ? itemOferecido.getNomePostagem() : null
                        );
                    }
                } catch (Exception ignored) {
                }
            }
            case "recusar" -> {
                if (proposta.getUserId01().equals(currentUserId))
                    return ResponseEntity.status(403).body(Map.of("message", "Não é possível recusar sua própria proposta."));
                proposta.setStatus(3);
            }
            default -> {
                return ResponseEntity.badRequest().body(Map.of("message", "Ação inválida."));
            }
        }

        propostaDAO.atualizarProposta(proposta);
        return ResponseEntity.ok(Map.of("message", "Proposta atualizada com sucesso!", "status", proposta.getStatus()));
    }
}
