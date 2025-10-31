package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.EscambistaDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.JwtUtil;
import unigran.br.Model.Entidades.Escambista;

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
    private EscambistaDAO escambistaDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginDados) {
        String user = loginDados.get("user");
        String senha = loginDados.get("senha");

        List<Cadastro> cadastros = cadastroDAO.listarTodos();

        Cadastro usuario = cadastros.stream()
                .filter(c -> (c.getEmail().equals(user) || c.getUserNome().equals(user))
                        && passwordEncoder.matches(senha, c.getSenha()))
                .findFirst()
                .orElse(null);

        Map<String, Object> response = new HashMap<>();

        if (usuario != null) {
            String token = jwtUtil.gerarToken(usuario.getUserNome());

            response.put("success", true);
            response.put("message", "Login realizado com sucesso!");
            response.put("token", token);
            response.put("userNome", usuario.getUserNome());
            response.put("userId", usuario.getId());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Usuário ou senha inválidos.");
            return ResponseEntity.status(401).body(response);
        }
    }

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

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout realizado com sucesso.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, Object>> recuperarSenha(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = dados.get("email");
            String cpfInformado = dados.get("cpf");
            String contatoInformado = dados.get("contato");
            String datanascInformada = dados.get("datanasc");
            String novaSenha = dados.get("novaSenha");
            Cadastro usuario = cadastroDAO.listarTodos()
                    .stream()
                    .filter(c -> c.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                response.put("success", false);
                response.put("message", "E-mail não encontrado ou não cadastrado.");
                return ResponseEntity.status(401).body(response);
            }

            Escambista escambista = escambistaDAO.encontrarPorUserId(Math.toIntExact(usuario.getId()));
            if (escambista == null) {
                response.put("success", false);
                response.put("message", "Nenhum escambista vinculado a este usuário.");
                return ResponseEntity.status(401).body(response);
            }




            boolean temCpf = escambista.getCpf() != null && !escambista.getCpf().isBlank();
            boolean temContato = escambista.getContato() != null && !escambista.getContato().isBlank();
            boolean temDataNasc = escambista.getDataNasc() != null;

            int totalCamposCadastrados = (temCpf ? 1 : 0) + (temContato ? 1 : 0) + (temDataNasc ? 1 : 0);
            int acertos = 0;
            if (temCpf && cpfInformado != null && escambista.getCpf().equals(cpfInformado)) acertos++;
            if (temContato && contatoInformado != null && escambista.getContato().equals(contatoInformado)) acertos++;
            if (temDataNasc && datanascInformada != null && escambista.getDataNasc().toString().equals(datanascInformada)) acertos++;

            boolean passouValidacao = false;
            if (totalCamposCadastrados == 1 && acertos >= 1) passouValidacao = true;
            if (totalCamposCadastrados == 2 && acertos >= 2) passouValidacao = true;
            if (totalCamposCadastrados >= 3 && acertos >= 2) passouValidacao = true;

            if (!passouValidacao && totalCamposCadastrados > 0) {
                response.put("success", false);
                response.put("message", "Os dados informados não conferem com nosso registro.");
                return ResponseEntity.status(401).body(response);
            }




            usuario.setSenha(passwordEncoder.encode(novaSenha));
            cadastroDAO.atualizar(usuario);

            response.put("success", true);
            response.put("message", "Senha redefinida com sucesso.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erro ao tentar redefinir senha: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/dados-seguranca")
    public ResponseEntity<Map<String, Object>> dadosSeguranca(@RequestParam String email) {
        Cadastro cadastro = cadastroDAO.buscarPorEmail(email);
        if (cadastro == null) return ResponseEntity.status(404).build();
        Escambista escambista = escambistaDAO.encontrarPorUserId(Math.toIntExact(cadastro.getId()));
        if (escambista == null) return ResponseEntity.status(404).build();
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("cpf", escambista.getCpf() != null && !escambista.getCpf().isBlank());
        mapa.put("contato", escambista.getContato() != null && !escambista.getContato().isBlank());
        mapa.put("datanasc", escambista.getDataNasc() != null);

        return ResponseEntity.ok(mapa);
    }
}
