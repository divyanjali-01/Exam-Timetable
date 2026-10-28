package database;

import model.Subject;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    // 1. Add Subject
    public boolean addSubject(Subject subject) {

        String sql = "INSERT INTO subjects " +
                "(subject_code, subject_name) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, subject.getCode().trim());
            ps.setString(2, subject.getName().trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding subject:");
            e.printStackTrace();
            return false;
        }
    }

    // 2. Get All Subjects
    public List<Subject> getAllSubjects() {

        List<Subject> subjects = new ArrayList<>();

        String sql = "SELECT subject_code, subject_name " +
                "FROM subjects ORDER BY subject_code";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getString("subject_code"),
                        rs.getString("subject_name")
                );

                subjects.add(subject);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching subjects:");
            e.printStackTrace();
        }

        return subjects;
    }

    // 3. Update Subject
    public boolean updateSubject(
            String oldCode,
            String newCode,
            String newName) {

        String sql = "UPDATE subjects SET subject_code = ?, " +
                "subject_name = ? WHERE subject_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newCode.trim());
            ps.setString(2, newName.trim());
            ps.setString(3, oldCode.trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating subject:");
            e.printStackTrace();
            return false;
        }
    }

    // 4. Delete Subject
    public boolean deleteSubject(String subjectCode) {

        String sql = "DELETE FROM subjects WHERE subject_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, subjectCode.trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting subject:");
            e.printStackTrace();
            return false;
        }
    }
}