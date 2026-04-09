# 🛒 SuperMercado MVC - Java + MySQL

Sistema de gerenciamento de supermercado desenvolvido em **Java Swing** com padrão **MVC** e banco de dados **MySQL**.

---

## 📁 Estrutura do Projeto (MVC)

```
SuperMercadoMVC/
├── src/
│   ├── Main.java                        ← Ponto de entrada
│   ├── database/
│   │   └── Conexao.java                 ← Gerenciamento da conexão MySQL
│   ├── model/                           ← Camada MODEL
│   │   ├── Usuario.java
│   │   ├── UsuarioDAO.java
│   │   ├── Produto.java
│   │   ├── ProdutoDAO.java
│   │   ├── ItemCarrinho.java
│   │   └── Supermercado.java
│   ├── view/                            ← Camada VIEW
│   │   ├── TelaLogin.java
│   │   ├── TelaCadastroUsuario.java
│   │   ├── TelaProdutos.java
│   │   └── TelaCompra.java
│   └── controller/                      ← Camada CONTROLLER
│       ├── UsuarioController.java
│       ├── ProdutoController.java
│       └── CompraController.java
├── lib/
│   └── mysql-connector-j-X.X.X.jar     ← (você deve adicionar)
└── database.sql                         ← Script de criação do banco
```

---

## ⚙️ Configuração Passo a Passo

### 1. Banco de Dados (MySQL Workbench)

1. Abra o **MySQL Workbench** e conecte-se ao seu servidor local
2. Abra o arquivo `database.sql` (File > Open SQL Script)
3. Execute o script completo (**Ctrl+Shift+Enter** ou botão ▶️ com raio)
4. O banco `supermercado_db` será criado com as tabelas e dados iniciais

### 2. Driver MySQL (Connector/J)

1. Baixe o **MySQL Connector/J** em: https://dev.mysql.com/downloads/connector/j/
2. Escolha "Platform Independent" → baixe o `.zip`
3. Extraia e copie o arquivo `mysql-connector-j-X.X.X.jar` para a pasta `lib/` do projeto

### 3. Importar no Eclipse

1. **File > Import > General > Existing Projects into Workspace**
2. Selecione a pasta `SuperMercadoMVC`
3. Clique em **Finish**

### 4. Adicionar o JAR ao Build Path

1. Clique com botão direito no projeto → **Build Path > Configure Build Path**
2. Aba **Libraries** → **Add External JARs...**
3. Selecione o arquivo `mysql-connector-j-X.X.X.jar` da pasta `lib/`
4. Clique **Apply and Close**

### 5. Configurar a Senha do MySQL

Abra `src/database/Conexao.java` e altere a linha:

```java
private static final String SENHA = "root"; // ← coloque sua senha aqui
```

### 6. Configurar os Source Folders no Eclipse

1. Clique com botão direito no projeto → **Build Path > Configure Build Path**
2. Aba **Source** → **Add Folder...**
3. Marque a pasta `src` como source folder
4. Clique **Apply and Close**

### 7. Executar

- Clique com botão direito em `Main.java` → **Run As > Java Application**

---

## 👤 Usuário Padrão (criado pelo script SQL)

| Campo | Valor |
|-------|-------|
| Nome  | Administrador |
| CPF   | 000.000.000-00 |
| Tipo  | Administrador |

---

## 🖥️ Telas do Sistema

| Tela | Acesso | Funcionalidades |
|------|--------|-----------------|
| **Login** | Todos | Autenticação por Nome + CPF |
| **Cadastro de Usuário** | Todos | Criar conta (Admin ou Cliente) |
| **Gerenciar Produtos** | Administrador | CRUD completo de produtos |
| **Compras** | Cliente | Carrinho, compra, nota fiscal |

---

## 🔄 Fluxo do Sistema

```
Tela Login
    ├── [Cadastrar-se] ──→ Tela Cadastro de Usuário ──→ Tela Login
    ├── [Login Administrador] ──→ Tela de Produtos
    │       └── [Sair] ──→ Tela Login
    └── [Login Cliente] ──→ Tela de Compras
            └── [Sair] ──→ Tela Login
```

---

## 🗄️ Banco de Dados

```sql
supermercado_db
├── usuarios       (id, nome, cpf UNIQUE, administrador, data_cadastro)
├── produtos       (id, nome, descricao, preco, quantidade_estoque, categoria)
├── compras        (id, usuario_id, total, data_compra)
└── itens_compra   (id, compra_id, produto_id, quantidade, preco_unitario, subtotal)
```

---

## 📋 Regras de Negócio

- ✅ **Administrador**: pode cadastrar, editar, remover e visualizar produtos
- ✅ **Cliente**: pode visualizar produtos, montar carrinho e finalizar compra
- ✅ **Estoque**: atualizado automaticamente ao finalizar compra (via transação SQL)
- ✅ **Nota Fiscal**: exibe nome, CPF, produtos comprados e total
- ✅ **Carrinho**: adicionar/remover itens, ver subtotal e total em tempo real
- ✅ **Deslogar**: botão "Sair" em todas as telas pós-login

---

## 🛠️ Tecnologias

- Java SE (Swing para interface gráfica)
- MySQL 8.x
- MySQL Connector/J (JDBC)
- Padrão de projeto: MVC + DAO
