package controller;

import model.*;
import java.util.List;

/**
 * Controller responsável pelas operações de compra e carrinho.
 */
public class CompraController {

    private Supermercado supermercado;
    private ProdutoDAO produtoDAO;

    public CompraController(Supermercado supermercado) {
        this.supermercado = supermercado;
        this.produtoDAO = new ProdutoDAO();
    }

    /**
     * Adiciona um produto ao carrinho.
     * @return "OK" ou mensagem de erro.
     */
    public String adicionarAoCarrinho(Produto produto, int quantidade) {
        if (produto == null) return "Produto inválido.";
        if (quantidade <= 0) return "Quantidade deve ser maior que zero.";

        // Calcula quanto já está no carrinho
        int jaNoCarrinho = 0;
        for (ItemCarrinho item : supermercado.getCarrinho()) {
            if (item.getProduto().getId() == produto.getId()) {
                jaNoCarrinho = item.getQuantidade();
                break;
            }
        }

        if ((jaNoCarrinho + quantidade) > produto.getQuantidadeEstoque()) {
            return "Quantidade indisponível em estoque. Disponível: "
                   + (produto.getQuantidadeEstoque() - jaNoCarrinho);
        }

        boolean sucesso = supermercado.adicionarAoCarrinho(produto, quantidade);
        return sucesso ? "OK" : "Não foi possível adicionar ao carrinho.";
    }

    /**
     * Remove um produto do carrinho pelo ID.
     */
    public boolean removerDoCarrinho(int produtoId) {
        return supermercado.removerDoCarrinho(produtoId);
    }

    /**
     * Atualiza a quantidade de um item no carrinho.
     * @return "OK" ou mensagem de erro.
     */
    public String atualizarQuantidade(int produtoId, int novaQtd) {
        if (novaQtd < 0) return "Quantidade inválida.";
        boolean sucesso = supermercado.atualizarQuantidade(produtoId, novaQtd);
        return sucesso ? "OK" : "Não foi possível atualizar a quantidade.";
    }

    /**
     * Retorna o total a pagar.
     */
    public double calcularTotal() {
        return supermercado.calcularTotal();
    }

    /**
     * Retorna a lista de itens do carrinho.
     */
    public List<ItemCarrinho> getCarrinho() {
        return supermercado.getCarrinho();
    }

    /**
     * Finaliza a compra: registra no banco e atualiza estoques.
     * @return "OK" ou mensagem de erro.
     */
    public String finalizarCompra() {
        if (supermercado.isCarrinhoVazio()) {
            return "O carrinho está vazio!";
        }

        Usuario usuario = supermercado.getUsuarioLogado();
        if (usuario == null) return "Nenhum usuário logado.";

        double total = supermercado.calcularTotal();
        boolean sucesso = produtoDAO.finalizarCompra(usuario.getId(), supermercado.getCarrinho(), total);

        if (sucesso) {
            supermercado.limparCarrinho();
            return "OK";
        }
        return "Erro ao finalizar a compra. Tente novamente.";
    }

    /**
     * Gera o texto da nota fiscal.
     */
    public String gerarNotaFiscal(List<ItemCarrinho> itens, double total, Usuario usuario) {
        StringBuilder nota = new StringBuilder();
        nota.append("========================================\n");
        nota.append("         SUPERMERCADO MVC               \n");
        nota.append("   CNPJ: 00.000.000/0001-00             \n");
        nota.append("========================================\n\n");
        nota.append("CLIENTE: ").append(usuario.getNome()).append("\n");
        nota.append("CPF:     ").append(usuario.getCpf()).append("\n");
        nota.append("----------------------------------------\n");
        nota.append(String.format("%-25s %4s %10s\n", "PRODUTO", "QTD", "SUBTOTAL"));
        nota.append("----------------------------------------\n");

        for (ItemCarrinho item : itens) {
            nota.append(String.format("%-25s %4d R$%8.2f\n",
                truncar(item.getProduto().getNome(), 25),
                item.getQuantidade(),
                item.getSubtotal()));
        }

        nota.append("----------------------------------------\n");
        nota.append(String.format("TOTAL A PAGAR:        R$ %10.2f\n", total));
        nota.append("========================================\n");
        nota.append("      Obrigado pela preferência!        \n");
        nota.append("========================================\n");

        return nota.toString();
    }

    private String truncar(String texto, int max) {
        return texto.length() > max ? texto.substring(0, max - 1) + "." : texto;
    }
}
