package controller;

import model.Produto;
import model.ProdutoDAO;
import java.util.List;

/**
 * Controller responsável pelas ações relacionadas a Produtos.
 * Faz a ponte entre a View e o Model.
 */
public class ProdutoController {

    private ProdutoDAO produtoDAO;

    public ProdutoController() {
        this.produtoDAO = new ProdutoDAO();
    }

    /**
     * Cadastra um novo produto.
     * @return "OK" ou mensagem de erro.
     */
    public String cadastrar(String nome, String descricao, String precoStr, String qtdStr, String categoria) {
        String erro = validarCampos(nome, precoStr, qtdStr);
        if (erro != null) return erro;

        double preco = Double.parseDouble(precoStr.replace(",", "."));
        int qtd = Integer.parseInt(qtdStr);

        Produto produto = new Produto(nome.trim(), descricao.trim(), preco, qtd, categoria.trim());
        boolean sucesso = produtoDAO.inserir(produto);
        return sucesso ? "OK" : "Erro ao cadastrar o produto.";
    }

    /**
     * Atualiza um produto existente.
     * @return "OK" ou mensagem de erro.
     */
    public String atualizar(int id, String nome, String descricao, String precoStr, String qtdStr, String categoria) {
        String erro = validarCampos(nome, precoStr, qtdStr);
        if (erro != null) return erro;

        double preco = Double.parseDouble(precoStr.replace(",", "."));
        int qtd = Integer.parseInt(qtdStr);

        Produto produto = new Produto(id, nome.trim(), descricao.trim(), preco, qtd, categoria.trim());
        boolean sucesso = produtoDAO.atualizar(produto);
        return sucesso ? "OK" : "Erro ao atualizar o produto.";
    }

    /**
     * Remove um produto pelo ID.
     * @return "OK" ou mensagem de erro.
     */
    public String remover(int id) {
        boolean sucesso = produtoDAO.remover(id);
        return sucesso ? "OK" : "Erro ao remover o produto.";
    }

    /**
     * Lista todos os produtos.
     */
    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    /**
     * Lista apenas produtos com estoque disponível.
     */
    public List<Produto> listarDisponiveis() {
        return produtoDAO.listarDisponiveis();
    }

    /**
     * Busca produto por ID.
     */
    public Produto buscarPorId(int id) {
        return produtoDAO.buscarPorId(id);
    }

    /**
     * Valida campos comuns do produto.
     */
    private String validarCampos(String nome, String precoStr, String qtdStr) {
        if (nome == null || nome.trim().isEmpty()) {
            return "O nome do produto não pode estar vazio.";
        }
        try {
            double preco = Double.parseDouble(precoStr.replace(",", "."));
            if (preco <= 0) return "O preço deve ser maior que zero.";
        } catch (NumberFormatException e) {
            return "Preço inválido. Use formato numérico (ex: 9.99)";
        }
        try {
            int qtd = Integer.parseInt(qtdStr);
            if (qtd < 0) return "Quantidade não pode ser negativa.";
        } catch (NumberFormatException e) {
            return "Quantidade inválida. Digite um número inteiro.";
        }
        return null;
    }
}
