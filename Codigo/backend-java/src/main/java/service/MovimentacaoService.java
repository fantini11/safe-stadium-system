package service;

import model.Movimentacao;
import dao.MovimentacaoDAO;
import java.util.List;

public class MovimentacaoService {
    private MovimentacaoDAO dao = new MovimentacaoDAO();

    public boolean insert(Movimentacao m) {
        return dao.insert(m);
    }

    public Movimentacao get(int id) {
        return dao.get(id);
    }

    public List<Movimentacao> listar() {
        return dao.listar();
    }

    public List<Movimentacao> listarPorCpf(String cpf) {
        return dao.listarPorCpf(cpf);
    }

    public boolean update(Movimentacao m) {
        return dao.update(m);
    }

    public boolean remove(int id) {
        return dao.remove(id);
    }
}
