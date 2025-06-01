package unigran.br.Controllers;

import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.Entidades.Cadastro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
//Controlador lógico de cadastro, para testar Integração com o FrontEnd em JavaScript atravéz de rota HTTP
@RestController
@RequestMapping("/api/cadastros")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CadastroController{
    @Autowired
    private CadastroDAO cadastroDAO;

    @PostMapping
    public ResponseEntity<?> salvarCadastro(@RequestBody Cadastro cadastro) {
        cadastroDAO.salvarCadastro(cadastro);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cadastro salvo com sucesso!");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Cadastro> encontrarCadastro(@PathVariable Long id) {
        Cadastro cadastro = cadastroDAO.encontrarCadastroPorId(id);
        if (cadastro != null) {
            return ResponseEntity.ok(cadastro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}