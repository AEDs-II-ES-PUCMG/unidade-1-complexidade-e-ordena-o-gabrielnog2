import java.util.Comparator;

/**
 * Critério A - Valor Final do Pedido (crescente).
 * Desempate 1: Volume Total de Itens (quantProdutos).
 * Desempate 2: Código Identificador do primeiro item do pedido.
 */
public class ComparadorCriterioA implements Comparator<Pedido> {

    @Override
    public int compare(Pedido p1, Pedido p2) {
        int cmp = Double.compare(p1.valorFinal(), p2.valorFinal());
        if (cmp != 0) return cmp;
        cmp = Integer.compare(p1.volumeTotal(), p2.volumeTotal());
        if (cmp != 0) return cmp;
        return Integer.compare(p1.codigoPrimeiroItem(), p2.codigoPrimeiroItem());
    }
}
