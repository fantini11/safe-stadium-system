package service;

import dao.UsuarioDAO;
import model.Usuario;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO dao = new UsuarioDAO();
    public boolean insert(Usuario u) { return dao.insert(u); }
    public Usuario get(int id) { return dao.get(id); }
    public Usuario getByCpf(String cpf) { return dao.getByCpf(cpf); }
    public List<Usuario> listar() { return dao.listar(); }
    public boolean update(Usuario u) { return dao.update(u); }
    public boolean remove(int id) { return dao.remove(id); }
}