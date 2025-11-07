package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import unigran.br.JwtUtil;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.DAO.ChatDAO;
import unigran.br.Model.DAO.EscambistaDAO;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Escambista;
import unigran.br.Services.EmailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Definição do controllador rest e a rota da API
@RestController
@RequestMapping("/api/cadastros")
public class CadastroController {

    @Autowired
    private EmailService emailService;  //Dependencia email
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CadastroDAO cadastroDAO;
    @Autowired
    private ChatDAO chatDAO;
    @Autowired
    private EscambistaDAO escambistaDAO;
    @Autowired
    private PostagemDAO postagemDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping
    public ResponseEntity<?> salvarCadastro(@RequestBody Cadastro cadastro) {
        String email = cadastro.getEmail();
        String userNome = cadastro.getUserNome();
        String senha = cadastro.getSenha();


        //Validações básicas (duplicidade, criptografia, nome, senha, etc)
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
        boolean userNomeJaCadastrado = cadastrosExistentes.stream().anyMatch(c -> c.getUserNome().equalsIgnoreCase(userNome));
        if (userNomeJaCadastrado){
            Map<String, String> error = new HashMap<>();
            error.put("error", "Este usuário já está cadastrado.");
            return ResponseEntity.badRequest().body(error);
        }

        String senhaCriptografada = passwordEncoder.encode(cadastro.getSenha());
        cadastro.setSenha(senhaCriptografada);

        //Salva o cadastro validado
        cadastroDAO.salvarCadastro(cadastro);

        Long idGerado = cadastro.getId();

        //Cria e salva escambista base, vinculado ao cadastro
        Escambista novoEscambista = new Escambista();
        novoEscambista.setUserId(Math.toIntExact(idGerado));
        novoEscambista.setUserNome(cadastro.getUserNome());
        novoEscambista.setQuerNotifi(true);

        escambistaDAO.salvarEscambista(novoEscambista);
        emailService.enviarEmailBoasVindas(cadastro.getEmail(), cadastro.getUserNome());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cadastro salvo com sucesso!");
        return ResponseEntity.ok(response);
    }

    //Busca cadastro pelo nome de usuário
    @GetMapping("/{userNome}")
    public ResponseEntity<?> buscarCadastro(@PathVariable String userNome) {
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(userNome);
        if (cadastro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cadastro);
    }

    //Busca por cadastros existentes
    @PutMapping("/{userNome}")
    public ResponseEntity<?> atualizarCadastro(@PathVariable String userNome, @RequestBody Cadastro dadosAtualizados) {
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(userNome);
        if (cadastro == null) {
            return ResponseEntity.notFound().build();
        }

        String userNomeAntigo = cadastro.getUserNome();
        String userNomeNovo = dadosAtualizados.getUserNome();

        cadastro.setEmail(dadosAtualizados.getEmail());

        //Atualiza email e nome se necessário
        boolean userNomeFoiAlterado = false;
        if (userNomeNovo != null && !userNomeNovo.equals(userNomeAntigo)) {
            if (cadastroDAO.encontrarPorUserNome(userNomeNovo) != null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Este nome de usuário já está em uso."));
            }

            cadastro.setUserNome(userNomeNovo);
            userNomeFoiAlterado = true;
        }

            //Atualiza a senha
        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(dadosAtualizados.getSenha());
            cadastro.setSenha(senhaCriptografada);
        }

        cadastroDAO.salvarCadastro(cadastro);

        //Propaga a alteração em tabelas relacionadas (responsabilidade da aplicação)
        if (userNomeFoiAlterado) {
            escambistaDAO.atualizarUserNome(userNomeAntigo, userNomeNovo);
            postagemDAO.atualizarUserNomePostagens(userNomeAntigo, userNomeNovo);
            chatDAO.atualizarUserNome(userNomeAntigo, userNomeNovo);

        }

        String novoToken = jwtUtil.gerarToken(userNomeNovo);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Conta atualizada com sucesso!");
        response.put("token", novoToken);

        return ResponseEntity.ok(response);
    }
}