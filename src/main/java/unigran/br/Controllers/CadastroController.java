package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.EscambistaDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Escambista;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cadastros")
public class CadastroController {

    @Autowired
    private CadastroDAO cadastroDAO;
    @Autowired
    private EscambistaDAO escambistaDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> salvarCadastro(@RequestBody Cadastro cadastro) {
        String email = cadastro.getEmail();
        String userNome = cadastro.getUserNome();
        String senha = cadastro.getSenha();

        if (email == null || email.isEmpty() || email.length() > 254 || email.contains(" ") || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email inválido. Deve conter '@' e não pode ter espaços.");
            return ResponseEntity.badRequest().body(error);
        }

        if (userNome == null || !userNome.matches("^[a-zA-Z0-9_-]{3,20}$")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Nome de usuário inválido. Apenas letras e números são permitidos, sem espaços ou símbolos.");
            return ResponseEntity.badRequest().body(error);
        }

        if (senha == null || senha.isEmpty() || senha.length() < 6 || senha.contains(" ")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Senha inválida. Deve conter ao menos 6 caracteres e não pode conter espaços");
            return ResponseEntity.badRequest().body(error);
        }

        List<Cadastro> cadastrosExistentes = cadastroDAO.listarTodos();
        boolean emailJaCadastrado = cadastrosExistentes.stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
        if (emailJaCadastrado) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Este email já está cadastrado.");
            return ResponseEntity.badRequest().body(error);
        }

        boolean userNomeJaCadastrado = cadastrosExistentes.stream()
                .anyMatch(c -> c.getUserNome().equalsIgnoreCase(userNome));
        if (userNomeJaCadastrado){
            Map<String, String> error = new HashMap<>();
            error.put("error", "Este usuário já está cadastrado.");
            return ResponseEntity.badRequest().body(error);
        }

        String senhaCriptografada = passwordEncoder.encode(cadastro.getSenha());
        cadastro.setSenha(senhaCriptografada);

        cadastroDAO.salvarCadastro(cadastro);

        Escambista novoEscambista = new Escambista();
        novoEscambista.setUserNome(cadastro.getUserNome());
        novoEscambista.setEmail(cadastro.getEmail());
        novoEscambista.setAvaliacao(3);

        escambistaDAO.salvarEscambista(novoEscambista);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cadastro salvo com sucesso!");
        return ResponseEntity.ok(response);
    }
}