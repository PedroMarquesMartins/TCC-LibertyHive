package unigran.br.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.ResponseEntity;
import java.util.*;
//API do IBGE para consultar nomes de Cidade e UF (Desafogando o banco de dados)
@Service
public class LocalidadeService {

    private static final String URL_ESTADOS = "https://servicodados.ibge.gov.br/api/v1/localidades/estados";
    private static final String URL_CIDADES = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/{UF}/municipios";

    private final RestTemplate restTemplate;
    //API Solicitando ao IBGE
    public LocalidadeService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);   
        this.restTemplate = new RestTemplate(factory);
    }

    //Validação UF
    public Set<String> getUFsValidas() {
        ResponseEntity<List> response = restTemplate.getForEntity(URL_ESTADOS, List.class);
        List<Map<String, Object>> estados = response.getBody();

        Set<String> ufs = new HashSet<>();
        for (Map<String, Object> estado : estados) {
            ufs.add(((String) estado.get("sigla")).toUpperCase());
        }
        return ufs;
    }
    //Validação Cidade
    public Set<String> getCidadesPorUF(String uf) {
        ResponseEntity<List> response = restTemplate.getForEntity(URL_CIDADES, List.class, uf.toLowerCase());
        List<Map<String, Object>> cidades = response.getBody();

        Set<String> nomes = new HashSet<>();
        for (Map<String, Object> cidade : cidades) {
            nomes.add((String) cidade.get("nome"));
        }
        return nomes;
    }
}