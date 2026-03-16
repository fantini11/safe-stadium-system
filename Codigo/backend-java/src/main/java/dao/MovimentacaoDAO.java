package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Movimentacao;

public class MovimentacaoDAO {

    public boolean insert(Movimentacao m) {
        String sql = """
            INSERT INTO movimentacoes
                (tipo, portao, cpf, nome, data, hora, timestamp, usuario_id, evento_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (m.getUsuarioId() == null && m.getCpf() != null) {
                Integer usuarioId = buscarUsuarioIdPorCpf(conn, m.getCpf());
                m.setUsuarioId(usuarioId);
            }
            ps.setString(1, m.getTipo());
            ps.setString(2, m.getPortao());
            ps.setString(3, m.getCpf());
            ps.setString(4, m.getNome());
            ps.setString(5, m.getData());
            ps.setString(6, m.getHora());
            ps.setTimestamp(7, m.getTimestamp());
            ps.setObject(8, m.getUsuarioId(), Types.INTEGER);
            ps.setObject(9, m.getEventoId(), Types.INTEGER);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Movimentacao get(int id) {
        String sql = """
            SELECT id, tipo, portao, cpf, nome, data, hora, timestamp, created_at, usuario_id, evento_id
            FROM movimentacoes
            WHERE id = ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Movimentacao> listar() {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = """
            SELECT id, tipo, portao, cpf, nome, data, hora, timestamp, created_at, usuario_id, evento_id
            FROM movimentacoes
            ORDER BY timestamp DESC
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Movimentacao> listarPorCpf(String cpf) {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = """
            SELECT id, tipo, portao, cpf, nome, data, hora, timestamp, created_at, usuario_id, evento_id
            FROM movimentacoes
            WHERE cpf = ?
            ORDER BY timestamp DESC
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean update(Movimentacao m) {
        String sql = """
            UPDATE movimentacoes
               SET tipo = ?, portao = ?, cpf = ?, nome = ?, data = ?, hora = ?, timestamp = ?, usuario_id = ?, evento_id = ?
             WHERE id = ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTipo());
            ps.setString(2, m.getPortao());
            ps.setString(3, m.getCpf());
            ps.setString(4, m.getNome());
            ps.setString(5, m.getData());
            ps.setString(6, m.getHora());
            ps.setTimestamp(7, m.getTimestamp());
            ps.setObject(8, m.getUsuarioId(), Types.INTEGER);
            ps.setObject(9, m.getEventoId(), Types.INTEGER);
            ps.setInt(10, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(int id) {
        String sql = "DELETE FROM movimentacoes WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Movimentacao mapRow(ResultSet rs) throws SQLException {
        return new Movimentacao(
            rs.getInt("id"),
            rs.getString("tipo"),
            rs.getString("portao"),
            rs.getString("cpf"),
            rs.getString("nome"),
            rs.getString("data"),
            rs.getString("hora"),
            rs.getTimestamp("timestamp"),
            rs.getTimestamp("created_at"),
            (Integer) rs.getObject("usuario_id"),
            (Integer) rs.getObject("evento_id")
        );
    }

    private Integer buscarUsuarioIdPorCpf(Connection conn, String cpf) throws SQLException {
        String sql = "SELECT id FROM usuarios WHERE cpf = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }
}
