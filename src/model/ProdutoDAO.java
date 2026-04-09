package model;

import database.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO (Data Access Object) para Produto.
 * Responsável por todas as operações de banco de dados relacionadas a produtos.
 */
public class ProdutoDAO {

    /**
     * Insere um novo produto no banco de dados.
     */
    public boolean inserir(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade_estoque, categoria) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setString(5, produto.getCategoria());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Atualiza os dados de um produto existente.
     */
    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome=?, descricao=?, preco=?, quantidade_estoque=?, categoria=? WHERE id=?";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setString(5, produto.getCategoria());
            stmt.setInt(6, produto.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove um produto pelo ID.
     */
    public boolean remover(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca um produto pelo ID.
     */
    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os produtos cadastrados.
     */
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";
        try (Statement stmt = Conexao.getConexao().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Lista produtos com estoque disponível (quantidade > 0).
     */
    public List<Produto> listarDisponiveis() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE quantidade_estoque > 0 ORDER BY nome";
        try (Statement stmt = Conexao.getConexao().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Atualiza apenas o estoque de um produto.
     */
    public boolean atualizarEstoque(int produtoId, int novaQuantidade) {
        String sql = "UPDATE produtos SET quantidade_estoque = ? WHERE id = ?";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, produtoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Salva uma compra completa e atualiza estoques (transação).
     */
    public boolean finalizarCompra(int usuarioId, List<ItemCarrinho> itens, double total) {
        Connection conn = Conexao.getConexao();
        if (conn == null) return false;
        try {
            conn.setAutoCommit(false);

            // Inserir cabeçalho da compra
            String sqlCompra = "INSERT INTO compras (usuario_id, total) VALUES (?, ?)";
            try (PreparedStatement stmtCompra = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
                stmtCompra.setInt(1, usuarioId);
                stmtCompra.setDouble(2, total);
                stmtCompra.executeUpdate();

                ResultSet keys = stmtCompra.getGeneratedKeys();
                int compraId = 0;
                if (keys.next()) compraId = keys.getInt(1);

                if (compraId <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return false;
                }

                // Inserir itens e atualizar estoque com proteção contra estoque negativo
                String sqlItem = "INSERT INTO itens_compra (compra_id, produto_id, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
                String sqlEstoque = "UPDATE produtos SET quantidade_estoque = quantidade_estoque - ? WHERE id = ? AND quantidade_estoque >= ?";

                try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
                     PreparedStatement stmtEst = conn.prepareStatement(sqlEstoque)) {
                    for (ItemCarrinho item : itens) {
                        stmtItem.setInt(1, compraId);
                        stmtItem.setInt(2, item.getProduto().getId());
                        stmtItem.setInt(3, item.getQuantidade());
                        stmtItem.setDouble(4, item.getProduto().getPreco());
                        stmtItem.setDouble(5, item.getSubtotal());
                        stmtItem.executeUpdate();

                        stmtEst.setInt(1, item.getQuantidade());
                        stmtEst.setInt(2, item.getProduto().getId());
                        stmtEst.setInt(3, item.getQuantidade());
                        int linhasAfetadas = stmtEst.executeUpdate();

                        if (linhasAfetadas == 0) {
                            conn.rollback();
                            conn.setAutoCommit(true);
                            return false;
                        }
                    }
                }
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Produto.
     */
    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("descricao"),
            rs.getDouble("preco"),
            rs.getInt("quantidade_estoque"),
            rs.getString("categoria")
        );
    }
}
