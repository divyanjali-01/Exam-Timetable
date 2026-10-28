package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/dashboard-data")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String sql = """
            SELECT
                (SELECT COUNT(*) FROM subjects) AS total_subjects,
                (SELECT COUNT(*) FROM exam_slots) AS total_slots,
                (SELECT COUNT(*) FROM conflicts) AS total_conflicts,
                (SELECT COUNT(*) FROM timetable_entries) AS total_entries
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int subjects = rs.getInt("total_subjects");
                int slots = rs.getInt("total_slots");
                int conflicts = rs.getInt("total_conflicts");
                int entries = rs.getInt("total_entries");

                String status;
                if (subjects == 0 || slots == 0) {
                    status = "Add subjects and slots";
                } else if (entries == subjects) {
                    status = "Generated";
                } else {
                    status = "Ready to generate";
                }

                String json = String.format(
                    "{\"subjects\":%d,\"slots\":%d,"
                    + "\"conflicts\":%d,\"entries\":%d,"
                    + "\"status\":\"%s\"}",
                    subjects, slots, conflicts, entries, status
                );

                response.getWriter().write(json);
            }

        } catch (SQLException e) {
            getServletContext().log("Dashboard data error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                "{\"error\":\"Unable to load dashboard data\"}"
            );
        }
    }
}