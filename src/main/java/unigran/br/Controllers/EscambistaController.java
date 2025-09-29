package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unigran.br.Model.DAO.EscambistaDAO;
import unigran.br.Model.Entidades.Escambista;

import java.util.Map;

@RestController
@RequestMapping("/api/escambista")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class EscambistaController {

    @Autowired
    private EscambistaDAO escambistaDAO;

    @GetMapping("/porUserNome/{userNome}")
    public ResponseEntity<?> getPorUserNome(@PathVariable String userNome) {
        Escambista escambista = escambistaDAO.encontrarPorUserNome(userNome);
        if (escambista == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Perfil de escambista não encontrado."));
        }
        return ResponseEntity.ok(escambista);
    }

    @PutMapping("/atualizarPorUserNome/{userNome}")
    public ResponseEntity<?> atualizarEscambistaPorUserNome(@PathVariable String userNome,
                                                            @RequestBody Escambista dadosAtualizados) {

        Escambista escambistaAtual = escambistaDAO.encontrarPorUserNome(userNome);

        if (escambistaAtual == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Perfil de escambista não encontrado."));
        }

        if (dadosAtualizados.getNomeEscambista() != null) {
            escambistaAtual.setNomeEscambista(dadosAtualizados.getNomeEscambista());
        }
        if (dadosAtualizados.getContato() != null) {
            escambistaAtual.setContato(dadosAtualizados.getContato());
        }
        if (dadosAtualizados.getEndereco() != null) {
            escambistaAtual.setEndereco(dadosAtualizados.getEndereco());
        }
        if (dadosAtualizados.getCpf() != null) {
            escambistaAtual.setCpf(dadosAtualizados.getCpf());
        }
        if (dadosAtualizados.getDataNasc() != null) {
            escambistaAtual.setDataNasc(dadosAtualizados.getDataNasc());
        }
        if (dadosAtualizados.getQuerNotifi() != null) {
            escambistaAtual.setQuerNotifi(dadosAtualizados.getQuerNotifi());
        }

        escambistaDAO.atualizarEscambista(escambistaAtual);

        return ResponseEntity.ok(Map.of("message", "Perfil atualizado com sucesso."));
    }
}