package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Classe responsável pela conexão com o banco de dados MySQL.
 * Utiliza o padrão Singleton para garantir uma única instância de conexão.
 */
public class Conexao {

    // =============================================
    // CONFIGURAÇÕES DE CONEXÃO - ALTERE AQUI
    // =============================================
    // createDatabaseIfNotExist evita falha quando o schema ainda não foi criado.
    private static final String URL      = "jdbc:mysql://localhost:3306/supermercado_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO  = "root";
    private static final String SENHA    = "admin"; // <-- Altere para sua senha do MySQL
    // =============================================

    private static Connection instancia = null;

    private Conexao() {}

    /**
     * Retorna a conexão ativa com o banco de dados.
     * Cria uma nova conexão caso ainda não exista ou esteja fechada.
     */
    public static Connection getConexao() {
        try {
            if (instancia == null || instancia.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instancia = DriverManager.getConnection(URL, USUARIO, SENHA);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Driver MySQL não encontrado!\nAdicione o mysql-connector-j ao Build Path.",
                "Erro de Driver", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao conectar ao banco de dados!\n" +
                "Verifique se o MySQL está rodando e as credenciais estão corretas.\n\n" +
                "Detalhes: " + e.getMessage(),
                "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
        return instancia;
    }

    /**
     * Fecha a conexão com o banco de dados.
     */
    public static void fecharConexao() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
