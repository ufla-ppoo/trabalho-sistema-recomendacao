package br.ufla.gac106.javaWikiAPI;

/**
 * Classe utilizada para lançar exceções de problemas internos no código da JavaWikiAPI
 */
public class JavaWikiInternalException extends Exception {
    // Termo (título de página ou termo de busca) usado na requisição à API da Wiki
    public String termoConsulta;

    // Endereço da API Wiki utilizada
    public String endpoint;

    /**
     * Constrói a exceção
     * 
     * @param excecaoOriginal Exceção que originou o erro interno
     */
    public JavaWikiInternalException(String termoConsulta, String endpoint, Exception excecaoOriginal) {
        super("Ocorreu um erro interno na JavaWikiAPI! Use getCause() para obter a exceção que originou o erro.",
              excecaoOriginal);
        this.termoConsulta = termoConsulta;
        this.endpoint = endpoint;
    }

    /**
     * Termo (título de página ou termo de busca) usado na requisição à API da Wiki
     * 
     * @return O termo
     */
    public String getTermoConsulta() {
        return termoConsulta;
    }

    /**
     * Endereço da API Wiki utilizada
     * 
     * @return O endereço
     */
    public String getEndpoint() {
        return endpoint;
    }
}