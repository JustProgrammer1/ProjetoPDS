package controller;

import model.Usuario;
import model.UsuarioDAO;

/**
 * Controller responsável pelas ações relacionadas a Usuários.
 * Faz a ponte entre a View e o Model.
 */
public class UsuarioController {

    private UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Realiza o login: busca usuário por nome e CPF.
     * @return Usuario encontrado, ou null se não existir.
     */
    public Usuario login(String nome, String cpf) {
        if (nome == null || nome.trim().isEmpty()) return null;
        if (cpf == null || cpf.trim().isEmpty()) return null;
        return usuarioDAO.buscarPorNomeECpf(nome.trim(), cpf.trim());
    }

    /**
     * Cadastra um novo usuário no sistema.
     * @return "OK" se sucesso, ou mensagem de erro.
     */
    public String cadastrar(String nome, String cpf, boolean administrador) {
        if (nome == null || nome.trim().isEmpty()) {
            return "O nome não pode estar vazio.";
        }
        if (cpf == null || cpf.trim().isEmpty()) {
            return "O CPF não pode estar vazio.";
        }
        if (!validarCpf(cpf.trim())) {
            return "CPF inválido. Use o formato: 000.000.000-00";
        }
        if (usuarioDAO.cpfExiste(cpf.trim())) {
            return "Já existe um usuário cadastrado com este CPF.";
        }

        Usuario usuario = new Usuario(nome.trim(), cpf.trim(), administrador);
        boolean sucesso = usuarioDAO.inserir(usuario);

        return sucesso ? "OK" : "Erro ao cadastrar o usuário. Tente novamente.";
    }

    /**
     * Valida o formato do CPF (000.000.000-00).
     */
    public boolean validarCpf(String cpf) {
        return cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }
}
