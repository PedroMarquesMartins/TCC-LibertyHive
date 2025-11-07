package unigran.br.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unigran.br.Model.DAO.AvaliacaoDAO;
import unigran.br.Model.DAO.PostagemDAO;
import unigran.br.Model.DAO.CadastroDAO;
import unigran.br.Model.Entidades.Cadastro;
import unigran.br.Model.Entidades.Postagem;
import unigran.br.JwtUtil;
import unigran.br.Services.LocalidadeService;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/postagens")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PostagemController {

    @Autowired
    private PostagemDAO postagemDAO;

    @Autowired
    private CadastroDAO cadastroDAO;

    @Autowired
    private LocalidadeService localidadeService;

    private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();

    @Autowired
    private JwtUtil jwtUtil;

    //Lista de categorias válidas do front-end
    private final List<String> categoriasValidas = Arrays.asList(
            "Tecnologia",
            "Celulares e Smartphones",
            "Acessórios para Celulares (Capas, Películas, Carregadores)",
            "Peças para Celulares",
            "Smartwatches e Pulseiras",
            "Informática",
            "Notebooks e Laptops",
            "Computadores de Mesa (Desktops)",
            "Componentes e Peças (Processadores, Placas de Vídeo, Memórias RAM)",
            "Tablets e Acessórios",
            "Impressoras e Suprimentos",
            "Monitores",
            "Acessórios para PC (Teclados, Mouses, Webcams)",
            "Redes e Wi-Fi (Roteadores, Repetidores)",
            "Videogames",
            "Consoles (PlayStation, Xbox, Nintendo)",
            "Jogos Físicos e Digitais",
            "Acessórios para Consoles (Controles, Headsets)",
            "PC Gamer",
            "Eletrônicos, Áudio e Vídeo",
            "TVs e Smart TVs",
            "Equipamentos de Áudio (Home Theaters, Soundbars, Caixas de Som)",
            "Fones de Ouvido",
            "Câmeras Digitais e Drones",
            "Projetores e Telas",
            "Casa, Móveis e Decoração",
            "Móveis",
            "Móveis para Quarto (Camas, Guarda-Roupas, Cômodas)",
            "Móveis para Sala de Estar (Sofás, Racks, Estantes)",
            "Móveis para Cozinha e Sala de Jantar (Mesas, Cadeiras, Armários)",
            "Móveis para Escritório",
            "Decoração",
            "Cortinas e Persianas",
            "Quadros, Vasos e Enfeites",
            "Tapetes",
            "Iluminação (Lustres, Luminárias, Lâmpadas)",
            "Cama, Mesa e Banho",
            "Roupas de Cama",
            "Toalhas de Banho e Rosto",
            "Toalhas de Mesa e Acessórios",
            "Utilidades Domésticas",
            "Utensílios de Cozinha (Panelas, Talheres, Potes)",
            "Organização da Casa (Caixas, Prateleiras, Cabides)",
            "Limpeza da Casa",
            "Jardim e Área Externa",
            "Móveis de Jardim",
            "Ferramentas de Jardinagem",
            "Churrasqueiras e Acessórios",
            "Piscinas e Acessórios",
            "Eletrodomésticos",
            "Cozinha",
            "Geladeiras e Freezers",
            "Fogões, Fornos e Cooktops",
            "Micro-ondas",
            "Lava-louças",
            "Pequenos Eletrodomésticos",
            "Cafeteiras",
            "Liquidificadores e Batedeiras",
            "Fritadeiras Elétricas (Air Fryer)",
            "Sanduicheiras e Grills",
            "Climatização e Cuidados com a Casa",
            "Ar Condicionado e Climatizadores",
            "Ventiladores e Circuladores de Ar",
            "Aspiradores de Pó e Robôs",
            "Máquinas de Lavar e Secar Roupas",
            "Ferros de Passar",
            "Moda e Acessórios",
            "Roupas",
            "Roupas Femininas (Vestidos, Blusas, Calças)",
            "Roupas Masculinas (Camisetas, Camisas, Bermudas)",
            "Moda Íntima e Lingerie",
            "Moda Praia",
            "Calçados",
            "Tênis",
            "Sapatos Sociais e Casuais",
            "Sandálias e Chinelos",
            "Botas",
            "Bolsas e Malas",
            "Bolsas Femininas",
            "Mochilas",
            "Malas de Viagem",
            "Acessórios",
            "Joias e Bijuterias",
            "Relógios",
            "Óculos de Sol e de Grau",
            "Cintos, Chapéus e Bonés",
            "Beleza e Cuidado Pessoal",
            "Perfumes",
            "Perfumes Femininos",
            "Perfumes Masculinos",
            "Maquiagem",
            "Rosto (Base, Corretivo, Pó)",
            "Olhos (Máscara de Cílios, Sombra, Delineador)",
            "Lábios (Batom, Gloss)",
            "Cuidados com a Pele (Skincare)",
            "Limpeza de Pele",
            "Hidratantes Faciais e Corporais",
            "Protetores Solares",
            "Cuidados com o Cabelo",
            "Shampoos e Condicionadores",
            "Tratamentos Capilares",
            "Modeladores e Finalizadores",
            "Barbearia e Cuidados Masculinos",
            "Esportes e Fitness",
            "Roupas e Calçados Esportivos",
            "Equipamentos de Academia e Musculação",
            "Suplementos Alimentares e Vitaminas",
            "Ciclismo",
            "Bicicletas",
            "Acessórios para Ciclistas (Capacetes, Luvas)",
            "Futebol, Vôlei e Basquete",
            "Lutas e Artes Marciais",
            "Esportes Aquáticos",
            "Brinquedos e Hobbies",
            "Brinquedos",
            "Bonecos e Bonecas",
            "Jogos de Tabuleiro e Cartas",
            "Blocos de Montar e Construção",
            "Veículos de Brinquedo",
            "Brinquedos para Bebês",
            "Hobbies",
            "Instrumentos Musicais",
            "Modelismo e Miniaturas",
            "Arte e Papelaria",
            "Colecionáveis (Figurinhas, Moedas, Selos)",
            "Veículos, Peças e Acessórios",
            "Carros e Caminhonetes",
            "Motos",
            "Peças para Veículos",
            "Motor e Componentes",
            "Freios e Suspensão",
            "Peças de Lataria",
            "Acessórios para Veículos",
            "Pneus e Rodas",
            "Som e Multimídia Automotivo",
            "GPS e Segurança",
            "Náutica (Barcos, Lanchas, Jet Skis)",
            "Cultura e Entretenimento",
            "Livros, Revistas e HQs",
            "Filmes e Séries (DVDs, Blu-ray)",
            "Música (CDs, Discos de Vinil)",
            "Antiguidades e Peças de Coleção",
            "Serviços",
            "Serviços Técnicos (Informática, Conserto de Eletrodomésticos)",
            "Aulas e Cursos (Idiomas, Música, Reforço Escolar)",
            "Consultoria (Negócios, Marketing, Jurídica)",
            "Design e Multimídia (Criação de Logos, Edição de Vídeo)",
            "Eventos e Festas (Fotografia, Buffet, Decoração)",
            "Reforma e Construção",
            "Imóveis",
            "Venda - Apartamentos",
            "Venda - Casas e Sobrados",
            "Venda - Terrenos e Lotes",
            "Aluguel - Apartamentos",
            "Aluguel - Casas e Sobrados",
            "Imóveis Comerciais",
            "Outra Categoria"
    );

    //Função Salvar postagem, recebendo os parâmetros do Front-End
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> salvarPostagemCompleta(
            @RequestParam("userNome") String userNome,
            @RequestParam("isProdOuServico") Integer isProdOuServico,
            @RequestParam("isDoacao") Integer isDoacao,
            @RequestParam("nomePostagem") String nomePostagem,
            @RequestParam("descricao") String descricao,
            @RequestParam("categoria") String categoria,
            @RequestParam("categoriaInteresse1") String categoriaInteresse1,
            @RequestParam(value = "categoriaInteresse2", required = false) String categoriaInteresse2,
            @RequestParam(value = "categoriaInteresse3", required = false) String categoriaInteresse3,
            @RequestParam("cidade") String cidade,
            @RequestParam("uf") String uf,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem,
            @RequestParam(value = "imagensSecundarias", required = false) List<MultipartFile> imagensSecundarias
    ) {
        //VALIDAÇÕES de campos obrigatorios
        if (nomePostagem == null || nomePostagem.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Nome da postagem é obrigatório."));
        if (descricao == null || descricao.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Descrição é obrigatória."));
        if (!localidadeService.getUFsValidas().contains(uf.toUpperCase()))
            return ResponseEntity.badRequest().body(Map.of("error", "UF inválida."));
        if (!localidadeService.getCidadesPorUF(uf).contains(cidade))
            return ResponseEntity.badRequest().body(Map.of("error", "Cidade não pertence à UF informada ou não existe."));

        //Validação de categorias
        if (!categoriasValidas.contains(categoria))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria principal inválida."));
        if (categoriaInteresse1 != null && !categoriaInteresse1.isBlank() && !categoriasValidas.contains(categoriaInteresse1))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria de interesse 1 inválida."));
        if (categoriaInteresse2 != null && !categoriaInteresse2.isBlank() && !categoriasValidas.contains(categoriaInteresse2))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria de interesse 2 inválida."));
        if (categoriaInteresse3 != null && !categoriaInteresse3.isBlank() && !categoriasValidas.contains(categoriaInteresse3))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria de interesse 3 inválida."));

        //Ao menos 1 categoria de interesse obrigatória para itens que não são para doaçao
        boolean ehDoacao = (isDoacao != null && isDoacao == 1);
        if (!ehDoacao){
            boolean temAlgumaCategoriaInteresse =
                    (categoriaInteresse1 != null && !categoriaInteresse1.isBlank()) ||
                            (categoriaInteresse2 != null && !categoriaInteresse2.isBlank()) ||
                            (categoriaInteresse3 != null && !categoriaInteresse3.isBlank());
            if (!temAlgumaCategoriaInteresse) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ao menos uma categoria de interesse é obrigatória para trocas."));
            }
        }

        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(userNome);
        if (cadastro == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Usuário não encontrado."));

        //Salvando Postagem
        Postagem postagem = new Postagem();
        postagem.setId(null);
        postagem.setUserNome(userNome);
        postagem.setDoacao(ehDoacao);
        postagem.setIsProdOuServico(isProdOuServico != null && isProdOuServico == 1);
        postagem.setNomePostagem(nomePostagem);
        postagem.setDescricao(descricao);
        postagem.setCategoria(categoria);
        postagem.setDisponibilidade(true);

        postagem.setCategoriaInteresse1(categoriaInteresse1);
        postagem.setCategoriaInteresse2(categoriaInteresse2);
        postagem.setCategoriaInteresse3(categoriaInteresse3);

        postagem.setCidade(cidade);
        postagem.setUf(uf);
        postagem.setUserID(cadastro.getId());

        //Salvando as imagens
        if (imagem != null && !imagem.isEmpty()) {
            try {
                postagem.setImagem(imagem.getBytes());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Erro ao processar imagem de capa."));
            }
        }
        //Salvando as imagens aplicando limite de 5
        if (imagensSecundarias != null) {
            if (imagensSecundarias.size() > 5)
                return ResponseEntity.badRequest().body(Map.of("error", "O limite é de 5 imagens secundárias."));
            try {
                for (int i = 0; i < imagensSecundarias.size(); i++) {
                    MultipartFile file = imagensSecundarias.get(i);
                    if (file != null && !file.isEmpty()) {
                        byte[] imgBytes = file.getBytes();
                        switch (i) {
                            case 0 -> postagem.setImagemS01(imgBytes);
                            case 1 -> postagem.setImagemS02(imgBytes);
                            case 2 -> postagem.setImagemS03(imgBytes);
                            case 3 -> postagem.setImagemS04(imgBytes);
                            case 4 -> postagem.setImagemS05(imgBytes);
                        }
                    }
                }
            } catch (IOException e) {
                return ResponseEntity.status(500).body(Map.of("error", "Erro ao salvar imagens secundárias: " + e.getMessage()));
            }
        }

        postagemDAO.salvarPostagem(postagem);
        return ResponseEntity.ok(Map.of("message", "Postagem e imagens salvas com sucesso!", "id", postagem.getId()));
    }
    //Remoção das postagens
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerPostagem(@PathVariable Long id) {
        Postagem postagem = postagemDAO.encontrarPostagemPorId(id);
        if (postagem == null)
            return ResponseEntity.notFound().build();

        postagemDAO.removerPostagem(id);
        return ResponseEntity.ok(Map.of("message", "Postagem removida com sucesso!"));
    }
    //Listagem das postagens do usuário
    @GetMapping("/listar")
    public ResponseEntity<List<Postagem>> listarPostagens(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(401).build();

        String token = authHeader.substring(7);
        if (!jwtUtil.validarToken(token))
            return ResponseEntity.status(401).build();

        String userNome = jwtUtil.extrairUserNome(token);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(userNome);
        if (cadastro == null)
            return ResponseEntity.status(401).build();

        List<Postagem> postagensUsuario = postagemDAO.listarPorUserID(cadastro.getId())
                .stream()
                .filter(Postagem::getDisponibilidade)
                .toList();
        return ResponseEntity.ok(postagensUsuario);
    }

    //Listar todas as postagens
    @GetMapping("/listar-todas")
    public ResponseEntity<List<Map<String, Object>>> listarTodas(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(401).build();
        }

        List<Postagem> todasPostagens = postagemDAO.listarTodas().stream()
                .filter(p -> Boolean.TRUE.equals(p.getDisponibilidade()))
                .toList();

        List<Map<String, Object>> resultadoFinal = new ArrayList<>();
        for (Postagem p : todasPostagens) {
            Map<String, Object> postagemMap = new HashMap<>();
            postagemMap.put("id", p.getId());
            postagemMap.put("userNome", p.getUserNome());
            postagemMap.put("nomePostagem", p.getNomePostagem());
            postagemMap.put("descricao", p.getDescricao());
            postagemMap.put("categoria", p.getCategoria());
            postagemMap.put("isProdOuServico", p.getIsProdOuServico());
            postagemMap.put("doacao", p.getDoacao());
            postagemMap.put("cidade", p.getCidade());
            postagemMap.put("uf", p.getUf());
            postagemMap.put("imagem", p.getImagem());
            postagemMap.put("userID", p.getUserID());

            Map<String, Object> avaliacaoInfo = avaliacaoDAO.calcularMediaPorUsuarioId(p.getUserID());
            postagemMap.put("avaliacaoUsuario", avaliacaoInfo.get("media"));

            resultadoFinal.add(postagemMap);
        }

        return ResponseEntity.ok(resultadoFinal);
    }

    //Buscar postagem por ID
    @GetMapping("/{id}")
    public ResponseEntity<Postagem> buscarPostagemPorId(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        //Autenticação do user
        String token = authHeader.substring(7);
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Postagem postagem = postagemDAO.encontrarPostagemPorId(id);
        if (postagem == null || !Boolean.TRUE.equals(postagem.getDisponibilidade())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postagem);
    }


    //Listar categorias de postagens do usuário
    @GetMapping("/listar-categorias-usuario")
    public ResponseEntity<List<Map<String, Object>>> listarCategoriasPorUsuario(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(401).build();
        }

        String userNome = jwtUtil.extrairUserNome(token);
        Cadastro cadastro = cadastroDAO.encontrarPorUserNome(userNome);
        if (cadastro == null) {
            return ResponseEntity.status(401).build();
        }

        //retorna apenas id + categoria
        List<Postagem> postagensUsuario = postagemDAO.listarPorUserID(cadastro.getId());
        List<Map<String, Object>> resp = new ArrayList<>();
        for (Postagem p : postagensUsuario) {
            resp.add(Map.of(
                    "id", p.getId(),
                    "categoria", p.getCategoria()
            ));
        }
        return ResponseEntity.ok(resp);
    }

    //Atualização da postagem
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> atualizarPostagem(
            @PathVariable Long id,
            @RequestParam("userNome") String userNome,
            @RequestParam("isProdOuServico") Integer isProdOuServico,
            @RequestParam("isDoacao") Integer isDoacao,
            @RequestParam("nomePostagem") String nomePostagem,
            @RequestParam("descricao") String descricao,
            @RequestParam("categoria") String categoria,
            @RequestParam("categoriaInteresse1") String categoriaInteresse1,
            @RequestParam(value = "categoriaInteresse2", required = false) String categoriaInteresse2,
            @RequestParam(value = "categoriaInteresse3", required = false) String categoriaInteresse3,
            @RequestParam("cidade") String cidade,
            @RequestParam("uf") String uf,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem,
            @RequestParam(value = "imagensSecundarias", required = false) List<MultipartFile> imagensSecundarias
    ) {
        Postagem postagemExistente = postagemDAO.encontrarPostagemPorId(id);
        if (postagemExistente == null) {
            return ResponseEntity.notFound().build();
        }

        //Validações
        if (nomePostagem == null || nomePostagem.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Nome da postagem é obrigatório."));
        if (descricao == null || descricao.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Descrição é obrigatória."));
        if (!localidadeService.getUFsValidas().contains(uf.toUpperCase()))
            return ResponseEntity.badRequest().body(Map.of("error", "UF inválida."));
        if (!localidadeService.getCidadesPorUF(uf).contains(cidade))
            return ResponseEntity.badRequest().body(Map.of("error", "Cidade não pertence à UF informada ou não existe."));
        if (!categoriasValidas.contains(categoria))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria principal inválida."));
        if (categoriaInteresse1 != null && !categoriaInteresse1.isBlank() && !categoriasValidas.contains(categoriaInteresse1))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria de interesse 1 inválida."));
        if (categoriaInteresse2 != null && !categoriaInteresse2.isBlank() && !categoriasValidas.contains(categoriaInteresse2))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria de interesse 2 inválida."));
        if (categoriaInteresse3 != null && !categoriaInteresse3.isBlank() && !categoriasValidas.contains(categoriaInteresse3))
            return ResponseEntity.badRequest().body(Map.of("error", "Categoria de interesse 3 inválida."));

        boolean ehDoacao = (isDoacao != null && isDoacao == 1);
        if (!ehDoacao) {
            boolean temAlgumaCategoriaInteresse =
                    (categoriaInteresse1 != null && !categoriaInteresse1.isBlank()) ||
                            (categoriaInteresse2 != null && !categoriaInteresse2.isBlank()) ||
                            (categoriaInteresse3 != null && !categoriaInteresse3.isBlank());
            if (!temAlgumaCategoriaInteresse) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ao menos uma categoria de interesse é obrigatória para trocas."));
            }
        }

        //Atualiza para os novos dados
        postagemExistente.setNomePostagem(nomePostagem);
        postagemExistente.setDescricao(descricao);
        postagemExistente.setCategoria(categoria);
        postagemExistente.setIsProdOuServico(isProdOuServico != null && isProdOuServico == 1);
        postagemExistente.setDoacao(isDoacao != null && isDoacao == 1);
        postagemExistente.setCategoriaInteresse1(categoriaInteresse1);
        postagemExistente.setCategoriaInteresse2(categoriaInteresse2);
        postagemExistente.setCategoriaInteresse3(categoriaInteresse3);
        postagemExistente.setCidade(cidade);
        postagemExistente.setUf(uf);
//substitui imagens se enviadas
        try {
            if (imagem != null && !imagem.isEmpty()) {
                postagemExistente.setImagem(imagem.getBytes());
            }

            if (imagensSecundarias != null) {
                for (int i = 0; i < imagensSecundarias.size(); i++) {
                    MultipartFile file = imagensSecundarias.get(i);
                    if (file != null && !file.isEmpty()) {
                        byte[] imgBytes = file.getBytes();
                        switch (i) {
                            case 0 -> postagemExistente.setImagemS01(imgBytes);
                            case 1 -> postagemExistente.setImagemS02(imgBytes);
                            case 2 -> postagemExistente.setImagemS03(imgBytes);
                            case 3 -> postagemExistente.setImagemS04(imgBytes);
                            case 4 -> postagemExistente.setImagemS05(imgBytes);
                        }
                    }
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao processar imagens: " + e.getMessage()));
        }

        //Finalmente, salva a postagem
        postagemDAO.salvarPostagem(postagemExistente);
        return ResponseEntity.ok(Map.of("message", "Postagem atualizada com sucesso!", "id", postagemExistente.getId()));
    }
}