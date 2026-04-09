package view;

import controller.CompraController;
import controller.ProdutoController;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * View - Tela de Compra (Cliente).
 */
public class TelaCompra extends JFrame {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloProdutos;
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloCarrinho;
    private JLabel lblTotal;
    private JButton btnAdicionar, btnRemoverCarrinho, btnFinalizar, btnNota, btnSair;
    private JSpinner spnQuantidade;
    private CompraController compraController;
    private ProdutoController produtoController;
    private Supermercado supermercado;

    public TelaCompra(Supermercado supermercado) {
        this.supermercado = supermercado;
        this.produtoController = new ProdutoController();
        this.compraController = new CompraController(supermercado);
        inicializarComponentes();
        carregarProdutos();
    }

    private void inicializarComponentes() {
        setTitle("SuperMercado MVC - Compras [" + supermercado.getUsuarioLogado().getNome() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(8, 8));
        painelPrincipal.setBackground(new Color(245, 245, 245));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(27, 94, 32));
        header.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblTitulo = new JLabel("🛒 Realizar Compra");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo, BorderLayout.WEST);

        JPanel painelHeaderDir = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelHeaderDir.setBackground(new Color(27, 94, 32));

        JLabel lblUser = new JLabel("👤 " + supermercado.getUsuarioLogado().getNome());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));

        btnSair = criarBotao("🚪 Sair", new Color(183, 28, 28));
        btnSair.addActionListener(e -> deslogar());

        painelHeaderDir.add(lblUser);
        painelHeaderDir.add(Box.createHorizontalStrut(10));
        painelHeaderDir.add(btnSair);
        header.add(painelHeaderDir, BorderLayout.EAST);

        // === PAINEL ESQUERDO - Produtos disponíveis ===
        JPanel painelEsq = new JPanel(new BorderLayout(0, 5));
        painelEsq.setBackground(Color.WHITE);
        painelEsq.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 10, 10, 10)));

        JLabel lblProdutos = new JLabel("📋 Produtos Disponíveis");
        lblProdutos.setFont(new Font("Arial", Font.BOLD, 14));
        lblProdutos.setForeground(new Color(27, 94, 32));
        painelEsq.add(lblProdutos, BorderLayout.NORTH);

        String[] colsProd = {"ID", "Nome", "Categoria", "Preço (R$)", "Estoque"};
        modeloProdutos = new DefaultTableModel(colsProd, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaProdutos = new JTable(modeloProdutos);
        tabelaProdutos.setFont(new Font("Arial", Font.PLAIN, 13));
        tabelaProdutos.setRowHeight(24);
        tabelaProdutos.setSelectionBackground(new Color(200, 230, 201));
        tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(40);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(85);
        tabelaProdutos.getColumnModel().getColumn(4).setPreferredWidth(65);

        // Double click para ver detalhes
        tabelaProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) verDetalhes();
            }
        });

        JScrollPane scrollProd = new JScrollPane(tabelaProdutos);
        painelEsq.add(scrollProd, BorderLayout.CENTER);

        // Botão adicionar
        JPanel painelAdicionar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAdicionar.setBackground(Color.WHITE);

        JLabel lblQtd = new JLabel("Qtd:");
        lblQtd.setFont(new Font("Arial", Font.BOLD, 13));
        spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnQuantidade.setPreferredSize(new Dimension(60, 28));

        btnAdicionar = criarBotao("➕ Adicionar ao Carrinho", new Color(27, 94, 32));
        btnAdicionar.addActionListener(e -> adicionarAoCarrinho());

        JLabel lblDica = new JLabel("<html><small><i>Clique duplo para ver detalhes</i></small></html>");
        lblDica.setForeground(Color.GRAY);

        painelAdicionar.add(lblQtd);
        painelAdicionar.add(spnQuantidade);
        painelAdicionar.add(btnAdicionar);
        painelAdicionar.add(lblDica);
        painelEsq.add(painelAdicionar, BorderLayout.SOUTH);

        // === PAINEL DIREITO - Carrinho ===
        JPanel painelDir = new JPanel(new BorderLayout(0, 5));
        painelDir.setBackground(Color.WHITE);
        painelDir.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 10, 10, 10)));
        painelDir.setPreferredSize(new Dimension(320, 0));

        JLabel lblCarrinho = new JLabel("🛒 Meu Carrinho");
        lblCarrinho.setFont(new Font("Arial", Font.BOLD, 14));
        lblCarrinho.setForeground(new Color(27, 94, 32));
        painelDir.add(lblCarrinho, BorderLayout.NORTH);

        String[] colsCarrinho = {"Produto", "Qtd", "Subtotal (R$)"};
        modeloCarrinho = new DefaultTableModel(colsCarrinho, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaCarrinho = new JTable(modeloCarrinho);
        tabelaCarrinho.setFont(new Font("Arial", Font.PLAIN, 13));
        tabelaCarrinho.setRowHeight(24);
        tabelaCarrinho.setSelectionBackground(new Color(255, 204, 128));
        tabelaCarrinho.getColumnModel().getColumn(1).setMaxWidth(45);
        tabelaCarrinho.getColumnModel().getColumn(2).setPreferredWidth(90);

        JScrollPane scrollCart = new JScrollPane(tabelaCarrinho);
        painelDir.add(scrollCart, BorderLayout.CENTER);

        // Rodapé do carrinho
        JPanel painelRodape = new JPanel(new GridLayout(4, 1, 0, 5));
        painelRodape.setBackground(Color.WHITE);
        painelRodape.setBorder(new EmptyBorder(8, 0, 0, 0));

        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(27, 94, 32));
        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);

        btnRemoverCarrinho = criarBotao("🗑️ Remover Item", new Color(183, 28, 28));
        btnRemoverCarrinho.addActionListener(e -> removerDoCarrinho());

        btnFinalizar = criarBotao("✅ Finalizar Compra", new Color(21, 101, 192));
        btnFinalizar.addActionListener(e -> finalizarCompra());

        btnNota = criarBotao("🧾 Emitir Nota Fiscal", new Color(100, 60, 180));
        btnNota.setEnabled(false);
        btnNota.addActionListener(e -> emitirNota());

        painelRodape.add(lblTotal);
        painelRodape.add(btnRemoverCarrinho);
        painelRodape.add(btnFinalizar);
        painelRodape.add(btnNota);
        painelDir.add(painelRodape, BorderLayout.SOUTH);

        // Montar split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelEsq, painelDir);
        split.setDividerLocation(660);
        split.setDividerSize(5);

        painelPrincipal.add(header, BorderLayout.NORTH);
        painelPrincipal.add(split, BorderLayout.CENTER);
        add(painelPrincipal);
        setVisible(true);
    }

    private void carregarProdutos() {
        modeloProdutos.setRowCount(0);
        List<Produto> produtos = produtoController.listarDisponiveis();
        for (Produto p : produtos) {
            modeloProdutos.addRow(new Object[]{
                p.getId(), p.getNome(), p.getCategoria(),
                String.format("%.2f", p.getPreco()), p.getQuantidadeEstoque()
            });
        }
    }

    private void atualizarCarrinho() {
        modeloCarrinho.setRowCount(0);
        List<ItemCarrinho> itens = compraController.getCarrinho();
        for (ItemCarrinho item : itens) {
            modeloCarrinho.addRow(new Object[]{
                item.getProduto().getNome(),
                item.getQuantidade(),
                String.format("%.2f", item.getSubtotal())
            });
        }
        lblTotal.setText(String.format("TOTAL: R$ %.2f", compraController.calcularTotal()));
    }

    private void adicionarAoCarrinho() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para adicionar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloProdutos.getValueAt(linha, 0);
        Produto produto = produtoController.buscarPorId(id);
        int qtd = (int) spnQuantidade.getValue();

        String resultado = compraController.adicionarAoCarrinho(produto, qtd);

        if ("OK".equals(resultado)) {
            atualizarCarrinho();
            JOptionPane.showMessageDialog(this,
                produto.getNome() + " adicionado ao carrinho!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerDoCarrinho() {
        int linha = tabelaCarrinho.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item no carrinho.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeProd = (String) modeloCarrinho.getValueAt(linha, 0);
        // Encontrar ID pelo nome
        for (ItemCarrinho item : compraController.getCarrinho()) {
            if (item.getProduto().getNome().equals(nomeProd)) {
                compraController.removerDoCarrinho(item.getProduto().getId());
                break;
            }
        }
        atualizarCarrinho();
        JOptionPane.showMessageDialog(this, "Item removido do carrinho.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finalizarCompra() {
        if (compraController.getCarrinho().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = compraController.calcularTotal();
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Confirmar compra?\nTotal: R$ %.2f", total),
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Salvar itens para nota fiscal antes de limpar
            List<ItemCarrinho> itensCopia = new java.util.ArrayList<>(compraController.getCarrinho());
            double totalCopia = total;

            String resultado = compraController.finalizarCompra();

            if ("OK".equals(resultado)) {
                JOptionPane.showMessageDialog(this,
                    "Compra finalizada com sucesso!\nEstoque atualizado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Habilitar botão de nota fiscal com os dados da compra
                btnNota.setEnabled(true);
                btnNota.putClientProperty("itens", itensCopia);
                btnNota.putClientProperty("total", totalCopia);

                atualizarCarrinho();
                carregarProdutos();
            } else {
                JOptionPane.showMessageDialog(this, resultado, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void emitirNota() {
        List<ItemCarrinho> itens = (List<ItemCarrinho>) btnNota.getClientProperty("itens");
        Double total = (Double) btnNota.getClientProperty("total");

        if (itens == null || itens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma compra para emitir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nota = compraController.gerarNotaFiscal(itens, total, supermercado.getUsuarioLogado());

        JTextArea area = new JTextArea(nota);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        area.setBackground(new Color(250, 250, 250));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 380));

        JOptionPane.showMessageDialog(this, scroll, "🧾 Nota Fiscal", JOptionPane.PLAIN_MESSAGE);
        btnNota.setEnabled(false);
    }

    private void verDetalhes() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha < 0) return;

        int id = (int) modeloProdutos.getValueAt(linha, 0);
        Produto p = produtoController.buscarPorId(id);
        if (p == null) return;

        String info = String.format(
            "<html><b>📦 %s</b><br><br>" +
            "<b>Categoria:</b> %s<br>" +
            "<b>Preço:</b> R$ %.2f<br>" +
            "<b>Estoque:</b> %d unidades<br><br>" +
            "<b>Descrição:</b><br>%s</html>",
            p.getNome(), p.getCategoria(), p.getPreco(),
            p.getQuantidadeEstoque(),
            (p.getDescricao() != null && !p.getDescricao().isEmpty()) ? p.getDescricao() : "Sem descrição.");

        JOptionPane.showMessageDialog(this, info, "Detalhes do Produto", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deslogar() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja deslogar e voltar ao login?", "Sair", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            supermercado.setUsuarioLogado(null);
            supermercado.limparCarrinho();
            dispose();
            new TelaLogin(supermercado);
        }
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
