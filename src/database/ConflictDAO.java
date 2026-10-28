package database;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConflictDAO {

    // Add a conflict between two subject IDs
    public boolean addConflict(int subjectAId, int subjectBId) {

        if (subjectAId == subjectBId) {
            return false;
        }

        String sql = "INSERT INTO conflicts "
                   + "(subject_a_id, subject_b_id) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int first = Math.min(subjectAId, subjectBId);
            int second = Math.max(subjectAId, subjectBId);

            ps.setInt(1, first);
            ps.setInt(2, second);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding conflict:");
            e.printStackTrace();
            return false;
        }
    }

    // Get all conflicts
    public List<String[]> getAllConflicts() {

        List<String[]> conflicts = new ArrayList<>();

        String sql = """
            SELECT c.conflict_id,
                   s1.subject_code AS code_a,
                   s1.subject_name AS name_a,
                   s2.subject_code AS code_b,
                   s2.subject_name AS name_b
            FROM conflicts c
            JOIN subjects s1 ON c.subject_a_id = s1.subject_id
            JOIN subjects s2 ON c.subject_b_id = s2.subject_id
            ORDER BY c.conflict_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                conflicts.add(new String[] {
                    String.valueOf(rs.getInt("conflict_id")),
                    rs.getString("code_a"),
                    rs.getString("name_a"),
                    rs.getString("code_b"),
                    rs.getString("name_b")
                });
            }

        } catch (SQLException e) {
            System.err.println("Error fetching conflicts:");
            e.printStackTrace();
        }

        return conflicts;
    }

    // Delete a conflict
    public boolean deleteConflict(int conflictId) {

        String sql = "DELETE FROM conflicts WHERE conflict_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, conflictId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting conflict:");
            e.printStackTrace();
            return false;
        }
    }
}