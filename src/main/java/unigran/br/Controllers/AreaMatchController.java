package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unigran.br.Model.DAO.*;
import unigran.br.Model.Entidades.*;
import unigran.br.JwtUtil;

import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/area-match")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AreaMatchController {

    @Autowired
    private PostagemDAO postagemDAO;
    @Autowired
    private EscambistaDAO escambistaDAO;
    @Autowired
    private CadastroDAO cadastroDAO;
    @Autowired
    private AreaMatchVistoDAO areaMatchVistoDAO;


    @Autowired
    private FavoritoDAO favoritoDAO;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/matches")
    public ResponseEntity<List<Map<String, Object>>> listarMatches(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return ResponseEntity.status(401).build();

        String token = authHeader.substring(7);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (cadastro == null) return ResponseEntity.status(401).build();

        List<Postagem> postagensUsuario = postagemDAO.listarPorUserID(cadastro.getId());
        List<Postagem> postagensOutros = postagemDAO.listarTodas();

        Map<Long, Postagem> resultadoMap = new LinkedHashMap<>();
        List<Long> vistos = areaMatchVistoDAO.listarIdsVistosPorUsuario(cadastro.getId());

        for (Postagem minhaPostagem : postagensUsuario) {
            List<String> interesses = new ArrayList<>();
            if (minhaPostagem.getCategoriaInteresse1() != null) interesses.add(minhaPostagem.getCategoriaInteresse1());
            if (minhaPostagem.getCategoriaInteresse2() != null) interesses.add(minhaPostagem.getCategoriaInteresse2());
            if (minhaPostagem.getCategoriaInteresse3() != null) interesses.add(minhaPostagem.getCategoriaInteresse3());

            for (Postagem outraPostagem : postagensOutros) {
                if (outraPostagem.getUserID().equals(cadastro.getId())) continue;

                if (vistos.contains(outraPostagem.getId())) continue;
                if (!Boolean.TRUE.equals(outraPostagem.getDisponibilidade())) continue;
                boolean match = interesses.stream()
                        .anyMatch(i -> i != null && i.equalsIgnoreCase(outraPostagem.getCategoria()))
                        && Stream.of(
                        outraPostagem.getCategoriaInteresse1(),
                        outraPostagem.getCategoriaInteresse2(),
                        outraPostagem.getCategoriaInteresse3()
                ).anyMatch(c -> c != null && c.equalsIgnoreCase(minhaPostagem.getCategoria()));

                if (match) {
                    resultadoMap.put(outraPostagem.getId(), outraPostagem);
                }
            }
        }

        List<Map<String, Object>> listaFinal = new ArrayList<>();

        for (Postagem p : resultadoMap.values()) {
            if (!Boolean.TRUE.equals(p.getDisponibilidade())) continue;
            Escambista escambista = escambistaDAO.encontrarPorUserId(p.getUserID().intValue());
            Integer avaliacao = (escambista != null) ? escambista.getAvaliacao() : null;

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", p.getId());
            itemMap.put("userNome", p.getUserNome());
            itemMap.put("nomePostagem", p.getNomePostagem());
            itemMap.put("categoria", p.getCategoria());
            itemMap.put("categoriaInteresse1", p.getCategoriaInteresse1());
            itemMap.put("categoriaInteresse2", p.getCategoriaInteresse2());
            itemMap.put("categoriaInteresse3", p.getCategoriaInteresse3());
            itemMap.put("isProdOuServico", p.getIsProdOuServico());
            itemMap.put("isDoacao", p.getDoacao());
            itemMap.put("cidade", p.getCidade());
            itemMap.put("uf", p.getUf());
            itemMap.put("imagem", p.getImagem());
            itemMap.put("avaliacaoUsuario", avaliacao);

            listaFinal.add(itemMap);
        }
        return ResponseEntity.ok(listaFinal);
    }

    @PostMapping("/interesse")
    public ResponseEntity<?> marcarInteresse(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long itemOutroUsuarioId,
            @RequestParam boolean sim
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (cadastro == null) {
            return ResponseEntity.status(401).build();
        }

        Postagem outroItem = postagemDAO.encontrarPostagemPorId(itemOutroUsuarioId);
        if (outroItem == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Item não encontrado."));
        }
        if (outroItem.getUserID().equals(cadastro.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Não é possível dar ação no próprio item."));
        }

        if (!sim) {
            if (!areaMatchVistoDAO.existeRegistro(cadastro.getId(), itemOutroUsuarioId)) {
                AreaMatchVisto visto = new AreaMatchVisto();
                visto.setUserId(cadastro.getId());
                visto.setPostagemId(itemOutroUsuarioId);
                areaMatchVistoDAO.salvar(visto);
            }
            return ResponseEntity.ok(Map.of("message", "Item marcado como 'passado' e não aparecerá mais."));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Interesse registrado com sucesso (não salvo em vistos).",
                "userId", cadastro.getId(),
                "itemOutroUsuarioId", itemOutroUsuarioId
        ));
    }

    @PostMapping("/favorito")
    public ResponseEntity<?> favoritar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long postagemId
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(jwtUtil.extrairUserNome(token));
        if (cadastro == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            if (favoritoDAO.existeFavorito(cadastro.getId(), postagemId)) {
                return ResponseEntity.status(409).body(Map.of("message", "Este item já foi favoritado por você."));
            }

            Favorito favorito = new Favorito();
            favorito.setUserId(cadastro.getId());
            favorito.setPostagemId(postagemId);
            favoritoDAO.salvar(favorito);

            return ResponseEntity.ok(Map.of("message", "Item favoritado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao favoritar: " + e.getMessage()));
        }
    }
}