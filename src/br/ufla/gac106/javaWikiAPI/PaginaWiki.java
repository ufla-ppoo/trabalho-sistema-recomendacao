package br.ufla.gac106.javaWikiAPI;

import java.awt.image.BufferedImage;

/**
 * Classe que representa uma Página Wiki consultada
 */
public class PaginaWiki {

    // Título da Página
    private String titulo;
    // Identificador da página na Wiki
    private int id;
    // Resumo da página
    private String resumo;
    // Imagem (thumbnail) representativa da página
    private BufferedImage imagem;

    /*
     * Constrói um objeto de uma página Wiki a partir de seu título e identificador
     * na Wiki
     * 
     * @param titulo Título da página na Wiki
     * 
     * @param id Identificador da página na Wiki
     */
    public PaginaWiki(String titulo, int id) {
        this(titulo, id, "");
    }

    /*
     * Constrói um objeto de uma página Wiki a partir de seu título, identificador e
     * texto de resumo
     * 
     * @param titulo Título da página na Wiki
     * 
     * @param id Identificador da página na Wiki
     * 
     * @param resumo Texto de resumo da página na Wiki
     */
    public PaginaWiki(String titulo, int id, String resumo) {
        this(titulo, id, resumo, null);
    }

    /*
     * Constrói um objeto de uma página Wiki a partir de seu título, identificador,
     * texto de resumo e imagem representativa
     * 
     * @param titulo Título da página na Wiki
     * 
     * @param id Identificador da página na Wiki
     * 
     * @param resumo Texto de resumo da página na Wiki
     * 
     * @param imagem Imagem (thumbnail) que representa a página na Wiki
     */
    public PaginaWiki(String titulo, int id, String resumo, BufferedImage imagem) {
        this.titulo = titulo;
        this.id = id;
        this.resumo = resumo;
        this.imagem = imagem;
    }

    /**
     * Título da página na Wiki
     * 
     * @return O título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Identificador da página na Wiki
     * 
     * @return O identificador
     */
    public int getId() {
        return id;
    }

    /**
     * Resumo da página na Wiki
     * 
     * @return O resumo
     */
    public String getResumo() {
        return resumo;
    }

    /**
     * Imagem (thubmnail) representativa da página
     * 
     * @return A imagem
     */
    public BufferedImage getImagem() {
        return imagem;
    }

    /**
     * Retorna uma representação da página como String
     */
    @Override
    public String toString() {
        return "PaginaWiki: " + titulo + " (id=" + id + ")\n" + resumo;
    }
}
