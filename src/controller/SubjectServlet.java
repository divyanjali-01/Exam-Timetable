package controller;

import database.SubjectDAO;
import model.Subject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/subjects")
public class SubjectServlet extends HttpServlet {

    private final SubjectDAO subjectDAO = new SubjectDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        List<Subject> subjects = subjectDAO.getAllSubjects();

        try (var out = response.getWriter()) {

            out.println("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Manage Subjects | Smart Timetable</title>

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
                    font-size: 22px;
                    margin-top: 0;
                    margin-bottom: 20px;
                }

                .form-grid {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 20px;
                }

                label {
                    display: block;
                    margin-bottom: 8px;
                    font-weight: 600;
                    font-size: 14px;
                }

                input {
                    width: 100%;
                    padding: 13px;
                    border: 1px solid #cbd5e1;
                    border-radius: 9px;
                    font: inherit;
                    background: white;
                }

                input:focus {
                    outline: 2px solid #a5b4fc;
                    border-color: #6366f1;
                }

                button {
                    padding: 12px 18px;
                    border: none;
                    border-radius: 9px;
                    background: #2563eb;
                    color: white;
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
                    text-align: left;
                    border-bottom: 1px solid #e2e8f0;
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
                    margin-bottom: 20px;
                    border-radius: 9px;
                    background: #dbeafe;
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

                <h1>Manage Subjects</h1>
                <p class="subtitle">Add and manage exam subjects.</p>
                """);

            String message = request.getParameter("message");

            if ("added".equals(message)) {
                out.println("<div class='message'>Subject added successfully.</div>");
            } else if ("deleted".equals(message)) {
                out.println("<div class='message'>Subject deleted successfully.</div>");
            } else if ("error".equals(message)) {
                out.println("<div class='message'>Operation failed. Check the server console.</div>");
            }

            out.println("""
                <section class="card">
                <h2>Add New Subject</h2>

                <form method="post" action="subjects">
                <input type="hidden" name="action" value="add">

                <div class="form-grid">
                    <div>
                        <label for="subjectCode">Subject Code</label>
                        <input type="text" id="subjectCode"
                               name="subjectCode"
                               placeholder="e.g. CS301" required>
                    </div>

                    <div>
                        <label for="subjectName">Subject Name</label>
                        <input type="text" id="subjectName"
                               name="subjectName"
                               placeholder="e.g. Database Management System"
                               required>
                    </div>
                </div>

                <button class="submit-btn" type="submit">
                    + Add Subject
                </button>
                </form>
                </section>

                <section class="card">
                <h2>All Subjects</h2>

                <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Subject Code</th>
                    <th>Subject Name</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                """);

            if (subjects.isEmpty()) {
                out.println("""
                    <tr>
                    <td colspan="4" class="empty">No subjects added yet.</td>
                    </tr>
                    """);
            } else {
                int count = 1;

                for (Subject subject : subjects) {
                    out.println("<tr>");
                    out.println("<td>" + count++ + "</td>");
                    out.println("<td>" + escapeHtml(subject.getCode()) + "</td>");
                    out.println("<td>" + escapeHtml(subject.getName()) + "</td>");
                    out.println("<td>");
                    out.println("<form method='post' action='subjects'>");
                    out.println("<input type='hidden' name='action' value='delete'>");
                    out.println("<input type='hidden' name='subjectCode' value='"
                            + escapeHtml(subject.getCode()) + "'>");
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
                String code = request.getParameter("subjectCode");
                String name = request.getParameter("subjectName");

                if (code != null && name != null
                        && !code.isBlank() && !name.isBlank()) {

                    success = subjectDAO.addSubject(
                            new Subject(code.trim(), name.trim()));
                }

            } else if ("delete".equals(action)) {
                String code = request.getParameter("subjectCode");

                if (code != null && !code.isBlank()) {
                    success = subjectDAO.deleteSubject(code.trim());
                }
            }

        } catch (Exception e) {
            getServletContext().log("Subject operation failed", e);
        }

        String message = success
                ? ("add".equals(action) ? "added" : "deleted")
                : "error";

        response.sendRedirect(
                request.getContextPath() + "/subjects?message=" + message);
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