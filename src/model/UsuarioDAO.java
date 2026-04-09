package model;

import database.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO (Data Access Object) para Usuário.
 * Responsável por todas as operações de banco de dados relacionadas a usuários.
 */
public class UsuarioDAO {

    /**
     * Insere um novo usuário no banco de dados.
     */
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, cpf, administrador) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setBoolean(3, usuario.isAdministrador());
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // CPF duplicado
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca um usuário pelo nome e CPF (login).
     */
    public Usuario buscarPorNomeECpf(String nome, String cpf) {
        String sql = "SELECT * FROM usuarios WHERE nome = ? AND cpf = ?";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verifica se um CPF já está cadastrado.
     */
    public boolean cpfExiste(String cpf) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE cpf = ?";
        try (PreparedStatement stmt = Conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lista todos os usuários cadastrados.
     */
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
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
     * Mapeia um ResultSet para um objeto Usuario.
     */
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("cpf"),
            rs.getBoolean("administrador")
        );
    }
}
