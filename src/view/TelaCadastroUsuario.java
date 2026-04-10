package view;

import controller.UsuarioController;
import model.Supermercado;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * View - Tela de Cadastro de Usuário.
 */
public class TelaCadastroUsuario extends JFrame {

    private JTextField txtNome;
    private JTextField txtCpf;
    private JCheckBox chkAdministrador;
    private JButton btnSalvar;
    private JButton btnVoltar;
    private UsuarioController controller;
    private Supermercado supermercado;

    public TelaCadastroUsuario(Supermercado supermercado) {
        this.supermercado = supermercado;
        this.controller = new UsuarioController();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("SuperMercado MVC - Cadastro de Usuário");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 430);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(new Color(21, 101, 192));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(21, 101, 192));
        header.setBorder(new EmptyBorder(25, 20, 15, 20));

        JLabel lblTitulo = new JLabel("👤 Cadastro de Usuário");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);

        // Formulário
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(Color.WHITE);
        painelForm.setBorder(new EmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridwidth = 1;

        // Nome
        JLabel lblNome = new JLabel("Nome Completo:");
        lblNome.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        painelForm.add(lblNome, gbc);

        txtNome = criarCampoTexto();
        gbc.gridx = 0; gbc.gridy = 1;
        painelForm.add(txtNome, gbc);

        // CPF
        JLabel lblCpf = new JLabel("CPF (000.000.000-00):");
        lblCpf.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        painelForm.add(lblCpf, gbc);

        txtCpf = criarCampoTexto();
        txtCpf.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                aplicarMascaraCpf();
            }
        });
        gbc.gridx = 0; gbc.gridy = 3;
        painelForm.add(txtCpf, gbc);

        // Tipo de usuário
        JLabel lblTipo = new JLabel("Tipo de Conta:");
        lblTipo.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 4;
        painelForm.add(lblTipo, gbc);

        JPanel painelCheck = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelCheck.setBackground(Color.WHITE);

        chkAdministrador = new JCheckBox("Cadastrar como Administrador");
        chkAdministrador.setFont(new Font("Arial", Font.PLAIN, 13));
        chkAdministrador.setBackground(Color.WHITE);
        painelCheck.add(chkAdministrador);

        gbc.gridx = 0; gbc.gridy = 5;
        painelForm.add(painelCheck, gbc);

        // Info
        JLabel lblInfo = new JLabel("<html><small>* Administradores gerenciam produtos.<br>* Clientes realizam compras.</small></html>");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 6;
        painelForm.add(lblInfo, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 10, 0));
        painelBotoes.setBackground(Color.WHITE);
        painelBotoes.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnSalvar = new JButton("Salvar Cadastro");
        estilizarBotao(btnSalvar, new Color(27, 94, 32));
        btnSalvar.addActionListener(e -> salvarUsuario());

        btnVoltar = new JButton("← Voltar");
        estilizarBotao(btnVoltar, new Color(117, 117, 117));
        btnVoltar.addActionListener(e -> voltarLogin());

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnSalvar);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.insets = new Insets(15, 5, 5, 5);
        painelForm.add(painelBotoes, gbc);

        painelPrincipal.add(header, BorderLayout.NORTH);
        painelPrincipal.add(painelForm, BorderLayout.CENTER);
        add(painelPrincipal);
        setVisible(true);
    }

    private void salvarUsuario() {
        String nome = txtNome.getText().trim();
        String cpf  = txtCpf.getText().trim();
        boolean adm = chkAdministrador.isSelected();

        String resultado = controller.cadastrar(nome, cpf, adm);

        if ("OK".equals(resultado)) {
            JOptionPane.showMessageDialog(this,
                "Usuário cadastrado com sucesso!\nFaça login para continuar.",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            voltarLogin();
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voltarLogin() {
        dispose();
        new TelaLogin(supermercado);
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(300, 35));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(5, 10, 5, 10)));
        return campo;
    }

    private void aplicarMascaraCpf() {
        String texto = txtCpf.getText().replaceAll("[^0-9]", "");
        if (texto.length() > 11) texto = texto.substring(0, 11);

        StringBuilder f = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            if (i == 3 || i == 6) f.append('.');
            if (i == 9) f.append('-');
            f.append(texto.charAt(i));
        }
        txtCpf.setText(f.toString());
        txtCpf.setCaretPosition(Math.min(f.length(), txtCpf.getText().length()));
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
