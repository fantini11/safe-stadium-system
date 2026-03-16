package service;

import dao.EventoDAO;
import model.Evento;
import java.util.List;

public class EventoService {
    private final EventoDAO dao = new EventoDAO();
    public boolean insert(Evento e) { return dao.insert(e); }
    public Evento get(int id) { return dao.get(id); }
    public List<Evento> listar() { return dao.listar(); }
    public boolean update(Evento e) { return dao.update(e); }
    public boolean remove(int id) { return dao.remove(id); }
}