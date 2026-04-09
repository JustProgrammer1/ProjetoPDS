
CREATE DATABASE IF NOT EXISTS supermercado_db;
USE supermercado_db;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    administrador BOOLEAN NOT NULL DEFAULT FALSE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Produtos
CREATE TABLE IF NOT EXISTS produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    preco DECIMAL(10,2) NOT NULL,
    quantidade_estoque INT NOT NULL DEFAULT 0,
    categoria VARCHAR(50),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Compras (cabeçalho)
CREATE TABLE IF NOT EXISTS compras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de Itens da Compra (detalhes)
CREATE TABLE IF NOT EXISTS itens_compra (
    id INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (compra_id) REFERENCES compras(id),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

-- ============================================
-- DADOS INICIAIS (opcional)
-- ============================================

-- Usuário administrador padrão
INSERT INTO usuarios (nome, cpf, administrador) VALUES
('Administrador', '000.000.000-00', TRUE);

-- Produtos de exemplo
INSERT INTO produtos (nome, descricao, preco, quantidade_estoque, categoria) VALUES
('Arroz Integral 1kg', 'Arroz integral tipo 1', 8.90, 50, 'Grãos'),
('Feijão Carioca 1kg', 'Feijão carioca tipo 1', 7.50, 40, 'Grãos'),
('Leite Integral 1L', 'Leite integral UHT', 4.99, 100, 'Laticínios'),
('Açúcar Refinado 1kg', 'Açúcar refinado especial', 4.50, 60, 'Mercearia'),
('Óleo de Soja 900ml', 'Óleo de soja refinado', 6.99, 45, 'Mercearia'),
('Macarrão Espaguete 500g', 'Macarrão espaguete grano duro', 3.99, 80, 'Massas'),
('Café Moído 500g', 'Café torrado e moído', 15.90, 35, 'Bebidas'),
('Sabão em Pó 1kg', 'Sabão em pó multiação', 12.50, 30, 'Limpeza');
