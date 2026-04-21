import java.util.Comparator;

/**
 * Critério B - Forma de Pagamento (crescente).
 * Desempate 1: Valor Final.
 * Desempate 2: Código do Primeiro Item.
 */
public class ComparadorCriterioB implements Comparator<Pedido> {

    @Override
    public int compare(Pedido p1, Pedido p2) {
        int cmp = Integer.compare(p1.formaDePagamento(), p2.formaDePagamento());
        if (cmp != 0) return cmp;
        cmp = Double.compare(p1.valorFinal(), p2.valorFinal());
        if (cmp != 0) return cmp;
        return Integer.compare(p1.codigoPrimeiroItem(), p2.codigoPrimeiroItem());
    }
}
