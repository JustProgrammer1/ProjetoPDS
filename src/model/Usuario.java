package model;

/**
 * Classe Model - Representa um Usuário do sistema.
 * Contém getters e setters para todos os atributos.
 */
public class Usuario {

    private int id;
    private String nome;
    private String cpf;
    private boolean administrador;

    // Construtor vazio
    public Usuario() {}

    // Construtor completo
    public Usuario(int id, String nome, String cpf, boolean administrador) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.administrador = administrador;
    }

    // Construtor sem ID (para inserção)
    public Usuario(String nome, String cpf, boolean administrador) {
        this.nome = nome;
        this.cpf = cpf;
        this.administrador = administrador;
    }

    // =============================================
    // GETTERS E SETTERS
    // =============================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    @Override
    public String toString() {
        return nome + " (" + (administrador ? "Administrador" : "Cliente") + ")";
    }
}
