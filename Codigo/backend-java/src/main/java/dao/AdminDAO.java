package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Admin;
import service.security.PasswordUtil;

public class AdminDAO {

    public boolean insert(Admin a) {
        String sql = "INSERT INTO admins (login, senha, nome, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getLogin());
            ps.setString(2, PasswordUtil.hashPassword(a.getSenha()));
            ps.setString(3, a.getNome());
            ps.setString(4, a.getEmail());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao inserir admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Admin get(int id) {
        String sql = "SELECT id, login, senha, nome, email, created_at FROM admins WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao buscar admin por id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Admin getByLogin(String login) {
        String sql = "SELECT id, login, senha, nome, email, created_at FROM admins WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao buscar admin por login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Admin> listar() {
        List<Admin> lista = new ArrayList<>();
        String sql = "SELECT id, login, senha, nome, email, created_at FROM admins ORDER BY id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Admin(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao listar admins: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public boolean update(Admin a) {
        String sql = "UPDATE admins SET login = ?, senha = ?, nome = ?, email = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getLogin());
            ps.setString(2, PasswordUtil.hashPassword(a.getSenha()));
            ps.setString(3, a.getNome());
            ps.setString(4, a.getEmail());
            ps.setInt(5, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao atualizar admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(int id) {
        String sql = "DELETE FROM admins WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao remover admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int migratePlaintextPasswords() {
        List<Admin> lista = listar();
        int atualizados = 0;
        String sql = "UPDATE admins SET senha = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            for (Admin admin : lista) {
                if (!PasswordUtil.isHashed(admin.getSenha())) {
                    String hashed = PasswordUtil.hashPassword(admin.getSenha());
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, hashed);
                        ps.setInt(2, admin.getId());
                        ps.executeUpdate();
                        atualizados++;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao migrar senhas: " + e.getMessage());
            e.printStackTrace();
        }
        return atualizados;
    }
}
