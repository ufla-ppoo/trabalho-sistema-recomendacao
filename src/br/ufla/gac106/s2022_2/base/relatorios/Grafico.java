package br.ufla.gac106.s2022_2.base.relatorios;

import java.text.DecimalFormat;

import javax.swing.JFrame;  
import javax.swing.SwingUtilities;
  
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import br.ufla.gac106.s2022_2.base.Avaliacao;
import br.ufla.gac106.s2022_2.base.Avaliacoes;  
      
public class Grafico {
    
    /**
     * Cria e exibe (assincronamente) uma tela com um gráfico de barras com as avaliações feitas sobre os itens do tema
     */
    public void exibir(String tituloGrafico, Avaliacoes avaliacoes) {
        SwingUtilities.invokeLater(() -> {
            TelaGraficoBarra tela = new TelaGraficoBarra(tituloGrafico, avaliacoes);
            tela.setAlwaysOnTop(true);
            tela.pack();
            tela.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
            tela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            tela.setVisible(true);
        });  
    }  
    
    /**
     * Classe interna criada para deifnir uma tela com gráfico de barras para as avaliações
     */
    private class TelaGraficoBarra extends JFrame {  
          
        private static final long serialVersionUID = 1L;
        
        /**
         * Constrói a tela com o gráfico de barras
         */
        public TelaGraficoBarra(String titulo, Avaliacoes avaliacoes) {  
            super(titulo);  
              
            DefaultCategoryDataset dataset = criarDataset(avaliacoes);
            
            JFreeChart graficoBarra = ChartFactory.createBarChart(
                titulo,   // Titulo do Grafico
                avaliacoes.temaAvaliacao(), // Eixo X
                "Classificação Média",      // Eixo Y
                dataset);
            
            // Exibe os valores nas barras com formatação
            BarRenderer renderizador = (BarRenderer) graficoBarra.getCategoryPlot().getRenderer();
            renderizador.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00")));
            renderizador.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
            renderizador.setBaseItemLabelsVisible(true);
            
            // Exibindo os rótulos do eixo X na vertical
            graficoBarra.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);                        
            // Permitindo que os rótulos do eixo X tenham até 3 linhas
            graficoBarra.getCategoryPlot().getDomainAxis().setMaximumCategoryLabelLines(3);
            // Escondendo a legenda
            graficoBarra.getLegend().setVisible(false);

            ChartPanel painel = new ChartPanel(graficoBarra);  
            setContentPane(painel); 
        }  
        
        /**
         * Cria um dataset a partir das avaliações recebidas
         */
        private DefaultCategoryDataset criarDataset(Avaliacoes avaliacoes) {          
            String serie = avaliacoes.temaAvaliacao();
            
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
            
            for (Avaliacao avaliacao : avaliacoes.colecaoAvaliacoes()) {
                dataset.addValue(avaliacao.classificacaoMedia(), serie, avaliacao.nomeItemAvaliado());
            }
            
            return dataset;  
        }       
    }
}
