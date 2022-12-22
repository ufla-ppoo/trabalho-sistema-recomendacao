package br.ufla.gac106.s2022_2.base;

/**
 * Interface que representa uma avaliação de um item do tema trabalhado
 * (ex: avaliação de um filme ou sére)
 */
public interface Avaliacao {
    /**
     * Nome do item avaliado (ex: "O Senhor dos Anéis")
     * @return o nome do item
     */
    String nomeItemAvaliado();

    /**
     * Classificação média avaliada do item (ex: 4.5)
     */
    double classificacaoMedia();
}
