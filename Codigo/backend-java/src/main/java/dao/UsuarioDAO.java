package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;

public class UsuarioDAO {

    public boolean insert(Usuario u) {
        String sql = "INSERT INTO usuarios (nome, cpf, email, telefone, data_nascimento, clube_coracao, foto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCpf());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getTelefone());
            ps.setDate(5, u.getDataNascimento());
            ps.setString(6, u.getClubeCoracao());
            ps.setString(7, u.getFoto());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario get(int id) {
        String sql = "SELECT id, nome, cpf, email, telefone, data_nascimento, clube_coracao, foto, cadastrado_em FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getDate("data_nascimento"),
                        rs.getString("clube_coracao"),
                        rs.getString("foto"),
                        rs.getTimestamp("cadastrado_em")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario getByCpf(String cpf) {
        String sql = "SELECT id, nome, cpf, email, telefone, data_nascimento, clube_coracao, foto, cadastrado_em FROM usuarios WHERE cpf = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getDate("data_nascimento"),
                        rs.getString("clube_coracao"),
                        rs.getString("foto"),
                        rs.getTimestamp("cadastrado_em")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, email, telefone, data_nascimento, clube_coracao, foto, cadastrado_em FROM usuarios ORDER BY id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("✅ Conexão estabelecida - Executando query: " + sql);
            while (rs.next()) {
                lista.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("email"),
                    rs.getString("telefone"),
                    rs.getDate("data_nascimento"),
                    rs.getString("clube_coracao"),
                    rs.getString("foto"),
                    rs.getTimestamp("cadastrado_em")
                ));
            }
            System.out.println("📊 Total de usuários encontrados: " + lista.size());
        } catch (SQLException e) {
            System.err.println("❌ ERRO ao listar usuários:");
            e.printStackTrace();
        }
        return lista;
    }

    public boolean update(Usuario u) {
        String sql = "UPDATE usuarios SET nome = ?, cpf = ?, email = ?, telefone = ?, data_nascimento = ?, clube_coracao = ?, foto = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCpf());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getTelefone());
            ps.setDate(5, u.getDataNascimento());
            ps.setString(6, u.getClubeCoracao());
            ps.setString(7, u.getFoto());
            ps.setInt(8, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}