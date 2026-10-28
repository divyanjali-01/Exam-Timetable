package database;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ExamSlotDAO {

    // Add a new exam slot
    public boolean addExamSlot(
            Date examDate,
            Time startTime,
            Time endTime) {

        String sql = "INSERT INTO exam_slots "
                   + "(exam_date, start_time, end_time) "
                   + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, examDate);
            ps.setTime(2, startTime);
            ps.setTime(3, endTime);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding exam slot:");
            e.printStackTrace();
            return false;
        }
    }

    // Get all exam slots
    public List<String[]> getAllExamSlots() {

        List<String[]> slots = new ArrayList<>();

        String sql = "SELECT slot_id, exam_date, start_time, end_time "
                   + "FROM exam_slots ORDER BY exam_date, start_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                slots.add(new String[] {
                    String.valueOf(rs.getInt("slot_id")),
                    rs.getDate("exam_date").toString(),
                    rs.getTime("start_time").toString(),
                    rs.getTime("end_time").toString()
                });
            }

        } catch (SQLException e) {
            System.err.println("Error fetching exam slots:");
            e.printStackTrace();
        }

        return slots;
    }

    // Delete an exam slot
    public boolean deleteExamSlot(int slotId) {

        String sql = "DELETE FROM exam_slots WHERE slot_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, slotId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting exam slot:");
            e.printStackTrace();
            return false;
        }
    }
}