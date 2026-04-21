package com.university.srms.dao;

import com.university.srms.model.Mark;
import com.university.srms.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Marks and Results.
 */
public class MarksDAO {

    private static final Logger logger = LoggerFactory.getLogger(MarksDAO.class);

    // ----------------------------------------------------------------
    // INSERT / UPDATE marks (upsert)
    // ----------------------------------------------------------------
    public boolean saveOrUpdateMark(Mark mark) throws SQLException {
        String sql = """
            INSERT INTO marks
              (student_id, course_id, session_id, exam_type_id,
               marks_obtained, max_marks, is_absent, entered_by, remarks)
            VALUES (?,?,?,?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE
              marks_obtained = VALUES(marks_obtained),
              max_marks      = VALUES(max_marks),
              is_absent      = VALUES(is_absent),
              entered_by     = VALUES(entered_by),
              remarks        = VALUES(remarks),
              updated_at     = CURRENT_TIMESTAMP
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mark.getStudentId());
            ps.setInt(2, mark.getCourseId());
            ps.setInt(3, mark.getSessionId());
            ps.setInt(4, mark.getExamTypeId());
            ps.setBigDecimal(5, mark.getMarksObtained());
            ps.setInt(6, mark.getMaxMarks());
            ps.setBoolean(7, mark.isAbsent());
            ps.setInt(8, mark.getEnteredBy());
            ps.setString(9, mark.getRemarks());
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // BULK INSERT marks for multiple students (batch)
    // ----------------------------------------------------------------
    public int[] batchSaveMarks(List<Mark> marks) throws SQLException {
        String sql = """
            INSERT INTO marks
              (student_id, course_id, session_id, exam_type_id,
               marks_obtained, max_marks, is_absent, entered_by)
            VALUES (?,?,?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE
              marks_obtained = VALUES(marks_obtained),
              updated_at     = CURRENT_TIMESTAMP
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            try {
                for (Mark m : marks) {
                    ps.setInt(1, m.getStudentId());
                    ps.setInt(2, m.getCourseId());
                    ps.setInt(3, m.getSessionId());
                    ps.setInt(4, m.getExamTypeId());
                    ps.setBigDecimal(5, m.getMarksObtained());
                    ps.setInt(6, m.getMaxMarks());
                    ps.setBoolean(7, m.isAbsent());
                    ps.setInt(8, m.getEnteredBy());
                    ps.addBatch();
                }
                int[] result = ps.executeBatch();
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ----------------------------------------------------------------
    // GET marks for a student in a session
    // ----------------------------------------------------------------
    public List<Mark> getMarksByStudent(int studentId, int sessionId) throws SQLException {
        String sql = """
            SELECT m.*, c.course_code, c.course_name, et.exam_name,
                   ses.session_name, s.enrollment_no,
                   CONCAT(s.first_name,' ',s.last_name) AS student_name
            FROM marks m
            JOIN courses c          ON c.course_id    = m.course_id
            JOIN exam_types et      ON et.exam_type_id = m.exam_type_id
            JOIN academic_sessions ses ON ses.session_id = m.session_id
            JOIN students s         ON s.student_id   = m.student_id
            WHERE m.student_id = ? AND m.session_id = ?
            ORDER BY c.course_code, et.exam_type_id
            """;
        List<Mark> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // GET marks for a course in a session (all students)
    // ----------------------------------------------------------------
    public List<Mark> getMarksByCourse(int courseId, int sessionId, int examTypeId) throws SQLException {
        String sql = """
            SELECT m.*, c.course_code, c.course_name, et.exam_name,
                   ses.session_name, s.enrollment_no,
                   CONCAT(s.first_name,' ',s.last_name) AS student_name
            FROM marks m
            JOIN courses c          ON c.course_id    = m.course_id
            JOIN exam_types et      ON et.exam_type_id = m.exam_type_id
            JOIN academic_sessions ses ON ses.session_id = m.session_id
            JOIN students s         ON s.student_id   = m.student_id
            WHERE m.course_id = ? AND m.session_id = ? AND m.exam_type_id = ?
            ORDER BY s.enrollment_no
            """;
        List<Mark> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, sessionId);
            ps.setInt(3, examTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // GET summary view (aggregated per course)
    // ----------------------------------------------------------------
    public List<java.util.Map<String, Object>> getStudentMarksSummary(int studentId, int sessionId) throws SQLException {
        String sql = """
            SELECT * FROM v_student_marks_summary
            WHERE student_id = ? AND session_id = ?
            ORDER BY course_code
            """;
        List<java.util.Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                while (rs.next()) {
                    java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    list.add(row);
                }
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // Calculate semester result using stored procedure
    // ----------------------------------------------------------------
    public java.util.Map<String, Object> calculateSemesterResult(int studentId, int sessionId, int semester) throws SQLException {
        String sql = "CALL sp_calculate_semester_result(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, studentId);
            cs.setInt(2, sessionId);
            cs.setInt(3, semester);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
                    result.put("message", rs.getString("message"));
                    result.put("percentage", rs.getBigDecimal("percentage"));
                    result.put("sgpa", rs.getBigDecimal("sgpa"));
                    result.put("grade", rs.getString("grade"));
                    return result;
                }
            }
        }
        return java.util.Collections.emptyMap();
    }

    // ----------------------------------------------------------------
    // Publish semester result
    // ----------------------------------------------------------------
    public boolean publishResult(int studentId, int sessionId, int semester) throws SQLException {
        String sql = """
            UPDATE semester_results
            SET published = 1, published_at = CURRENT_TIMESTAMP
            WHERE student_id = ? AND session_id = ? AND semester = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sessionId);
            ps.setInt(3, semester);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // GET report card data
    // ----------------------------------------------------------------
    public java.util.Map<String, Object> getReportCard(int studentId, int semester) throws SQLException {
        java.util.Map<String, Object> reportCard = new java.util.LinkedHashMap<>();

        // Overall semester result
        String resSql = "SELECT * FROM v_report_card WHERE student_id = ? AND semester = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(resSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
                    ResultSetMetaData meta = rs.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        result.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    reportCard.put("result", result);
                }
            }
        }

        // Subject-wise marks (from view)
        String marksSql = """
            SELECT vms.course_code, vms.course_name, vms.credits,
                   vms.total_obtained, vms.max_marks, vms.percentage,
                   vms.grade_letter, vms.grade_points, vms.result
            FROM v_student_marks_summary vms
            JOIN courses c ON c.course_id = vms.course_id
            WHERE vms.student_id = ? AND c.semester = ?
            ORDER BY vms.course_code
            """;
        List<java.util.Map<String, Object>> subjects = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(marksSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                while (rs.next()) {
                    java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    subjects.add(row);
                }
            }
        }
        reportCard.put("subjects", subjects);

        return reportCard;
    }

    // ----------------------------------------------------------------
    // Dashboard stats
    // ----------------------------------------------------------------
    public java.util.Map<String, Object> getDashboardStats() throws SQLException {
        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students WHERE status='Active'")) {
                if (rs.next()) stats.put("totalStudents", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM faculty")) {
                if (rs.next()) stats.put("totalFaculty", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM courses")) {
                if (rs.next()) stats.put("totalCourses", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM semester_results WHERE published=1")) {
                if (rs.next()) stats.put("publishedResults", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery(
                    "SELECT AVG(percentage) FROM semester_results WHERE published=1")) {
                if (rs.next()) {
                    BigDecimal avg = rs.getBigDecimal(1);
                    stats.put("avgPercentage", avg != null ? avg.setScale(2, java.math.RoundingMode.HALF_UP) : 0);
                }
            }
        }
        return stats;
    }

    // ----------------------------------------------------------------
    // MAP ResultSet row → Mark
    // ----------------------------------------------------------------
    private Mark mapRow(ResultSet rs) throws SQLException {
        Mark m = new Mark();
        m.setMarkId(rs.getInt("mark_id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setCourseId(rs.getInt("course_id"));
        m.setSessionId(rs.getInt("session_id"));
        m.setExamTypeId(rs.getInt("exam_type_id"));
        m.setMarksObtained(rs.getBigDecimal("marks_obtained"));
        m.setMaxMarks(rs.getInt("max_marks"));
        m.setAbsent(rs.getBoolean("is_absent"));
        m.setEnteredBy(rs.getInt("entered_by"));
        m.setRemarks(rs.getString("remarks"));
        try { m.setCourseCode(rs.getString("course_code")); } catch (SQLException ignored) {}
        try { m.setCourseName(rs.getString("course_name")); } catch (SQLException ignored) {}
        try { m.setExamTypeName(rs.getString("exam_name")); } catch (SQLException ignored) {}
        try { m.setSessionName(rs.getString("session_name")); } catch (SQLException ignored) {}
        try { m.setEnrollmentNo(rs.getString("enrollment_no")); } catch (SQLException ignored) {}
        try { m.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        return m;
    }
}
