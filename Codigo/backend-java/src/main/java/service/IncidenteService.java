package service;

import dao.IncidenteDAO;
import java.util.List;
import model.Incidente;

public class IncidenteService {
    private final IncidenteDAO dao = new IncidenteDAO();

    public boolean insert(Incidente incidente) {
        return dao.insert(incidente);
    }

    public Incidente get(int id) {
        return dao.get(id);
    }

    public List<Incidente> listar(Boolean resolvido, String nivel) {
        return dao.listar(resolvido, nivel);
    }

    public boolean update(Incidente incidente) {
        return dao.update(incidente);
    }

    public boolean remove(int id) {
        return dao.remove(id);
    }
}

