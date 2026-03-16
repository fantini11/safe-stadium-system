package service;

import model.Admin;
import dao.AdminDAO;
import java.util.List;
import service.security.PasswordUtil;

public class AdminService {
    private AdminDAO dao = new AdminDAO();

    public boolean insert(Admin a) {
        return dao.insert(a);
    }

    public Admin get(int id) {
        return dao.get(id);
    }

    public Admin login(String login, String senha) {
        Admin admin = dao.getByLogin(login);
        if (admin != null && PasswordUtil.matches(senha, admin.getSenha())) {
            if (!PasswordUtil.isHashed(admin.getSenha())) {
                admin.setSenha(PasswordUtil.hashPassword(senha));
                dao.update(admin);
            }
            return admin;
        }
        return null;
    }

    public List<Admin> listar() {
        return dao.listar();
    }

    public boolean update(Admin a) {
        return dao.update(a);
    }

    public boolean remove(int id) {
        return dao.remove(id);
    }

    public int migratePlaintextPasswords() {
        return dao.migratePlaintextPasswords();
    }
}
