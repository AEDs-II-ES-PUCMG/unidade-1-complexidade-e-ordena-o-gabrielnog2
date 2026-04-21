import java.util.Comparator;

/**
 * Critério C - Índice de Economia (decrescente).
 * O índice de economia é a diferença entre o valor de catálogo atual e o valor efetivamente pago.
 * Desempate 1: Valor Final do Pedido (crescente).
 * Desempate 2: Código Identificador do pedido (crescente).
 */
public class ComparadorCriterioC implements Comparator<Pedido> {

    @Override
    public int compare(Pedido p1, Pedido p2) {
        int cmp = Double.compare(p1.ticketMedio(), p2.ticketMedio());
        if (cmp != 0) return cmp;
        cmp = Double.compare(p1.valorFinal(), p2.valorFinal());
        if (cmp != 0) return cmp;
        return Integer.compare(p1.codigoPrimeiroItem(), p2.codigoPrimeiroItem());
    }
}
