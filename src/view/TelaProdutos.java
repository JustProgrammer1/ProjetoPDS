package view;

import controller.ProdutoController;
import model.Produto;
import model.Supermercado;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * View - Tela de Cadastro e Gerenciamento de Produtos (Administrador).
 */
public class TelaProdutos extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtDescricao, txtPreco, txtQtd, txtCategoria;
    private JButton btnSalvar, btnNovo, btnEditar, btnRemover, btnSair;
    private JLabel lblTituloCampos;
    private ProdutoController controller;
    private Supermercado supermercado;
    private int idEditando = -1;

    public TelaProdutos(Supermercado supermercado) {
        this.supermercado = supermercado;
        this.controller = new ProdutoController();
        inicializarComponentes();
        carregarProdutos();
    }

    private void inicializarComponentes() {
        setTitle("SuperMercado MVC - Gerenciar Produtos [" + supermercado.getUsuarioLogado().getNome() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(245, 245, 245));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(27, 94, 32));
        header.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblTitulo = new JLabel("📦 Gerenciamento de Produtos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo, BorderLayout.WEST);

        JPanel painelHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelHeader.setBackground(new Color(27, 94, 32));

        JLabel lblUsuario = new JLabel("👤 " + supermercado.getUsuarioLogado().getNome() + " (Admin)");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));

        btnSair = criarBotao("🚪 Sair", new Color(183, 28, 28));
        btnSair.addActionListener(e -> deslogar());

        painelHeader.add(lblUsuario);
        painelHeader.add(Box.createHorizontalStrut(10));
        painelHeader.add(btnSair);
        header.add(painelHeader, BorderLayout.EAST);

        // --- Tabela ---
        String[] colunas = {"ID", "Nome", "Categoria", "Preço (R$)", "Estoque", "Descrição"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Arial", Font.PLAIN, 13));
        tabela.setRowHeight(25);
        tabela.setSelectionBackground(new Color(200, 230, 201));
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(70);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherCampos();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // --- Formulário lado direito ---
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(Color.WHITE);
        painelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)));
        painelForm.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 3, 5, 3);
        gbc.gridx = 0; gbc.weightx = 1.0;

        lblTituloCampos = new JLabel("NOVO PRODUTO");
        lblTituloCampos.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloCampos.setForeground(new Color(27, 94, 32));
        gbc.gridy = 0; painelForm.add(lblTituloCampos, gbc);

        gbc.gridy = 1; painelForm.add(criarLabel("Nome *:"), gbc);
        txtNome = criarCampo(); gbc.gridy = 2; painelForm.add(txtNome, gbc);

        gbc.gridy = 3; painelForm.add(criarLabel("Categoria:"), gbc);
        txtCategoria = criarCampo(); gbc.gridy = 4; painelForm.add(txtCategoria, gbc);

        gbc.gridy = 5; painelForm.add(criarLabel("Preço (R$) *:"), gbc);
        txtPreco = criarCampo(); gbc.gridy = 6; painelForm.add(txtPreco, gbc);

        gbc.gridy = 7; painelForm.add(criarLabel("Quantidade em Estoque *:"), gbc);
        txtQtd = criarCampo(); gbc.gridy = 8; painelForm.add(txtQtd, gbc);

        gbc.gridy = 9; painelForm.add(criarLabel("Descrição:"), gbc);
        txtDescricao = criarCampo(); gbc.gridy = 10; painelForm.add(txtDescricao, gbc);

        // Botões do formulário
        JPanel painelBotoes = new JPanel(new GridLayout(3, 1, 0, 8));
        painelBotoes.setBackground(Color.WHITE);
        painelBotoes.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnNovo   = criarBotao("➕ Novo Produto", new Color(21, 101, 192));
        btnSalvar = criarBotao("💾 Salvar", new Color(27, 94, 32));
        btnEditar = criarBotao("✏️ Editar Selecionado", new Color(230, 119, 0));
        btnRemover= criarBotao("🗑️ Remover Selecionado", new Color(183, 28, 28));

        btnNovo.addActionListener(e -> novoForm());
        btnSalvar.addActionListener(e -> salvar());
        btnEditar.addActionListener(e -> editarSelecionado());
        btnRemover.addActionListener(e -> removerSelecionado());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);

        gbc.gridy = 11; gbc.insets = new Insets(10, 3, 3, 3);
        painelForm.add(btnNovo, gbc);
        gbc.gridy = 12; gbc.insets = new Insets(3, 3, 3, 3);
        painelForm.add(painelBotoes, gbc);

        // Montar layout
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, painelForm);
        split.setDividerLocation(590);
        split.setDividerSize(5);

        painelPrincipal.add(header, BorderLayout.NORTH);
        painelPrincipal.add(split, BorderLayout.CENTER);
        add(painelPrincipal);
        setVisible(true);
    }

    private void carregarProdutos() {
        modeloTabela.setRowCount(0);
        List<Produto> produtos = controller.listarTodos();
        for (Produto p : produtos) {
            modeloTabela.addRow(new Object[]{
                p.getId(), p.getNome(), p.getCategoria(),
                String.format("%.2f", p.getPreco()), p.getQuantidadeEstoque(), p.getDescricao()
            });
        }
    }

    private void salvar() {
        String resultado;
        if (idEditando == -1) {
            resultado = controller.cadastrar(
                txtNome.getText(), txtDescricao.getText(),
                txtPreco.getText(), txtQtd.getText(), txtCategoria.getText());
        } else {
            resultado = controller.atualizar(
                idEditando, txtNome.getText(), txtDescricao.getText(),
                txtPreco.getText(), txtQtd.getText(), txtCategoria.getText());
        }

        if ("OK".equals(resultado)) {
            JOptionPane.showMessageDialog(this,
                idEditando == -1 ? "Produto cadastrado com sucesso!" : "Produto atualizado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            novoForm();
            carregarProdutos();
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        preencherCampos();
        lblTituloCampos.setText("EDITAR PRODUTO");
    }

    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabela.getValueAt(linha, 0);
        String nome = (String) modeloTabela.getValueAt(linha, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja remover o produto:\n" + nome + " (ID: " + id + ")?",
            "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = controller.remover(id);
            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                novoForm();
                carregarProdutos();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        idEditando = (int) modeloTabela.getValueAt(linha, 0);
        txtNome.setText((String) modeloTabela.getValueAt(linha, 1));
        txtCategoria.setText((String) modeloTabela.getValueAt(linha, 2));
        txtPreco.setText(modeloTabela.getValueAt(linha, 3).toString());
        txtQtd.setText(modeloTabela.getValueAt(linha, 4).toString());
        Object desc = modeloTabela.getValueAt(linha, 5);
        txtDescricao.setText(desc != null ? desc.toString() : "");
        lblTituloCampos.setText("EDITAR PRODUTO");
    }

    private void novoForm() {
        idEditando = -1;
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtQtd.setText("");
        txtCategoria.setText("");
        lblTituloCampos.setText("NOVO PRODUTO");
        tabela.clearSelection();
    }

    private void deslogar() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja deslogar e voltar ao login?", "Sair", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            supermercado.setUsuarioLogado(null);
            dispose();
            new TelaLogin(supermercado);
        }
    }

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        return l;
    }

    private JTextField criarCampo() {
        JTextField t = new JTextField();
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setPreferredSize(new Dimension(0, 30));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(2, 6, 2, 6)));
        return t;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 32));
        return b;
    }
}
