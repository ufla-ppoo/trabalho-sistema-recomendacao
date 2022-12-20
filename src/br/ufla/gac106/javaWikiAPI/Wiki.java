package br.ufla.gac106.javaWikiAPI;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Classe para obter dados de uma plataforma Wiki.
 * 
 * Pode ser a Wikipedia em qualquer idioma, ou qualquer outro site que utilize a
 * plataforma Wiki
 */
public class Wiki implements Closeable {
    // Endereço completo da API a ser utilizada
    private String endpoint;
    // Parâmetros padrões a serem utilizados em uma ação de consulta
    private Map<String, String> parametrosConsulta;
    // Parâmetros padrões a serem utilizados em uma ação de pesquisa
    private Map<String, String> parametrosPesquisa;
    // indica se está em modo de debug (nesse caso, são exibidas mensagens
    // detalhadas do que é feito)
    private boolean debug;

    /**
     * Constrói o objeto capaz de obter dados da Wikipedia em Português (endpoint
     * "https://pt.wikipedia.org/w/api.php").
     * Lembre-se de usar o método terminar quando não for mais usar o objeto.
     * 
     * Para obter dados de outras Wikis, utilize outro construtor (ou o método
     * setEndpoint).
     */
    public Wiki() {
        this("https://pt.wikipedia.org/w/api.php");
    }

    /**
     * Constrói o objeto capaz de obter dados de uma Wiki
     * Lembre-se de usar o método terminar quando não for mais usar o objeto.
     * 
     * @param endpoint Endpoint da Wiki a ser consultada (ex:
     *                 "https://en.wikipedia.org/w/api.php").
     */
    public Wiki(String endpoint) {
        this.endpoint = endpoint;
        debug = false;

        definirParametrosPadroes();
    }

    /**
     * Define parâmetros padrões a serem utilizados em todas as consultas
     */
    private void definirParametrosPadroes() {
        parametrosConsulta = Map.of(
                "action", "query",             // vamos fazer uma consulta
                "prop", "extracts|pageimages", // buscando pelo resumo e pela imagem (thumbnail) da página
                "exintro", "true",             // queremos o resumo que vem antes da primeira seção
                "explaintext", "true",         // queremos o texto puro em vez de HTML limitado
                "exsectionformat", "plain",    // e com o texto sem nenhuma formatação
                "pithumbsize", "300",          // a imagem deve ter largura máxima da imagem em pixels
                "redirects", "resolve",        // os redirecionamentos de página devem ser tratados
                "format", "json",              // a resposta deve vir no formato JSON
                "formatversion", "2"           // na versão 2
        );

        parametrosPesquisa = Map.of(
                "action", "opensearch", // vamos fazer uma pesquisa
                "redirects", "resolve", // os redirecionamentos de página devem ser tratados
                "format", "json",       // a resposta deve vir no formato JSON
                "formatversion", "2"    // na versão 2
        );
    }

    /**
     * Domínio que está sendo utilizado nas consultas
     * 
     * @return Domínio da Wiki
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Muda o domínio Wiki utilizado nas consultas (ex:
     * "https://en.wikipedia.org/w/api.php")
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Retorna se está em modo de debug (nesse caso, são exibidas mensagens
     * detalhadas do que é feito)
     * 
     * @return Indica se está em modo de debug
     */
    public Boolean getDebug() {
        return debug;
    }

    /**
     * Permite ligar/desligar modo de debug
     * 
     * @param debug Estado do modo de debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;

        if (debug) System.out.println("=> Wiki em modo de debug (endpoint: " + endpoint + ")");
    }

    /**
     * Libera os recursos utilizados
     */
    @Override
    public void close() throws IOException {
        // É necessário terminar o loop do Unirest para conseguir fechar o programa
        Unirest.shutdown();
    }

    /*
     * Retorna um objeto que representa a página Wiki cujo título foi passado (ou
     * null se a página não for encontrada).
     * Observações:
     * - O resumo é dado pelo texto que vem antes da primeira seção da página (nem
     * toda página da Wiki tem).
     * - A imagem é o 'thumbnail' da página (nem toda página da Wiki tem).
     * 
     * @param titulo Título da página a ser buscada
     * 
     * @returns Objeto da página Wiki buscada (ou null se ela não for encontrada)
     */
    public PaginaWiki consultarPagina(String titulo) throws JavaWikiInternalException, UnsuccessfulHTTPRequestException {
        try {
            if (debug) System.out.println("=> Wiki: Montando parâmetros da busca por uma página pelo título");

            Map<String, String> parametros = new HashMap<>(parametrosConsulta);

            // Vamos buscar a página cujo título foi passado
            parametros.put("titles", titulo);

            // Faz a requisição de consulta na API
            JsonObject jsonResposta = fazerRequisicao(parametros).getAsJsonObject();

            // Processa a resposta e retorna a página
            return processarRespostaConsulta(jsonResposta);
        }
        catch (UnsuccessfulHTTPRequestException e) {
            // se ocorrer uma exceção de requisição HTTP mal-sucedida, ela é simplesmente relançada
            throw e;
        }
        catch (Exception e) {
            // Caso ocorra qualquer outra exceção, lança uma exceção de erro interno na JavaWikiAPI,
            // passando a exceção original como a causa
            throw new JavaWikiInternalException(titulo, endpoint, e);
        }
    }

    /**
     * Faz uma busca pelo termo passado e retorna títulos de páginas relacionados ao
     * termo de busca (no máximo 10 títulos).
     * É útil quando se pretende buscar uma página mas não se tem certeza do título
     * dela na Wiki
     * 
     * @param termoDeBusca String utilizada para a busca
     * 
     * @return Uma lista de páginas retornada pela busca
     * @throws UnsuccessfulHTTPRequestException
     */
    public List<String> pesquisarTitulosDePaginas(String termoDeBusca) throws JavaWikiInternalException, UnsuccessfulHTTPRequestException {
        try {            
            if (debug) System.out.println("=> Wiki: Montando parâmetros da pesquisa por títulos de páginas");

            Map<String, String> parametros = new HashMap<>(parametrosPesquisa);

            // Parâmetro para passar o termo de busca
            parametros.put("search", termoDeBusca);

            // Faz a requisição de pesquisa na API
            JsonArray jsonResposta = fazerRequisicao(parametros).getAsJsonArray();

            // Processa a resposta da pesquisa e retorna a lista de títulos de página
            return processarRespostaPesquisa(jsonResposta);
        }
        catch (UnsuccessfulHTTPRequestException e) {
            // se ocorrer uma exceção de requisição HTTP mal-sucedida, ela é simplesmente relançada
            throw e;
        }
        catch (Exception e) {
            // Caso ocorra qualquer outra exceção, lança uma exceção de erro interno na JavaWikiAPI,
            // passando a exceção original como a causa
            throw new JavaWikiInternalException(termoDeBusca, endpoint, e);
        }
    }

    /**
     * Faz uma requisição na API da Wiki utilizando os parâmetros passados.
     * 
     * @param parametros Parâmetros da chamada da API
     * @return Elemento JSON retornado como resposta
     * @throws UnsuccessfulHTTPRequestException
     * @throws URISyntaxException
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     * @throws UnirestException
     * @throws Exception
     */
    private JsonElement fazerRequisicao(Map<String, String> parametros) throws UnsuccessfulHTTPRequestException, UnsupportedEncodingException, MalformedURLException, URISyntaxException, UnirestException {
        // se está em modo de debug, monta a requisição sem executá-la para obter a URL de consulta
        if (debug) {
            System.out.println("=> Wiki: URL da requisição: " + Unirest.get(endpoint).queryString(new HashMap<String, Object>(parametros)).getUrl());
        }
        // Faz a requisição na API
        HttpResponse<JsonNode> response = Unirest.get(endpoint).queryString(new HashMap<String, Object>(parametros)).asJson();        

        // Se a requisição foi bem-sucedida
        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            // se está em modo de debug, exibe a resposta da requisição em formato amigável
            if (debug)  System.out.println("=> Wiki: Resposta da requisição: " + JSONUtils.stringAmigavel(response.getBody().toString()));

            // Cria um elemento JSON a partir da String JSON restornada
            return JsonParser.parseString(response.getBody().toString());
        } 
        // se a requisição NÃO deu certo
        else {
            // Lança uma exceção específica para essa situação           
            throw new UnsuccessfulHTTPRequestException(response.getStatus(), response.getStatusText());
        }
    }

    /**
     * Processa a resposta a uma requisição de consulta
     * @throws IOException
     * @throws MalformedURLException
     * 
     * @para jsonConsulta Objeto JSON retornado pela API
     */
    private PaginaWiki processarRespostaConsulta(JsonObject jsonConsulta) throws MalformedURLException {
        if (debug) System.out.println("=> Wiki: Processando retorno da requisição");

        // se a resposta veio completa
        if (jsonConsulta.get("batchcomplete").getAsBoolean()) {

            // Obtém o elemento com informações da consulta
            JsonObject query = jsonConsulta.get("query").getAsJsonObject();

            // Se encontrou informações da página
            if (query.get("pages") != null) {

                // Obtém o elemento com informações da página
                JsonObject pagina =  query.get("pages").getAsJsonArray().get(0).getAsJsonObject();

                // Se o retorno é inválido
                if (pagina.get("invalid") != null) {
                    if (debug) System.out.println("=> Wiki: página não encontrada, motivo: " + pagina.get("invalidreason").getAsString());            
                } 
                // Se a página ainda não existe
                else if (pagina.get("missing") != null) {
                    if (debug) System.out.println("=> Wiki: página de título '" + pagina.get("title").getAsString() + "' não existe.");
                } 
                // Se a página foi encontrada
                else {
                    // Obtém o resumo da página (se ele foi retornado)
                    String resumo = "";
                    if (pagina.get("extract") != null) {
                        resumo = pagina.get("extract").getAsString();
                    }
                    
                    // Obtém o imagem (thumbnail) da página (se ela foi retornada)
                    BufferedImage imagem = null;
                    if (pagina.get("thumbnail") != null) {
                        String endereçoDaImagem = pagina.get("thumbnail").getAsJsonObject().get("source").getAsString();
                        try {
                            imagem = ImageIO.read(new URL(endereçoDaImagem));
                        }
                        catch (IOException e) {
                            if (debug) System.err.println("=> Wiki: Erro ao tentar obter imagem da página '" + pagina.get("title").getAsString() + "'. URL: " + endereçoDaImagem);
                        }
                    }

                    // Cria e retorna um objeto que representa a página Wiki obtida
                    return new PaginaWiki(pagina.get("title").getAsString(),
                                        pagina.get("pageid").getAsInt(),
                                        resumo, imagem);
                }
            }
            // Se a página não foi obtida com sucesso, retorna null
            return null;
        } else {
            // Caso o retorno não seja completo, lança exceção indicando a situação
            throw new UnsupportedOperationException("Wiki: Ainda não há tratamento para consultas em lote");
        }
    }

    /**
     * Processa a resposta a uma requisição de pesquisa
     * 
     * @para jsonArray Objeto JSON Array retornado pela API
     */
    private List<String> processarRespostaPesquisa(JsonArray jsonArray) {
        if (debug)
            System.out.println("=> Wiki: Processando retorno da requisição");

        List<String> titulos = new ArrayList<>();

        for (JsonElement element : jsonArray.get(1).getAsJsonArray()) {
            titulos.add(element.getAsString());
        }

        return titulos;
    }
}
