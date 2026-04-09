import model.Supermercado;
import view.TelaLogin;

import javax.swing.*;

/**
 * Classe principal - Ponto de entrada da aplicação.
 * Inicializa o modelo Supermercado e abre a tela de Login.
 */
public class Main {

    public static void main(String[] args) {
        // Usar o tema visual nativo do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantém o look padrão
        }

        // Instância única do supermercado compartilhada entre telas
        Supermercado supermercado = new Supermercado();

        // Abrir tela de login na thread de UI
        SwingUtilities.invokeLater(() -> new TelaLogin(supermercado));
    }
}
