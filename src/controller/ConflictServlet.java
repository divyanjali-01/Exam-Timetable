package controller;

import database.ConflictDAO;
import database.SubjectDAO;
import model.Subject;

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
import java.util.List;

import util.DBConnection;

@WebServlet("/conflicts")
public class ConflictServlet extends HttpServlet {

    private final ConflictDAO conflictDAO = new ConflictDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        List<Subject> subjects = subjectDAO.getAllSubjects();
        List<String[]> conflicts = conflictDAO.getAllConflicts();

        try (var out = response.getWriter()) {

            out.println("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Manage Conflicts | Smart Timetable</title>

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
                    max-width: 1050px;
                    margin: auto;
                }

                h1 {
                    margin: 18px 0 8px;
                    color: #172554;
                    font-size: 34px;
                }

                .subtitle {
                    color: #64748b;
                    margin-bottom: 25px;
                }

                .card {
                    background: white;
                    border: 1px solid #e2e8f0;
                    border-radius: 16px;
                    padding: 25px;
                    margin-bottom: 22px;
                    box-shadow: 0 5px 18px rgba(15, 23, 42, 0.04);
                }

                h2 {
                    margin-top: 0;
                    margin-bottom: 20px;
                    font-size: 22px;
                }

                .form-grid {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 20px;
                }

                label {
                    display: block;
                    font-weight: 600;
                    margin-bottom: 8px;
                }

                select {
                    width: 100%;
                    padding: 13px;
                    border: 1px solid #cbd5e1;
                    border-radius: 9px;
                    background: white;
                    font-size: 14px;
                }

                select:focus {
                    outline: 2px solid #a5b4fc;
                    border-color: #6366f1;
                }

                button {
                    padding: 11px 18px;
                    background: #2563eb;
                    color: white;
                    border: none;
                    border-radius: 8px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                }

                button:hover {
                    background: #1d4ed8;
                }

                .submit-btn {
                    margin-top: 20px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                }

                th, td {
                    padding: 14px;
                    border-bottom: 1px solid #e2e8f0;
                    text-align: left;
                }

                th {
                    background: #eff6ff;
                    color: #1e3a8a;
                }

                .delete-btn {
                    background: #dc2626;
                    padding: 8px 12px;
                }

                .delete-btn:hover {
                    background: #b91c1c;
                }

                .message {
                    padding: 13px;
                    border-radius: 9px;
                    background: #dbeafe;
                    margin-bottom: 20px;
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

                @media (max-width: 650px) {
                    body {
                        padding: 16px;
                    }

                    .form-grid {
                        grid-template-columns: 1fr;
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
                <h1>Manage Conflicts</h1>
                <p class="subtitle">
                    Select subjects that must not have exams in the same slot.
                </p>
                """);

            String message = request.getParameter("message");

            if ("added".equals(message)) {
                out.println("<div class='message'>Conflict added successfully.</div>");
            } else if ("deleted".equals(message)) {
                out.println("<div class='message'>Conflict deleted successfully.</div>");
            } else if ("error".equals(message)) {
                out.println("<div class='message'>Operation failed. Check the server console for details.</div>");
            }

            out.println("""
                <section class="card">
                <h2>Define New Conflict</h2>
                <form method="post" action="conflicts">
                <input type="hidden" name="action" value="add">
                <div class="form-grid">
                <div>
                <label for="subjectA">First Subject</label>
                <select id="subjectA" name="subjectA" required>
                <option value="">Select first subject</option>
                """);

            for (Subject subject : subjects) {
                out.println("<option value=\"" + escapeHtml(subject.getCode())
                        + "\">" + escapeHtml(subject.getCode() + " - "
                        + subject.getName()) + "</option>");
            }

            out.println("""
                </select>
                </div>
                <div>
                <label for="subjectB">Second Subject</label>
                <select id="subjectB" name="subjectB" required>
                <option value="">Select second subject</option>
                """);

            for (Subject subject : subjects) {
                out.println("<option value=\"" + escapeHtml(subject.getCode())
                        + "\">" + escapeHtml(subject.getCode() + " - "
                        + subject.getName()) + "</option>");
            }

            out.println("""
                </select>
                </div>
                </div>
                <button class="submit-btn" type="submit">
                    + Add Conflict
                </button>
                </form>
                </section>

                <section class="card">
                <h2>Defined Conflicts</h2>
                <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>First Subject</th>
                    <th>Second Subject</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                """);

            if (conflicts.isEmpty()) {
                out.println("""
                    <tr>
                    <td colspan="4" class="empty">
                    No conflicts defined yet.
                    </td>
                    </tr>
                    """);
            } else {
                int count = 1;

                for (String[] conflict : conflicts) {
                    out.println("<tr>");
                    out.println("<td>" + count++ + "</td>");
                    out.println("<td>" + escapeHtml(conflict[1]) + "</td>");
                    out.println("<td>" + escapeHtml(conflict[2]) + "</td>");
                    out.println("<td>");
                    out.println("<form method='post' action='conflicts'>");
                    out.println("<input type='hidden' name='action' value='delete'>");
                    out.println("<input type='hidden' name='conflictId' value='"
                            + escapeHtml(conflict[0]) + "'>");
                    out.println("<button class='delete-btn' type='submit'>Delete</button>");
                    out.println("</form>");
                    out.println("</td>");
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

        String action = request.getParameter("action");
        boolean success = false;

        try {
            if ("add".equals(action)) {
                String subjectA = request.getParameter("subjectA");
                String subjectB = request.getParameter("subjectB");

                if (subjectA != null && subjectB != null
                        && !subjectA.equalsIgnoreCase(subjectB)) {

                    int idA = getSubjectId(subjectA);
                    int idB = getSubjectId(subjectB);

                    if (idA > 0 && idB > 0) {
                        success = conflictDAO.addConflict(idA, idB);
                    }
                }

            } else if ("delete".equals(action)) {
                String id = request.getParameter("conflictId");

                if (id != null) {
                    success = conflictDAO.deleteConflict(
                            Integer.parseInt(id));
                }
            }

        } catch (Exception e) {
            getServletContext().log("Conflict operation failed", e);
        }

        String message = success
                ? ("add".equals(action) ? "added" : "deleted")
                : "error";

        response.sendRedirect(
                request.getContextPath() + "/conflicts?message=" + message);
    }

    private int getSubjectId(String subjectCode) throws SQLException {

        String sql = "SELECT subject_id FROM subjects WHERE subject_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, subjectCode);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("subject_id") : -1;
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