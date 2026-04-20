import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.Comparator;

public class AppOficina {

    static final int MAX_PEDIDOS = 100;
    static Produto[] produtos;
    static int quantProdutos = 0;
    static String nomeArquivoDados = "produtos.txt";
    static IOrdenador<Produto> ordenador;

    static Produto[] produtosPorId;
    static Produto[] produtosPorDescricao;

    // #region utilidades
    static Scanner teclado;

    static <T extends Number> T lerNumero(String mensagem, Class<T> classe) {
        System.out.print(mensagem + ": ");
        T valor;
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("Tecle Enter para continuar.");
        teclado.nextLine();
    }

    static void cabecalho() {
        limparTela();
        System.out.println("XULAMBS COMÉRCIO DE COISINHAS v0.2\n================");
    }

    static int exibirMenuPrincipal() {
        Integer opcao;

        cabecalho();
        System.out.println("1 - Procurar produto");
        System.out.println("2 - Filtrar produtos por preço máximo");
        System.out.println("3 - Ordenar produtos");
        System.out.println("4 - Embaralhar produtos");
        System.out.println("5 - Listar produtos");
        System.out.println("0 - Finalizar");

        opcao = lerNumero("Digite sua opção", Integer.class);
        return opcao != null ? opcao.intValue() : -1;
    }

    static int exibirMenuOrdenadores() {
        Integer opcao;

        cabecalho();
        System.out.println("1 - Bolha");
        System.out.println("2 - Inserção");
        System.out.println("3 - Seleção");
        System.out.println("4 - Mergesort");
        System.out.println("0 - Finalizar");

        opcao = lerNumero("Digite sua opção", Integer.class);
        return opcao != null ? opcao.intValue() : -1;
    }

    static int exibirMenuComparadores() {
        Integer opcao;

        cabecalho();
        System.out.println("1 - Padrão");
        System.out.println("2 - Por código");

        opcao = lerNumero("Digite sua opção", Integer.class);
        return opcao != null ? opcao.intValue() : -1;
    }

    // #endregion
    static Produto[] carregarProdutos(String nomeArquivo) {
        Scanner dados;
        Produto[] dadosCarregados;
        try {
            dados = new Scanner(new File(nomeArquivo));
            int tamanho = Integer.parseInt(dados.nextLine());

            quantProdutos = 0;
            dadosCarregados = new Produto[tamanho];
            while (dados.hasNextLine() && quantProdutos < tamanho) {
                Produto novoProduto = Produto.criarDoTexto(dados.nextLine());
                dadosCarregados[quantProdutos] = novoProduto;
                quantProdutos++;
            }
            dados.close();
        } catch (FileNotFoundException fex) {
            System.out.println("Arquivo não encontrado. Produtos não carregados");
            dadosCarregados = null;
        }
        return dadosCarregados;
    }

    private static Produto buscaBinaria(Produto[] array, Object alvo, Comparator<Object> comp) {
        int inicio = 0;
        int fim = quantProdutos - 1;
        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            int res = comp.compare(array[meio], alvo);
            if (res == 0) return array[meio];
            if (res < 0) inicio = meio + 1;
            else fim = meio - 1;
        }
        return null;
    }

    static Produto localizarProduto() {
        cabecalho();
        System.out.println("Localizar por:\n1 - Identificador\n2 - Descrição");
        Integer opcao = lerNumero("Opção", Integer.class);

        if (opcao == null) {
            return null;
        }

        if (opcao.intValue() == 1) {
            Integer id = lerNumero("Digite o ID", Integer.class);
            if (id == null) {
                return null;
            }
            return buscaBinaria(produtosPorId, id, (p, obj) -> Integer.compare(((Produto) p).hashCode(), ((Integer) obj).intValue()));
        } else if (opcao.intValue() == 2) {
            System.out.print("Digite a descrição: ");
            String desc = teclado.nextLine();

            if (desc != null) {
                desc = desc.trim();
            }

            if (desc == null || desc.length() == 0) {
                return null;
            }

            return buscaBinaria(produtosPorDescricao, desc, (p, obj) -> {
                String descricaoProduto = ((Produto) p).getDescricao();
                String descricaoBuscada = (String) obj;

                if (descricaoProduto != null) {
                    descricaoProduto = descricaoProduto.trim();
                } else {
                    descricaoProduto = "";
                }

                if (descricaoBuscada != null) {
                    descricaoBuscada = descricaoBuscada.trim();
                } else {
                    descricaoBuscada = "";
                }

                return descricaoProduto.compareToIgnoreCase(descricaoBuscada);
            });
        }
        return null;
    }

    private static void mostrarProduto(Produto produto) {
        cabecalho();
        String mensagem = "Dados inválidos";

        if (produto != null) {
            mensagem = String.format("Dados do produto:\n%s", produto);
        }

        System.out.println(mensagem);
    }

    private static void filtrarPorPrecoMaximo() {
        cabecalho();
        System.out.println("Filtrando por valor máximo:");
        Double valor = lerNumero("valor", Double.class);

        if (valor == null) {
            System.out.println("Valor inválido.");
            return;
        }

        StringBuilder relatorio = new StringBuilder();
        for (int i = 0; i < quantProdutos; i++) {
            if (produtos[i].valorDeVenda() < valor.doubleValue()) {
                relatorio.append(produtos[i]).append("\n");
            }
        }
        System.out.println(relatorio.toString());
    }

    static void ordenarProdutos() {
        cabecalho();
        int opcaoAlgoritmo = exibirMenuOrdenadores();
        if (opcaoAlgoritmo == 0) return;
        if (opcaoAlgoritmo < 0) {
            System.out.println("Opção inválida!");
            return;
        }

        int opcaoComparador = exibirMenuComparadores();
        if (opcaoComparador < 0) {
            System.out.println("Opção inválida!");
            return;
        }

        Comparator<Produto> comp;
        if (opcaoComparador == 2) {
            comp = new ComparadorPorCodigo();
        } else {
            comp = (p1, p2) -> {
                String descricao1 = p1.getDescricao();
                String descricao2 = p2.getDescricao();

                if (descricao1 != null) {
                    descricao1 = descricao1.trim();
                } else {
                    descricao1 = "";
                }

                if (descricao2 != null) {
                    descricao2 = descricao2.trim();
                } else {
                    descricao2 = "";
                }

                return descricao1.compareToIgnoreCase(descricao2);
            };
        }

        switch (opcaoAlgoritmo) {
            case 1:
                ordenador = new BubbleSort<Produto>();
                break;
            case 2:
                ordenador = new InsertSort<Produto>();
                break;
            case 3:
                ordenador = new SelectionSort<Produto>();
                break;
            case 4:
                ordenador = new Mergesort<Produto>();
                break;
            default:
                System.out.println("Opção inválida!");
                return;
        }

        if (ordenador != null) {
            Produto[] copiaParaOrdenar = Arrays.copyOf(produtos, quantProdutos);

            ordenador.ordenar(copiaParaOrdenar, comp);

            System.out.println("Produtos ordenados com sucesso!");
            listarProdutos(copiaParaOrdenar);

            verificarSubstituicao(produtos, copiaParaOrdenar);
        }
    }

    static void embaralharProdutos() {
        Collections.shuffle(Arrays.asList(produtos));
    }

    static void verificarSubstituicao(Produto[] dadosOriginais, Produto[] copiaDados) {
        cabecalho();
        System.out.print("Deseja sobrescrever os dados originais pelos ordenados (S/N)?");
        String resposta = teclado.nextLine().toUpperCase();
        if (resposta.equals("S")) {
            for (int i = 0; i < quantProdutos; i++) {
                dadosOriginais[i] = copiaDados[i];
            }

            produtosPorId = Arrays.copyOf(produtos, quantProdutos);
            Arrays.sort(produtosPorId, (p1, p2) -> Integer.compare(p1.hashCode(), p2.hashCode()));

            produtosPorDescricao = Arrays.copyOf(produtos, quantProdutos);
            Arrays.sort(produtosPorDescricao, (p1, p2) -> {
                String descricao1 = p1.getDescricao();
                String descricao2 = p2.getDescricao();

                if (descricao1 != null) {
                    descricao1 = descricao1.trim();
                } else {
                    descricao1 = "";
                }

                if (descricao2 != null) {
                    descricao2 = descricao2.trim();
                } else {
                    descricao2 = "";
                }

                return descricao1.compareToIgnoreCase(descricao2);
            });
        }
    }

    static void listarProdutos() {
        cabecalho();
        for (int i = 0; i < quantProdutos; i++) {
            System.out.println(produtos[i]);
        }
    }

    static void listarProdutos(Produto[] listaProdutos) {
        cabecalho();
        for (int i = 0; i < listaProdutos.length; i++) {
            System.out.println(listaProdutos[i]);
        }
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in);

        produtos = carregarProdutos(nomeArquivoDados);

        if (produtos != null) {
            produtosPorId = Arrays.copyOf(produtos, quantProdutos);
            Arrays.sort(produtosPorId, (p1, p2) -> Integer.compare(p1.hashCode(), p2.hashCode()));

            produtosPorDescricao = Arrays.copyOf(produtos, quantProdutos);
            Arrays.sort(produtosPorDescricao, (p1, p2) -> {
                String descricao1 = p1.getDescricao();
                String descricao2 = p2.getDescricao();

                if (descricao1 != null) {
                    descricao1 = descricao1.trim();
                } else {
                    descricao1 = "";
                }

                if (descricao2 != null) {
                    descricao2 = descricao2.trim();
                } else {
                    descricao2 = "";
                }

                return descricao1.compareToIgnoreCase(descricao2);
            });
        }

        embaralharProdutos();

        int opcao = -1;

        do {
            opcao = exibirMenuPrincipal();
            switch (opcao) {
                case 1 -> mostrarProduto(localizarProduto());
                case 2 -> filtrarPorPrecoMaximo();
                case 3 -> ordenarProdutos();
                case 4 -> embaralharProdutos();
                case 5 -> listarProdutos();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
                default -> System.out.println("Opção inválida!");
            }
            pausa();
        } while (opcao != 0);
        teclado.close();
    }
}
