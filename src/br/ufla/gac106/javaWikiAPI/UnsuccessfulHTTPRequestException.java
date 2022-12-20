package br.ufla.gac106.javaWikiAPI;

/**
 * Exceção lançada quando uma requisição a uma Wiki não é bem-sucedida.
 * Basicamente sempre que o status da requisição HTTP for diferente de 200.
 */
public class UnsuccessfulHTTPRequestException extends Exception {
    // Código de status da requisição HTTP mal-sucedida
    public int statusCode;
    // Descrição da requisição HTTP mal-sucedida
    public String statusText;

    /**
     * Constrói a exceção
     * 
     * @param statusCode Código de status da requisição HTTP mal-sucedida
     * @param statusText Descrição da requisição HTTP mal-sucedida
     */
    public UnsuccessfulHTTPRequestException(int statusCode, String statusText) {
        super("Requisição HTTP mal-sucedida (status code " + statusCode + ")");
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    /**
     * Código de status da requisição HTTP mal-sucedida
     * 
     * @return O código
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Descrição da requisição HTTP mal-sucedida
     * 
     * @return A descrição
     */
    public String getStatusText() {
        return statusText;
    }

}