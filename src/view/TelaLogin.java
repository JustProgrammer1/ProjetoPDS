package view;

import controller.UsuarioController;
import model.Supermercado;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * View - Tela de Login do sistema.
 */
public class TelaLogin extends JFrame {

    private JTextField txtNome;
    private JTextField txtCpf;
    private JButton btnEntrar;
    private JButton btnCadastrar;
    private UsuarioController controller;
    private Supermercado supermercado;

    public TelaLogin(Supermercado supermercado) {
        this.supermercado = supermercado;
        this.controller = new UsuarioController();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("SuperMercado MVC - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Painel principal com gradiente
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(new Color(27, 94, 32));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(27, 94, 32));
        header.setBorder(new EmptyBorder(30, 20, 10, 20));

        JLabel lblTitulo = new JLabel("🛒 SuperMercado MVC");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Sistema de Gerenciamento");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(200, 230, 201));

        JPanel pnlTitulos = new JPanel(new GridLayout(2, 1));
        pnlTitulos.setBackground(new Color(27, 94, 32));
        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(lblSubtitulo);
        header.add(pnlTitulos);

        // Painel de formulário
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(Color.WHITE);
        painelForm.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Campo Nome
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        painelForm.add(lblNome, gbc);

        txtNome = new JTextField(20);
        txtNome.setFont(new Font("Arial", Font.PLAIN, 13));
        txtNome.setPreferredSize(new Dimension(250, 35));
        txtNome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 0; gbc.gridy = 1;
        painelForm.add(txtNome, gbc);

        // Campo CPF
        JLabel lblCpf = new JLabel("CPF (000.000.000-00):");
        lblCpf.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        painelForm.add(lblCpf, gbc);

        txtCpf = new JTextField(20);
        txtCpf.setFont(new Font("Arial", Font.PLAIN, 13));
        txtCpf.setPreferredSize(new Dimension(250, 35));
        txtCpf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 10, 5, 10)));
        // Máscara simples de CPF ao digitar
        txtCpf.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                aplicarMascaraCpf();
            }
        });
        gbc.gridx = 0; gbc.gridy = 3;
        painelForm.add(txtCpf, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 10, 0));
        painelBotoes.setBackground(Color.WHITE);
        painelBotoes.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnEntrar = new JButton("Entrar");
        estilizarBotao(btnEntrar, new Color(27, 94, 32));
        btnEntrar.addActionListener(this::acaoLogin);

        btnCadastrar = new JButton("Cadastrar-se");
        estilizarBotao(btnCadastrar, new Color(66, 165, 245));
        btnCadastrar.addActionListener(e -> abrirCadastro());

        painelBotoes.add(btnEntrar);
        painelBotoes.add(btnCadastrar);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        painelForm.add(painelBotoes, gbc);

        painelPrincipal.add(header, BorderLayout.NORTH);
        painelPrincipal.add(painelForm, BorderLayout.CENTER);

        add(painelPrincipal);
        setVisible(true);
    }

    private void acaoLogin(ActionEvent e) {
        String nome = txtNome.getText().trim();
        String cpf  = txtCpf.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = controller.login(nome, cpf);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado!\nVerifique nome e CPF.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        } else {
            supermercado.setUsuarioLogado(usuario);
            dispose();
            if (usuario.isAdministrador()) {
                new TelaProdutos(supermercado);
            } else {
                new TelaCompra(supermercado);
            }
        }
    }

    private void abrirCadastro() {
        dispose();
        new TelaCadastroUsuario(supermercado);
    }

    private void aplicarMascaraCpf() {
        String texto = txtCpf.getText().replaceAll("[^0-9]", "");
        if (texto.length() > 11) texto = texto.substring(0, 11);

        StringBuilder formatado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            if (i == 3 || i == 6) formatado.append('.');
            if (i == 9) formatado.append('-');
            formatado.append(texto.charAt(i));
        }

        int caret = formatado.length();
        txtCpf.setText(formatado.toString());
        txtCpf.setCaretPosition(Math.min(caret, txtCpf.getText().length()));
    }

    private void estilizarBotao(JButton botao, Color corFundo) {
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 13));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(false);
    }
}
