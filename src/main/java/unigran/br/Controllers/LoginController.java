package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.JwtUtil;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class LoginController {

    @Autowired
    private CadastroDAO cadastroDAO;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
//Recebendo o formulário:
    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginDados) {
        String user = loginDados.get("user");
        String senha = loginDados.get("senha");

        List<Cadastro> cadastros = cadastroDAO.listarTodos();
//Buscando se há usuário compatível, com senha compatível
        Cadastro usuario = cadastros.stream()
                .filter(c -> (c.getEmail().equals(user) || c.getUserNome().equals(user))
                        && passwordEncoder.matches(senha, c.getSenha()))
                .findFirst()
                .orElse(null);

        Map<String, Object> response = new HashMap<>();

        if (usuario != null) {
            String token = jwtUtil.gerarToken(usuario.getUserNome()); //Geração de Token de entrada (Segurança)

            response.put("success", true);
            response.put("message", "Login realizado com sucesso!");
            response.put("token", token);
            response.put("userNome", usuario.getUserNome());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Usuário ou senha inválidos.");
            return ResponseEntity.status(401).body(response);
        }
    }
    //Autenticação de entrada, gerência de acesso por  Token de sessão
    @GetMapping("/usuario")
    public ResponseEntity<Map<String, Object>> getUsuarioLogado(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("logado", false);
            return ResponseEntity.status(401).body(response);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validarToken(token)) {
            response.put("logado", false);
            return ResponseEntity.status(401).body(response);
        }

        String userNome = jwtUtil.extrairUserNome(token);
        response.put("logado", true);
        response.put("userNome", userNome);
        return ResponseEntity.ok(response);
    }

    //Desautenticação, finalizando o Token de sessão
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout realizado com sucesso.");
        return ResponseEntity.ok(response);
    }
}