package controller;

import algorithm.BacktrackingScheduler;
import database.ConflictDAO;
import database.ExamSlotDAO;
import database.SubjectDAO;
import database.TimetableDAO;
import model.Conflict;
import model.ExamSlot;
import model.Subject;
import util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/timetable")
public class TimetableServlet extends HttpServlet {

    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final ExamSlotDAO examSlotDAO = new ExamSlotDAO();
    private final ConflictDAO conflictDAO = new ConflictDAO();
    private final TimetableDAO timetableDAO = new TimetableDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        List<String[]> entries = timetableDAO.getTimetable();

        try (var out = response.getWriter()) {

            out.println("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Exam Timetable | Smart Timetable Generator</title>

                <style>
                * {
                    box-sizing: border-box;
                }

                body {
                    margin: 0;
                    padding: 30px;
                    background: #f1f5f9;
                    color: #172554;
                    font-family: "Segoe UI", Arial, sans-serif;
                }

                .container {
                    max-width: 1100px;
                    margin: auto;
                }

                h1 {
                    font-size: 34px;
                    margin: 18px 0 8px;
                }

                .subtitle {
                    color: #64748b;
                    margin-bottom: 25px;
                }

                .card {
                    background: white;
                    padding: 25px;
                    border-radius: 16px;
                    border: 1px solid #e2e8f0;
                    margin-bottom: 22px;
                    box-shadow: 0 5px 18px rgba(15, 23, 42, 0.04);
                }

                h2 {
                    font-size: 23px;
                    margin-top: 0;
                    margin-bottom: 20px;
                }

                .generate-btn {
                    display: inline-block;
                    padding: 13px 22px;
                    background: linear-gradient(135deg, #2563eb, #4338ca);
                    color: white;
                    border: none;
                    border-radius: 10px;
                    font-size: 14px;
                    font-weight: 700;
                    cursor: pointer;
                    box-shadow: 0 5px 14px rgba(37, 99, 235, 0.22);
                    transition: all 0.25s ease;
                }

                .generate-btn:hover {
                    background: linear-gradient(135deg, #1d4ed8, #3730a3);
                    transform: translateY(-2px);
                    box-shadow: 0 9px 20px rgba(37, 99, 235, 0.3);
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                }

                th, td {
                    padding: 14px;
                    text-align: left;
                    border-bottom: 1px solid #e2e8f0;
                }

                th {
                    background: #eff6ff;
                    color: #1e3a8a;
                }

                tr:hover td {
                    background: #f8fafc;
                }

                .message {
                    padding: 14px;
                    border-radius: 10px;
                    margin: 18px 0;
                    line-height: 1.6;
                }

                .success {
                    background: #dcfce7;
                    color: #166534;
                }

                .warning {
                    background: #fef3c7;
                    color: #92400e;
                }

                .error {
                    background: #fee2e2;
                    color: #991b1b;
                }

                .empty {
                    color: #64748b;
                    padding: 20px 0;
                }

                a.back-dashboard,
                a.back-dashboard:link,
                a.back-dashboard:visited {
                    display: inline-flex;
                    align-items: center;
                    gap: 12px;
                    padding: 13px 22px;
                    margin-top: 6px;
                    background: linear-gradient(135deg, #111827, #4338ca);
                    color: #ffffff;
                    text-decoration: none;
                    font-size: 14px;
                    font-weight: 700;
                    border: 1px solid #818cf8;
                    border-radius: 12px;
                    box-shadow: 0 6px 16px rgba(67, 56, 202, 0.25);
                    transition: all 0.25s ease;
                }

                a.back-dashboard::before {
                    content: "↶";
                    font-size: 22px;
                }

                a.back-dashboard::after {
                    content: "➜";
                    color: #c7d2fe;
                    transition: transform 0.25s ease;
                }

                a.back-dashboard:hover {
                    background: linear-gradient(135deg, #4338ca, #7c3aed);
                    color: white;
                    text-decoration: none;
                    transform: translateY(-3px);
                    box-shadow: 0 10px 22px rgba(67, 56, 202, 0.35);
                }

                a.back-dashboard:hover::after {
                    transform: translateX(5px);
                }

                @media (max-width: 700px) {
                    body {
                        padding: 16px;
                    }

                    .card {
                        padding: 18px;
                        overflow-x: auto;
                    }

                    h1 {
                        font-size: 27px;
                    }
                }
                </style>
                </head>
                <body>
                <main class="container">

                <h1>Exam Timetable</h1>
                <p class="subtitle">
                    View and generate your conflict-free exam schedule.
                </p>

                <section class="card">
                <form method="post" action="timetable">
                    <button class="generate-btn" type="submit">
                        Generate Timetable
                    </button>
                </form>
                """);

            String message = request.getParameter("message");

            if ("success".equals(message)) {
                out.println("""
                    <div class="message success">
                    Timetable generated and saved successfully!
                    </div>
                    """);
            } else if ("no-solution".equals(message)) {
                out.println("""
                    <div class="message warning">
                    Timetable could not be generated. Add enough exam slots
                    and check your subject conflicts.
                    </div>
                    """);
            } else if ("error".equals(message)) {
                out.println("""
                    <div class="message error">
                    An error occurred while generating the timetable.
                    Check the Tomcat console for details.
                    </div>
                    """);
            }

            out.println("""
                <h2>Generated Timetable</h2>

                <table>
                <thead>
                <tr>
                    <th>Subject Code</th>
                    <th>Subject Name</th>
                    <th>Exam Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                </tr>
                </thead>
                <tbody>
                """);

            if (entries.isEmpty()) {
                out.println("""
                    <tr>
                    <td colspan="5" class="empty">
                    No timetable generated yet. Add subjects and exam slots,
                    then click Generate Timetable.
                    </td>
                    </tr>
                    """);
            } else {
                for (String[] entry : entries) {
                    out.println("<tr>");

                    for (String value : entry) {
                        out.println("<td>" + escapeHtml(value) + "</td>");
                    }

                    out.println("</tr>");
                }
            }

            out.println("""
                </tbody>
                </table>
                </section>

                <a href="index.html" class="back-dashboard">
                    Back to Dashboard
                </a>

                </main>
                </body>
                </html>
                """);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            List<ExamSlot> slots = loadExamSlots();
            List<Conflict> conflicts = loadConflicts(subjects);

            if (subjects.isEmpty() || slots.isEmpty()) {
                response.sendRedirect(
                        request.getContextPath() + "/timetable?message=no-solution");
                return;
            }

            BacktrackingScheduler scheduler =
                    new BacktrackingScheduler(subjects, slots, conflicts);

            Map<String, ExamSlot> result = scheduler.generateTimetable();

            if (result == null || result.size() != subjects.size()) {
                response.sendRedirect(
                        request.getContextPath() + "/timetable?message=no-solution");
                return;
            }

            /*
             * Validate every database slot before clearing the old timetable.
             */
            List<int[]> entriesToSave = new ArrayList<>();

            for (Subject subject : subjects) {
                ExamSlot slot = result.get(subject.getCode());

                if (slot == null) {
                    response.sendRedirect(
                            request.getContextPath() + "/timetable?message=error");
                    return;
                }

                int subjectId = findSubjectId(subject.getCode());
                int slotId = findSlotId(slot);

                if (subjectId <= 0 || slotId <= 0) {
                    response.sendRedirect(
                            request.getContextPath() + "/timetable?message=error");
                    return;
                }

                entriesToSave.add(new int[]{subjectId, slotId});
            }

            if (!timetableDAO.clearTimetable()) {
                response.sendRedirect(
                        request.getContextPath() + "/timetable?message=error");
                return;
            }

            for (int[] entry : entriesToSave) {
                if (!timetableDAO.saveEntry(entry[0], entry[1])) {
                    response.sendRedirect(
                            request.getContextPath() + "/timetable?message=error");
                    return;
                }
            }

            response.sendRedirect(
                    request.getContextPath() + "/timetable?message=success");

        } catch (Exception e) {
            getServletContext().log("Timetable generation failed", e);

            response.sendRedirect(
                    request.getContextPath() + "/timetable?message=error");
        }
    }

    private List<ExamSlot> loadExamSlots() {
        List<ExamSlot> slots = new ArrayList<>();

        for (String[] row : examSlotDAO.getAllExamSlots()) {
            slots.add(new ExamSlot(row[1], row[2], row[3]));
        }

        return slots;
    }

    private List<Conflict> loadConflicts(List<Subject> subjects)
            throws SQLException {

        List<Conflict> conflicts = new ArrayList<>();

        String sql = """
            SELECT sa.subject_code AS code_a,
                   sb.subject_code AS code_b
            FROM conflicts c
            JOIN subjects sa ON c.subject_a_id = sa.subject_id
            JOIN subjects sb ON c.subject_b_id = sb.subject_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Subject a = findSubject(subjects, rs.getString("code_a"));
                Subject b = findSubject(subjects, rs.getString("code_b"));

                if (a != null && b != null) {
                    conflicts.add(new Conflict(a, b));
                }
            }
        }

        return conflicts;
    }

    private Subject findSubject(List<Subject> subjects, String code) {
        for (Subject subject : subjects) {
            if (subject.getCode().equalsIgnoreCase(code)) {
                return subject;
            }
        }

        return null;
    }

    private int findSubjectId(String code) throws SQLException {
        String sql = "SELECT subject_id FROM subjects WHERE subject_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("subject_id") : -1;
            }
        }
    }

    private int findSlotId(ExamSlot slot) throws SQLException {
        String sql = """
            SELECT slot_id
            FROM exam_slots
            WHERE exam_date = ?
              AND start_time = ?
              AND end_time = ?
            ORDER BY slot_id
            LIMIT 1
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(slot.getDate()));
            ps.setTime(2, java.sql.Time.valueOf(slot.getStartTime()));
            ps.setTime(3, java.sql.Time.valueOf(slot.getEndTime()));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("slot_id") : -1;
            }
        }
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}