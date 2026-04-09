package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Model - Representa o Supermercado.
 * Gerencia o carrinho de compras e o usuário logado no momento.
 */
public class Supermercado {

    private String nome;
    private String cnpj;
    private String endereco;
    private List<ItemCarrinho> carrinho;
    private Usuario usuarioLogado;

    public Supermercado() {
        this.nome      = "SuperMercado MVC";
        this.cnpj      = "00.000.000/0001-00";
        this.endereco  = "Rua Principal, 123 - Centro";
        this.carrinho  = new ArrayList<>();
    }

    // =============================================
    // OPERAÇÕES DO CARRINHO
    // =============================================

    /**
     * Adiciona um produto ao carrinho.
     * Se o produto já existir, incrementa a quantidade.
     */
    public boolean adicionarAoCarrinho(Produto produto, int quantidade) {
        if (quantidade <= 0) return false;
        if (quantidade > produto.getQuantidadeEstoque()) return false;

        for (ItemCarrinho item : carrinho) {
            if (item.getProduto().getId() == produto.getId()) {
                int novaQtd = item.getQuantidade() + quantidade;
                if (novaQtd > produto.getQuantidadeEstoque()) return false;
                item.setQuantidade(novaQtd);
                return true;
            }
        }
        carrinho.add(new ItemCarrinho(produto, quantidade));
        return true;
    }

    /**
     * Remove um produto do carrinho pelo ID do produto.
     */
    public boolean removerDoCarrinho(int produtoId) {
        return carrinho.removeIf(item -> item.getProduto().getId() == produtoId);
    }

    /**
     * Atualiza a quantidade de um item no carrinho.
     */
    public boolean atualizarQuantidade(int produtoId, int novaQuantidade) {
        for (ItemCarrinho item : carrinho) {
            if (item.getProduto().getId() == produtoId) {
                if (novaQuantidade <= 0) {
                    return removerDoCarrinho(produtoId);
                }
                if (novaQuantidade > item.getProduto().getQuantidadeEstoque()) return false;
                item.setQuantidade(novaQuantidade);
                return true;
            }
        }
        return false;
    }

    /**
     * Limpa todos os itens do carrinho.
     */
    public void limparCarrinho() {
        carrinho.clear();
    }

    /**
     * Calcula o total a pagar com os itens do carrinho.
     */
    public double calcularTotal() {
        double total = 0;
        for (ItemCarrinho item : carrinho) {
            total += item.getSubtotal();
        }
        return total;
    }

    /**
     * Retorna true se o carrinho estiver vazio.
     */
    public boolean isCarrinhoVazio() {
        return carrinho.isEmpty();
    }

    // =============================================
    // GETTERS E SETTERS
    // =============================================

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<ItemCarrinho> getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(List<ItemCarrinho> carrinho) {
        this.carrinho = carrinho;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }
}
