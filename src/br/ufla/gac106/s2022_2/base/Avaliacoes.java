package br.ufla.gac106.s2022_2.base;

import java.util.Collection;

/**
 * Interface que representa um conjunto de avaliações de itens do tema do trabalho
 */
public interface Avaliacoes {
    /**
     * Tema da avaliação (ex: "Obras Audivisuais")
     * @return o tema da avaliação
     */
    String temaAvaliacao();

    /**
     * Coleção de avaliações dos itens trabalhados no tema (ex: coleção de avaliações de filmes e séries)
     */
    Collection<Avaliacao> colecaoAvaliacoes();
}
