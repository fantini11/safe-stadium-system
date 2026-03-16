package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Incidente;

public class IncidenteDAO {

    public boolean insert(Incidente incidente) {
        String sql = """
            INSERT INTO incidentes
                (data, horario, setor, tipo, descricao, nivel, policiamento, resolvido, usuario_id, evento_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, parseDate(incidente.getData()));
            ps.setTime(2, parseTime(incidente.getHorario()));
            ps.setString(3, incidente.getSetor());
            ps.setString(4, incidente.getTipo());
            ps.setString(5, incidente.getDescricao());
            ps.setString(6, incidente.getNivel());
            ps.setBoolean(7, incidente.isPoliciamento());
            ps.setBoolean(8, incidente.isResolvido());
            ps.setObject(9, incidente.getUsuarioId(), Types.INTEGER);
            ps.setObject(10, incidente.getEventoId(), Types.INTEGER);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Incidente get(int id) {
        String sql = """
            SELECT id, data, horario, setor, tipo, descricao, nivel, policiamento, resolvido,
                   created_at, usuario_id, evento_id
            FROM incidentes
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

    public List<Incidente> listar(Boolean resolvido, String nivel) {
        List<Incidente> incidentes = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT id, data, horario, setor, tipo, descricao, nivel, policiamento, resolvido,
                   created_at, usuario_id, evento_id
            FROM incidentes
            WHERE 1 = 1
        """);

        List<Object> params = new ArrayList<>();

        if (resolvido != null) {
            sql.append(" AND resolvido = ?");
            params.add(resolvido);
        }

        if (nivel != null && !nivel.isBlank()) {
            sql.append(" AND LOWER(nivel) = LOWER(?)");
            params.add(nivel);
        }

        sql.append(" ORDER BY data DESC, horario DESC NULLS LAST, id DESC");

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    incidentes.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidentes;
    }

    public boolean update(Incidente incidente) {
        String sql = """
            UPDATE incidentes
               SET data = ?, horario = ?, setor = ?, tipo = ?, descricao = ?, nivel = ?, policiamento = ?,
                   resolvido = ?, usuario_id = ?, evento_id = ?
             WHERE id = ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, parseDate(incidente.getData()));
            ps.setTime(2, parseTime(incidente.getHorario()));
            ps.setString(3, incidente.getSetor());
            ps.setString(4, incidente.getTipo());
            ps.setString(5, incidente.getDescricao());
            ps.setString(6, incidente.getNivel());
            ps.setBoolean(7, incidente.isPoliciamento());
            ps.setBoolean(8, incidente.isResolvido());
            ps.setObject(9, incidente.getUsuarioId(), Types.INTEGER);
            ps.setObject(10, incidente.getEventoId(), Types.INTEGER);
            ps.setInt(11, incidente.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(int id) {
        String sql = "DELETE FROM incidentes WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Incidente mapRow(ResultSet rs) throws SQLException {
        Date data = rs.getDate("data");
        Time horario = rs.getTime("horario");

        String dataStr = data != null ? data.toString() : null;
        String horarioStr = horario != null ? horario.toLocalTime().toString() : null;

        return new Incidente(
            rs.getInt("id"),
            dataStr,
            horarioStr,
            rs.getString("setor"),
            rs.getString("tipo"),
            rs.getString("descricao"),
            rs.getString("nivel"),
            rs.getBoolean("policiamento"),
            rs.getBoolean("resolvido"),
            rs.getTimestamp("created_at"),
            (Integer) rs.getObject("usuario_id"),
            (Integer) rs.getObject("evento_id")
        );
    }

    private Date parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Date.valueOf(value);
    }

    private Time parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.length() == 5 ? value + ":00" : value;
        return Time.valueOf(normalized);
    }
}
