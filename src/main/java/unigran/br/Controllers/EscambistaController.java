package unigran.br.Controllers;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unigran.br.Model.DAO.*;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Escambista;

import java.util.Map;

@RestController
@RequestMapping("/api/escambista")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class EscambistaController {

    @Autowired
    private EscambistaDAO escambistaDAO;

    @Autowired
    private CadastroDAO cadastroDAO;

    @Autowired
    private AvaliacaoDAO avaliacoesDAO;

    @Autowired
    private FavoritoDAO favoritoDAO;

    @Autowired
    private AreaMatchVistoDAO areaMatchDAO;

    @Autowired
    private ChatDAO chatDAO;

    @Autowired
    private PropostaDAO propostaDAO;

    @Autowired
    private PostagemDAO postagemDAO;

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

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

        if (dadosAtualizados.getCpf() != null) {
            if (!validarCpf(dadosAtualizados.getCpf())) {
                return ResponseEntity.badRequest().body(Map.of("message", "CPF inválido."));
            }

            Escambista outro = escambistaDAO.buscarPorCpf(dadosAtualizados.getCpf());
            if (outro != null && !outro.getUserNome().equals(userNome)) {
                return ResponseEntity.badRequest().body(Map.of("message", "CPF já cadastrado por outro usuário."));
            }
            escambistaAtual.setCpf(dadosAtualizados.getCpf());
        }

        if (dadosAtualizados.getContato() != null && !dadosAtualizados.getContato().isBlank()) {
            String telefoneFormatado;
            try {
                telefoneFormatado = formatarTelefoneE164(dadosAtualizados.getContato(), "BR");
            } catch (NumberParseException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Telefone inválido."));
            }
            escambistaAtual.setContato(telefoneFormatado);
        }

        if (dadosAtualizados.getNomeEscambista() != null) escambistaAtual.setNomeEscambista(dadosAtualizados.getNomeEscambista());
        if (dadosAtualizados.getEndereco() != null) escambistaAtual.setEndereco(dadosAtualizados.getEndereco());
        if (dadosAtualizados.getDataNasc() != null) escambistaAtual.setDataNasc(dadosAtualizados.getDataNasc());
        if (dadosAtualizados.getQuerNotifi() != null) escambistaAtual.setQuerNotifi(dadosAtualizados.getQuerNotifi());

        escambistaDAO.atualizarEscambista(escambistaAtual);
        return ResponseEntity.ok(Map.of("message", "Perfil atualizado com sucesso."));
    }

    @DeleteMapping("/excluir/{userId}")
    public ResponseEntity<?> excluirConta(@PathVariable Integer userId,
                                          @RequestBody Map<String, String> payload) {
        String senha = payload.get("senha");
        if (senha == null || senha.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Senha é obrigatória."));
        }

        Escambista escambista = escambistaDAO.encontrarPorUserId(userId);
        if (escambista == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Usuário não encontrado."));
        }

        Cadastro cadastro = cadastroDAO.encontrarCadastroPorId(Long.valueOf(userId));
        if (cadastro == null || !cadastro.getSenha().equals(senha)) {
            return ResponseEntity.status(403).body(Map.of("message", "Senha incorreta."));
        }

        avaliacoesDAO.removerPorUserId(Long.valueOf(userId));
        favoritoDAO.removerPorUserId(Long.valueOf(userId));
        areaMatchDAO.removerPorUserId(Long.valueOf(userId));
        chatDAO.removerChatsEMensagensPorUserId(Long.valueOf(userId));
        propostaDAO.removerPorUserId(Long.valueOf(userId));
        postagemDAO.removerPorUserId(Long.valueOf(userId));
        escambistaDAO.removerEscambista(escambista.getId());
        cadastroDAO.removerCadastro(cadastro.getId());

        return ResponseEntity.ok(Map.of("message", "Conta e todos os dados associados foram excluídos."));
    }

    private boolean validarCpf(String cpf) {
        if (cpf == null) return false;
        String s = cpf.replaceAll("\\D", "");
        if (s.length() != 11) return false;
        if (s.chars().distinct().count() == 1) return false;

        try {
            int[] nums = s.chars().map(c -> c - '0').toArray();
            int sum = 0;
            for (int i = 0; i < 9; i++) sum += nums[i] * (10 - i);
            int r = 11 - (sum % 11);
            int d1 = (r == 10 || r == 11) ? 0 : r;
            if (d1 != nums[9]) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) sum += nums[i] * (11 - i);
            r = 11 - (sum % 11);
            int d2 = (r == 10 || r == 11) ? 0 : r;
            return d2 == nums[10];
        } catch (Exception e) {
            return false;
        }
    }

    private String formatarTelefoneE164(String telefoneRaw, String regiao) throws NumberParseException {
        telefoneRaw = telefoneRaw.replaceAll("\\D", "");
        PhoneNumber number = phoneUtil.parse(telefoneRaw, regiao);
        if (!phoneUtil.isPossibleNumber(number) || !phoneUtil.isValidNumber(number)) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "Número inválido");
        }
        return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}