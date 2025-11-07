package unigran.br.Controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import unigran.br.JwtUtil;
import unigran.br.Model.DAO.ChatDAO;
import unigran.br.Model.DAO.MensagemDAO;
import unigran.br.Model.Entidades.Chat;
import unigran.br.Model.Entidades.Mensagem;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatDAO chatDAO;
    private final MensagemDAO mensagemDAO;
    private final JwtUtil jwtUtil;

    @Autowired
    public ChatController(ChatDAO chatDAO, MensagemDAO mensagemDAO, JwtUtil jwtUtil) {
        this.chatDAO = chatDAO;
        this.mensagemDAO = mensagemDAO;
        this.jwtUtil = jwtUtil;
    }

    //Cria um novo chat ou retorna o já existente entre os dois usuários

    @PostMapping("/criar")
    public ResponseEntity<?> criarOuObterChat(
            @RequestParam Long userId01,
            @RequestParam Long userId02,
            @RequestParam String userNome01,
            @RequestParam String userNome02
    ) {
        try {
            Chat chat = chatDAO.criarOuObterChat(userId01, userId02, userNome01, userNome02);
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar/obter chat: " + e.getMessage());
        }
    }


    @PutMapping("/{chatId}/bloquear")
    public ResponseEntity<?> alternarBloqueio(@PathVariable Long chatId) {
        Chat chat = chatDAO.encontrarPorId(chatId);
        if (chat == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat não encontrado.");

        //Inverte o estado de bloqueio do chat
        chat.setBloqueado(!Boolean.TRUE.equals(chat.getBloqueado()));
        chatDAO.atualizar(chat);
        return ResponseEntity.ok(chat.getBloqueado() ? "Chat bloqueado." : "Chat desbloqueado.");
    }

    @PutMapping("/{chatId}/valor")
    public ResponseEntity<?> atualizarValor(@PathVariable Long chatId, @RequestBody Map<String, Object> body) {
        Chat chat = chatDAO.encontrarPorId(chatId);
        if (chat == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat não encontrado.");

        try {             //Atualiza o valor proposto dentro do chat
            Object valorObj = body.get("valorProposto");
            if (valorObj == null) return ResponseEntity.badRequest().body("Valor proposto ausente.");
            double valor = Double.parseDouble(valorObj.toString());
            chat.setValorProposto(BigDecimal.valueOf(valor));


            //e salva
            chatDAO.atualizar(chat);
            return ResponseEntity.ok("Valor proposto atualizado com sucesso.");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Valor proposto inválido.");
        }
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> obterChat(@PathVariable Long chatId) {
        Chat chat = chatDAO.encontrarPorId(chatId);
        if (chat == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat não encontrado.");
        return ResponseEntity.ok(chat);
    }

    //Lista todas as mensagens do chat via ID
    @GetMapping("/{chatId}/mensagens")
    public ResponseEntity<?> obterMensagens(@PathVariable Long chatId) {
        Chat chat = chatDAO.encontrarPorId(chatId);
        if (chat == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat não encontrado.");
        List<Mensagem> mensagens = mensagemDAO.listarPorChat(chatId);
        return ResponseEntity.ok(mensagens);
    }

    @PostMapping("/{chatId}/mensagem")
    public ResponseEntity<?> enviarMensagem(
            @PathVariable Long chatId,
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Header de autorização inválido.");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido.");
        }
//Depois da autenticacao,  extrai o nome do usuário do token,
        String userNome = jwtUtil.extrairUserNome(token);
        Chat chat = chatDAO.encontrarPorId(chatId);
        if (chat == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat não encontrado.");


        //Impede envio se o chat estiver bloqueado
        if (Boolean.TRUE.equals(chat.getBloqueado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chat bloqueado. Não é possível enviar mensagens.");
        }

        Long userId;         //determina qual usuário esta enviando a mensagem
        if (userNome.equals(chat.getUserNome01())) userId = chat.getUserId01();
        else if (userNome.equals(chat.getUserNome02())) userId = chat.getUserId02();
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não participa do chat.");

        //Verifica texto da mensagem

        String mensagemTexto = body.get("mensagem");
        if (mensagemTexto == null || mensagemTexto.isBlank()) {
            return ResponseEntity.badRequest().body("Mensagem vazia.");
        }
        //Cria e salva a mensagem
        Mensagem msg = new Mensagem(chatId, userId, mensagemTexto);
        mensagemDAO.salvar(msg);
        return ResponseEntity.ok(msg);
    }

    //retorna todos os chats daquele user
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<?> listarChatsPorUsuario(@PathVariable Long userId) {
        try {
            List<Chat> chats = chatDAO.listarPorUsuario(userId);
            if (chats == null || chats.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar chats do usuário: " + e.getMessage());
        }
    }
}
