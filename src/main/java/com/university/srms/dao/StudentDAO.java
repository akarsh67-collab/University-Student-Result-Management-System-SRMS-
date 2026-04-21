package com.university.srms.dao;

import com.university.srms.model.Student;
import com.university.srms.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations.
 * All DB interaction for students goes here.
 */
public class StudentDAO {

    private static final Logger logger = LoggerFactory.getLogger(StudentDAO.class);

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    public int insertStudent(Student s) throws SQLException {
        String sql = """
            INSERT INTO students
              (enrollment_no, first_name, last_name, email, phone, date_of_birth,
               gender, program_id, admission_year, current_semester, status,
               guardian_name, guardian_phone, address)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getEnrollmentNo());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setDate(6, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
            ps.setString(7, s.getGender());
            ps.setInt(8, s.getProgramId());
            ps.setInt(9, s.getAdmissionYear());
            ps.setInt(10, s.getCurrentSemester());
            ps.setString(11, s.getStatus() != null ? s.getStatus() : "Active");
            ps.setString(12, s.getGuardianName());
            ps.setString(13, s.getGuardianPhone());
            ps.setString(14, s.getAddress());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insert failed, no rows affected");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // READ — by ID
    // ----------------------------------------------------------------
    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT * FROM v_student_profile WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // READ — by enrollment number
    // ----------------------------------------------------------------
    public Student getStudentByEnrollment(String enrollmentNo) throws SQLException {
        String sql = "SELECT * FROM v_student_profile WHERE enrollment_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enrollmentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // READ — all students (optionally filtered by program & semester)
    // ----------------------------------------------------------------
    public List<Student> getAllStudents(Integer programId, Integer semester, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM v_student_profile WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (programId != null) { sql.append(" AND program_id = ?"); params.add(programId); }
        if (semester  != null) { sql.append(" AND current_semester = ?"); params.add(semester); }
        if (status    != null && !status.isBlank()) { sql.append(" AND status = ?"); params.add(status); }
        sql.append(" ORDER BY enrollment_no");

        List<Student> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // READ — search by name or enrollment
    // ----------------------------------------------------------------
    public List<Student> searchStudents(String keyword) throws SQLException {
        String sql = """
            SELECT * FROM v_student_profile
            WHERE enrollment_no LIKE ? OR first_name LIKE ? OR last_name LIKE ?
            ORDER BY enrollment_no
            LIMIT 50
            """;
        String like = "%" + keyword + "%";
        List<Student> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    public boolean updateStudent(Student s) throws SQLException {
        String sql = """
            UPDATE students SET
              first_name = ?, last_name = ?, email = ?, phone = ?,
              date_of_birth = ?, gender = ?, program_id = ?,
              current_semester = ?, status = ?,
              guardian_name = ?, guardian_phone = ?, address = ?
            WHERE student_id = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setDate(5, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
            ps.setString(6, s.getGender());
            ps.setInt(7, s.getProgramId());
            ps.setInt(8, s.getCurrentSemester());
            ps.setString(9, s.getStatus());
            ps.setString(10, s.getGuardianName());
            ps.setString(11, s.getGuardianPhone());
            ps.setString(12, s.getAddress());
            ps.setInt(13, s.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // DELETE (soft — sets status = Dropped)
    // ----------------------------------------------------------------
    public boolean deactivateStudent(int studentId) throws SQLException {
        String sql = "UPDATE students SET status = 'Dropped' WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ----------------------------------------------------------------
    // COUNT
    // ----------------------------------------------------------------
    public int countStudents() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ----------------------------------------------------------------
    // MAP ResultSet → Student
    // ----------------------------------------------------------------
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setEnrollmentNo(rs.getString("enrollment_no"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setGender(rs.getString("gender"));
        s.setAdmissionYear(rs.getInt("admission_year"));
        s.setCurrentSemester(rs.getInt("current_semester"));
        s.setStatus(rs.getString("status"));
        s.setGuardianName(rs.getString("guardian_name"));
        s.setGuardianPhone(rs.getString("guardian_phone"));
        s.setAddress(rs.getString("address"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) s.setDateOfBirth(dob.toLocalDate());

        // from v_student_profile join
        try { s.setProgramId(rs.getInt("program_id")); } catch (SQLException ignored) {}
        try { s.setProgramCode(rs.getString("program_code")); } catch (SQLException ignored) {}
        try { s.setProgramName(rs.getString("program_name")); } catch (SQLException ignored) {}
        try { s.setDeptName(rs.getString("dept_name")); } catch (SQLException ignored) {}

        return s;
    }
}
