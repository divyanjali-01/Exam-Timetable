package database;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    // Save a subject's assigned exam slot
    public boolean saveEntry(int subjectId, int slotId) {

        String sql = """
            INSERT INTO timetable_entries (subject_id, slot_id)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE
                slot_id = VALUES(slot_id),
                generated_at = CURRENT_TIMESTAMP
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.setInt(2, slotId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving timetable entry:");
            e.printStackTrace();
            return false;
        }
    }

    // Remove previously generated entries
    public boolean clearTimetable() {

        String sql = "DELETE FROM timetable_entries";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error clearing timetable:");
            e.printStackTrace();
            return false;
        }
    }

    // Display generated timetable
    public List<String[]> getTimetable() {

        List<String[]> entries = new ArrayList<>();

        String sql = """
            SELECT s.subject_code,
                   s.subject_name,
                   es.exam_date,
                   es.start_time,
                   es.end_time
            FROM timetable_entries te
            JOIN subjects s ON te.subject_id = s.subject_id
            JOIN exam_slots es ON te.slot_id = es.slot_id
            ORDER BY es.exam_date, es.start_time, s.subject_code
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                entries.add(new String[] {
                    rs.getString("subject_code"),
                    rs.getString("subject_name"),
                    rs.getDate("exam_date").toString(),
                    rs.getTime("start_time").toString(),
                    rs.getTime("end_time").toString()
                });
            }

        } catch (SQLException e) {
            System.err.println("Error fetching timetable:");
            e.printStackTrace();
        }

        return entries;
    }
}