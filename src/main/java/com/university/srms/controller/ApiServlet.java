package com.university.srms.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.university.srms.dao.MarksDAO;
import com.university.srms.dao.StudentDAO;
import com.university.srms.model.Mark;
import com.university.srms.model.Student;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Main REST API Servlet.
 * Maps URL patterns to CRUD operations.
 *
 * API Endpoints:
 *   GET  /api/students              → list all students
 *   GET  /api/students?id=X         → get student by ID
 *   GET  /api/students?search=name  → search students
 *   POST /api/students              → create student
 *   PUT  /api/students              → update student
 *
 *   GET  /api/marks?studentId=X&sessionId=Y     → get marks
 *   POST /api/marks                              → save mark
 *   POST /api/marks/calculate                   → calc semester result
 *   POST /api/marks/publish                     → publish result
 *
 *   GET  /api/report?studentId=X&semester=Y     → report card JSON
 *   GET  /api/dashboard                         → dashboard stats
 */
@WebServlet(urlPatterns = {"/api/*"}, asyncSupported = true)
public class ApiServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ApiServlet.class);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private final StudentDAO studentDAO = new StudentDAO();
    private final MarksDAO   marksDAO   = new MarksDAO();

    // ----------------------------------------------------------------
    // GET
    // ----------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonResponse(resp);
        String path = getPath(req);
        PrintWriter out = resp.getWriter();

        try {
            switch (path) {
                case "/students" -> handleGetStudents(req, out);
                case "/marks"    -> handleGetMarks(req, out);
                case "/report"   -> handleGetReport(req, out);
                case "/dashboard"-> handleDashboard(out);
                case "/courses"  -> handleGetCourses(req, out);
                case "/sessions" -> handleGetSessions(out);
                default          -> writeError(resp, out, 404, "Endpoint not found: " + path);
            }
        } catch (SQLException e) {
            logger.error("DB error on GET " + path, e);
            writeError(resp, out, 500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error on GET " + path, e);
            writeError(resp, out, 500, "Server error: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // POST
    // ----------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonResponse(resp);
        String path = getPath(req);
        PrintWriter out = resp.getWriter();

        try {
            JsonObject body = parseBody(req);
            switch (path) {
                case "/students"          -> handleCreateStudent(body, resp, out);
                case "/marks"             -> handleSaveMark(body, resp, out);
                case "/marks/calculate"   -> handleCalculateResult(body, out);
                case "/marks/publish"     -> handlePublishResult(body, out);
                default                   -> writeError(resp, out, 404, "Endpoint not found: " + path);
            }
        } catch (SQLException e) {
            logger.error("DB error on POST " + path, e);
            writeError(resp, out, 500, "Database error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error on POST " + path, e);
            writeError(resp, out, 500, "Server error: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // PUT
    // ----------------------------------------------------------------
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonResponse(resp);
        String path = getPath(req);
        PrintWriter out = resp.getWriter();

        try {
            JsonObject body = parseBody(req);
            if ("/students".equals(path)) {
                handleUpdateStudent(body, resp, out);
            } else {
                writeError(resp, out, 404, "Endpoint not found: " + path);
            }
        } catch (SQLException e) {
            writeError(resp, out, 500, "Database error: " + e.getMessage());
        }
    }

    // ================================================================
    // HANDLERS
    // ================================================================

    private void handleGetStudents(HttpServletRequest req, PrintWriter out) throws SQLException {
        String idParam     = req.getParameter("id");
        String enrollment  = req.getParameter("enrollment");
        String searchParam = req.getParameter("search");
        String programId   = req.getParameter("programId");
        String semester    = req.getParameter("semester");
        String status      = req.getParameter("status");

        if (idParam != null) {
            Student s = studentDAO.getStudentById(Integer.parseInt(idParam));
            out.print(gson.toJson(s));
        } else if (enrollment != null) {
            Student s = studentDAO.getStudentByEnrollment(enrollment);
            out.print(gson.toJson(s));
        } else if (searchParam != null) {
            List<Student> list = studentDAO.searchStudents(searchParam);
            out.print(gson.toJson(list));
        } else {
            Integer pid = programId != null ? Integer.parseInt(programId) : null;
            Integer sem = semester   != null ? Integer.parseInt(semester)  : null;
            List<Student> list = studentDAO.getAllStudents(pid, sem, status);
            out.print(gson.toJson(list));
        }
    }

    private void handleCreateStudent(JsonObject body, HttpServletResponse resp, PrintWriter out) throws SQLException {
        Student s = new Student();
        s.setEnrollmentNo(getString(body, "enrollmentNo"));
        s.setFirstName(getString(body, "firstName"));
        s.setLastName(getString(body, "lastName"));
        s.setEmail(getString(body, "email"));
        s.setPhone(getString(body, "phone"));
        s.setGender(getString(body, "gender"));
        s.setProgramId(getInt(body, "programId"));
        s.setAdmissionYear(getInt(body, "admissionYear"));
        s.setCurrentSemester(getInt(body, "currentSemester", 1));
        s.setStatus("Active");
        s.setGuardianName(getString(body, "guardianName"));
        s.setGuardianPhone(getString(body, "guardianPhone"));
        s.setAddress(getString(body, "address"));
        if (body.has("dateOfBirth") && !body.get("dateOfBirth").isJsonNull()) {
            s.setDateOfBirth(LocalDate.parse(body.get("dateOfBirth").getAsString()));
        }
        int newId = studentDAO.insertStudent(s);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        out.print(gson.toJson(Map.of("success", true, "studentId", newId)));
    }

    private void handleUpdateStudent(JsonObject body, HttpServletResponse resp, PrintWriter out) throws SQLException {
        Student s = new Student();
        s.setStudentId(getInt(body, "studentId"));
        s.setFirstName(getString(body, "firstName"));
        s.setLastName(getString(body, "lastName"));
        s.setEmail(getString(body, "email"));
        s.setPhone(getString(body, "phone"));
        s.setGender(getString(body, "gender"));
        s.setProgramId(getInt(body, "programId"));
        s.setCurrentSemester(getInt(body, "currentSemester", 1));
        s.setStatus(getString(body, "status"));
        s.setGuardianName(getString(body, "guardianName"));
        s.setGuardianPhone(getString(body, "guardianPhone"));
        s.setAddress(getString(body, "address"));
        boolean ok = studentDAO.updateStudent(s);
        out.print(gson.toJson(Map.of("success", ok)));
    }

    private void handleGetMarks(HttpServletRequest req, PrintWriter out) throws SQLException {
        int studentId = Integer.parseInt(req.getParameter("studentId"));
        int sessionId = Integer.parseInt(req.getParameter("sessionId"));
        String type   = req.getParameter("type"); // "summary" or null for raw

        if ("summary".equals(type)) {
            out.print(gson.toJson(marksDAO.getStudentMarksSummary(studentId, sessionId)));
        } else {
            out.print(gson.toJson(marksDAO.getMarksByStudent(studentId, sessionId)));
        }
    }

    private void handleSaveMark(JsonObject body, HttpServletResponse resp, PrintWriter out) throws SQLException {
        Mark m = new Mark();
        m.setStudentId(getInt(body, "studentId"));
        m.setCourseId(getInt(body, "courseId"));
        m.setSessionId(getInt(body, "sessionId"));
        m.setExamTypeId(getInt(body, "examTypeId"));
        m.setMarksObtained(new BigDecimal(getString(body, "marksObtained")));
        m.setMaxMarks(getInt(body, "maxMarks"));
        m.setAbsent(body.has("isAbsent") && body.get("isAbsent").getAsBoolean());
        m.setEnteredBy(getInt(body, "enteredBy", 1));
        m.setRemarks(getString(body, "remarks"));
        boolean ok = marksDAO.saveOrUpdateMark(m);
        out.print(gson.toJson(Map.of("success", ok)));
    }

    private void handleCalculateResult(JsonObject body, PrintWriter out) throws SQLException {
        int studentId = getInt(body, "studentId");
        int sessionId = getInt(body, "sessionId");
        int semester  = getInt(body, "semester");
        Map<String, Object> result = marksDAO.calculateSemesterResult(studentId, sessionId, semester);
        out.print(gson.toJson(result));
    }

    private void handlePublishResult(JsonObject body, PrintWriter out) throws SQLException {
        int studentId = getInt(body, "studentId");
        int sessionId = getInt(body, "sessionId");
        int semester  = getInt(body, "semester");
        boolean ok = marksDAO.publishResult(studentId, sessionId, semester);
        out.print(gson.toJson(Map.of("success", ok)));
    }

    private void handleGetReport(HttpServletRequest req, PrintWriter out) throws SQLException {
        int studentId = Integer.parseInt(req.getParameter("studentId"));
        int semester  = Integer.parseInt(req.getParameter("semester"));
        Map<String, Object> report = marksDAO.getReportCard(studentId, semester);
        out.print(gson.toJson(report));
    }

    private void handleDashboard(PrintWriter out) throws SQLException {
        out.print(gson.toJson(marksDAO.getDashboardStats()));
    }

    private void handleGetCourses(HttpServletRequest req, PrintWriter out) throws SQLException {
        // Basic course list from DB
        int programId = Integer.parseInt(req.getParameter("programId"));
        int semester  = Integer.parseInt(req.getParameter("semester"));
        String sql = "SELECT * FROM courses WHERE program_id=? AND semester=? ORDER BY course_code";
        var list = new java.util.ArrayList<Map<String, Object>>();
        try (var conn = com.university.srms.util.DBConnection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, programId); ps.setInt(2, semester);
            try (var rs = ps.executeQuery()) {
                var meta = rs.getMetaData();
                while (rs.next()) {
                    var row = new java.util.LinkedHashMap<String, Object>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) row.put(meta.getColumnLabel(i), rs.getObject(i));
                    list.add(row);
                }
            }
        }
        out.print(gson.toJson(list));
    }

    private void handleGetSessions(PrintWriter out) throws SQLException {
        String sql = "SELECT * FROM academic_sessions ORDER BY session_id DESC";
        var list = new java.util.ArrayList<Map<String, Object>>();
        try (var conn = com.university.srms.util.DBConnection.getConnection();
             var st = conn.createStatement();
             var rs = st.executeQuery(sql)) {
            var meta = rs.getMetaData();
            while (rs.next()) {
                var row = new java.util.LinkedHashMap<String, Object>();
                for (int i = 1; i <= meta.getColumnCount(); i++) row.put(meta.getColumnLabel(i), rs.getObject(i));
                list.add(row);
            }
        }
        out.print(gson.toJson(list));
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private String getPath(HttpServletRequest req) {
        String info = req.getPathInfo();
        return info != null ? info : "/";
    }

    private void setJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private JsonObject parseBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (var reader = req.getReader()) {
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return gson.fromJson(sb.toString(), JsonObject.class);
    }

    private void writeError(HttpServletResponse resp, PrintWriter out, int code, String msg) {
        resp.setStatus(code);
        out.print(gson.toJson(Map.of("error", msg)));
    }

    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : null;
    }

    private int getInt(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsInt() : 0;
    }

    private int getInt(JsonObject obj, String key, int def) {
        return obj.has(key) ? obj.get(key).getAsInt() : def;
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setJsonResponse(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
