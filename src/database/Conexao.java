package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Classe responsável pela conexão com o banco de dados MySQL.
 * Utiliza o padrão Singleton para garantir uma única instância de conexão.
 */
public class Conexao {

    // =============================================
    // CONFIGURAÇÕES DE CONEXÃO
    // =============================================
    // createDatabaseIfNotExist evita falha quando o schema ainda não foi criado.
    private static final String URL      = "jdbc:mysql://localhost:3306/supermercado_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO  = lerConfig("DB_USER", "root");
    private static final String SENHA    = lerConfig("DB_PASSWORD", ""); // defina DB_PASSWORD se necessário
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
                instancia = conectarComFallback();
                inicializarSchema(instancia);
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

    private static Connection conectarComFallback() throws SQLException {
        SQLException ultimoErro = null;
        for (String senha : montarCandidatosSenha()) {
            try {
                return DriverManager.getConnection(URL, USUARIO, senha);
            } catch (SQLException e) {
                ultimoErro = e;
            }
        }
        throw ultimoErro;
    }

    private static List<String> montarCandidatosSenha() {
        List<String> senhas = new ArrayList<>();
        adicionarSeNaoExiste(senhas, SENHA);
        adicionarSeNaoExiste(senhas, "");
        adicionarSeNaoExiste(senhas, "admin");
        adicionarSeNaoExiste(senhas, "root");
        return senhas;
    }

    private static void adicionarSeNaoExiste(List<String> lista, String valor) {
        if (valor == null) return;
        String normalizado = valor.trim();
        if (!lista.contains(normalizado)) {
            lista.add(normalizado);
        }
    }

    /**
     * Garante que as tabelas essenciais existam e que o schema esteja compatível
     * com a versão atual da aplicação.
     */
    private static void inicializarSchema(Connection conexao) throws SQLException {
        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "cpf VARCHAR(14) NOT NULL UNIQUE, " +
                "administrador BOOLEAN NOT NULL DEFAULT FALSE, " +
                "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS produtos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "descricao VARCHAR(255), " +
                "preco DECIMAL(10,2) NOT NULL, " +
                "quantidade_estoque INT NOT NULL DEFAULT 0, " +
                "categoria VARCHAR(50), " +
                "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS compras (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "usuario_id INT NOT NULL, " +
                "total DECIMAL(10,2) NOT NULL, " +
                "data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS itens_compra (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "compra_id INT NOT NULL, " +
                "produto_id INT NOT NULL, " +
                "quantidade INT NOT NULL, " +
                "preco_unitario DECIMAL(10,2) NOT NULL, " +
                "subtotal DECIMAL(10,2) NOT NULL, " +
                "FOREIGN KEY (compra_id) REFERENCES compras(id), " +
                "FOREIGN KEY (produto_id) REFERENCES produtos(id)" +
                ")");

            if (!colunaExiste(conexao, "usuarios", "administrador")) {
                stmt.executeUpdate("ALTER TABLE usuarios ADD COLUMN administrador BOOLEAN NOT NULL DEFAULT FALSE");
            }
        }
    }

    private static boolean colunaExiste(Connection conexao, String tabela, String coluna) throws SQLException {
        DatabaseMetaData metaData = conexao.getMetaData();
        try (ResultSet rs = metaData.getColumns(conexao.getCatalog(), null, tabela, coluna)) {
            return rs.next();
        }
    }

    private static String lerConfig(String chave, String padrao) {
        String valor = System.getProperty(chave);
        if (valor == null || valor.trim().isEmpty()) {
            valor = System.getenv(chave);
        }
        return (valor == null || valor.trim().isEmpty()) ? padrao : valor.trim();
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
