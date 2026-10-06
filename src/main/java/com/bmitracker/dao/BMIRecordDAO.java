package com.bmitracker.dao;

import com.bmitracker.model.BMIRecord;
import com.bmitracker.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) PATTERN: isolates all JDBC/SQL detail behind
 * the Crud<BMIRecord> abstraction so the rest of the app never writes
 * SQL. This also satisfies SOLID's Single Responsibility Principle —
 * this class's only job is talking to the bmi_records table. Note that
 * BMI itself is never stored: it's always recomputed from height/weight
 * via BMIRecord.getBmi(), so there's no risk of a stale stored value.
 */
public class BMIRecordDAO implements Crud<BMIRecord> {

    private final Connection connection;

    public BMIRecordDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean create(BMIRecord r) {
        String sql = "INSERT INTO bmi_records (first_name, last_name, email, " +
                "height_cm, weight_kg) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindRecord(ps, r);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Create failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public BMIRecord read(int id) {
        String sql = "SELECT * FROM bmi_records WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<BMIRecord> readAll() {
        List<BMIRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM bmi_records ORDER BY last_name, first_name";
        try (var stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("ReadAll failed: " + e.getMessage());
        }
        return records;
    }

    @Override
    public boolean update(BMIRecord r) {
        String sql = "UPDATE bmi_records SET first_name=?, last_name=?, email=?, " +
                "height_cm=?, weight_kg=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindRecord(ps, r);
            ps.setInt(6, r.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bmi_records WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
            return false;
        }
    }

    // ----- helpers -----
    private void bindRecord(PreparedStatement ps, BMIRecord r) throws SQLException {
        ps.setString(1, r.getFirstName());
        ps.setString(2, r.getLastName());
        ps.setString(3, r.getEmail());
        ps.setDouble(4, r.getHeightCm());
        ps.setDouble(5, r.getWeightKg());
    }

    private BMIRecord mapRow(ResultSet rs) throws SQLException {
        return new BMIRecord(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getDouble("height_cm"),
                rs.getDouble("weight_kg")
        );
    }
}
