package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Evento;

public class EventoDAO {

    public boolean insert(Evento e) {
        String sql = "INSERT INTO eventos (nome, data, horario, local, descricao, criado_por_admin) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setDate(2, e.getData());
            ps.setTime(3, e.getHorario());
            ps.setString(4, e.getLocal());
            ps.setString(5, e.getDescricao());
            ps.setObject(6, e.getCriadoPorAdmin(), Types.INTEGER);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("[DB] Erro ao inserir evento: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public Evento get(int id) {
        String sql = "SELECT id, nome, data, horario, local, descricao, created_at, criado_por_admin FROM eventos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao buscar evento: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Evento> listar() {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT id, nome, data, horario, local, descricao, created_at, criado_por_admin FROM eventos ORDER BY data, horario";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao listar eventos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public boolean update(Evento e) {
        String sql = "UPDATE eventos SET nome=?, data=?, horario=?, local=?, descricao=?, criado_por_admin=? WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setDate(2, e.getData());
            ps.setTime(3, e.getHorario());
            ps.setString(4, e.getLocal());
            ps.setString(5, e.getDescricao());
            ps.setObject(6, e.getCriadoPorAdmin(), Types.INTEGER);
            ps.setInt(7, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[DB] Erro ao atualizar evento: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean remove(int id) {
        String sql = "DELETE FROM eventos WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao remover evento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Evento mapRow(ResultSet rs) throws SQLException {
        return new Evento(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getDate("data"),
            rs.getTime("horario"),
            rs.getString("local"),
            rs.getString("descricao"),
            rs.getTimestamp("created_at"),
            (Integer) rs.getObject("criado_por_admin")
        );
    }
}
