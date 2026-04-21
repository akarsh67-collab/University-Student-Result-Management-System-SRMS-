-- ============================================================
-- Views & Stored Procedures
-- ============================================================

USE university_srms;

-- ============================================================
-- VIEW: Student marks summary per course per session
-- ============================================================
CREATE OR REPLACE VIEW v_student_marks_summary AS
SELECT
    m.student_id,
    CONCAT(s.first_name, ' ', s.last_name)          AS student_name,
    s.enrollment_no,
    m.course_id,
    c.course_code,
    c.course_name,
    c.credits,
    c.semester,
    m.session_id,
    ses.session_name,
    SUM(m.marks_obtained)                            AS total_obtained,
    c.max_marks                                       AS max_marks,
    ROUND(SUM(m.marks_obtained) / c.max_marks * 100, 2) AS percentage,
    gs.grade_letter,
    gs.grade_points,
    CASE WHEN SUM(m.marks_obtained) >= c.pass_marks THEN 'Pass' ELSE 'Fail' END AS result
FROM marks m
JOIN students s     ON s.student_id   = m.student_id
JOIN courses  c     ON c.course_id    = m.course_id
JOIN academic_sessions ses ON ses.session_id = m.session_id
LEFT JOIN grade_scale gs
    ON ROUND(SUM(m.marks_obtained) / c.max_marks * 100, 2)
       BETWEEN gs.min_percentage AND gs.max_percentage
GROUP BY m.student_id, m.course_id, m.session_id;

-- ============================================================
-- VIEW: Full student profile with program & department
-- ============================================================
CREATE OR REPLACE VIEW v_student_profile AS
SELECT
    s.student_id,
    s.enrollment_no,
    CONCAT(s.first_name, ' ', s.last_name) AS full_name,
    s.first_name,
    s.last_name,
    s.email,
    s.phone,
    s.date_of_birth,
    s.gender,
    s.admission_year,
    s.current_semester,
    s.status,
    s.guardian_name,
    s.guardian_phone,
    s.address,
    p.program_id,
    p.program_code,
    p.program_name,
    p.total_semesters,
    d.dept_id,
    d.dept_code,
    d.dept_name
FROM students s
JOIN programs    p ON p.program_id = s.program_id
JOIN departments d ON d.dept_id    = p.dept_id;

-- ============================================================
-- VIEW: Complete report card per student per semester
-- ============================================================
CREATE OR REPLACE VIEW v_report_card AS
SELECT
    sr.student_id,
    s.enrollment_no,
    CONCAT(s.first_name, ' ', s.last_name) AS student_name,
    p.program_name,
    d.dept_name,
    sr.semester,
    ses.session_name,
    sr.total_marks,
    sr.max_marks,
    sr.percentage,
    sr.sgpa,
    sr.cgpa,
    sr.grade_letter,
    sr.result_status,
    sr.back_subjects,
    sr.published
FROM semester_results sr
JOIN students          s   ON s.student_id   = sr.student_id
JOIN academic_sessions ses ON ses.session_id = sr.session_id
JOIN programs          p   ON p.program_id   = s.program_id
JOIN departments       d   ON d.dept_id      = p.dept_id;

-- ============================================================
-- VIEW: Class topper per session & semester
-- ============================================================
CREATE OR REPLACE VIEW v_class_toppers AS
SELECT
    sr.session_id,
    ses.session_name,
    s.program_id,
    p.program_name,
    sr.semester,
    s.student_id,
    s.enrollment_no,
    CONCAT(s.first_name, ' ', s.last_name) AS student_name,
    sr.percentage,
    sr.sgpa,
    sr.grade_letter,
    RANK() OVER (
        PARTITION BY sr.session_id, s.program_id, sr.semester
        ORDER BY sr.percentage DESC
    ) AS rank_in_class
FROM semester_results sr
JOIN students          s   ON s.student_id   = sr.student_id
JOIN programs          p   ON p.program_id   = s.program_id
JOIN academic_sessions ses ON ses.session_id = sr.session_id
WHERE sr.published = 1;

-- ============================================================
-- STORED PROCEDURE: Calculate and save semester result
-- ============================================================
DELIMITER $$

CREATE PROCEDURE sp_calculate_semester_result(
    IN  p_student_id  INT,
    IN  p_session_id  INT,
    IN  p_semester    INT
)
BEGIN
    DECLARE v_total_obtained  DECIMAL(10,2) DEFAULT 0;
    DECLARE v_total_max       DECIMAL(10,2) DEFAULT 0;
    DECLARE v_percentage      DECIMAL(5,2)  DEFAULT 0;
    DECLARE v_total_credits   INT           DEFAULT 0;
    DECLARE v_grade_points    DECIMAL(10,4) DEFAULT 0;
    DECLARE v_sgpa            DECIMAL(4,2)  DEFAULT 0;
    DECLARE v_grade_letter    VARCHAR(5)    DEFAULT 'F';
    DECLARE v_back_subjects   INT           DEFAULT 0;
    DECLARE v_result_status   VARCHAR(20)   DEFAULT 'Pass';
    DECLARE v_cgpa            DECIMAL(4,2)  DEFAULT 0;

    -- Aggregate marks for all courses this semester/session
    SELECT
        COALESCE(SUM(vms.total_obtained), 0),
        COALESCE(SUM(vms.max_marks), 0),
        COUNT(CASE WHEN vms.result = 'Fail' THEN 1 END),
        COALESCE(SUM(c.credits), 0),
        COALESCE(SUM(vms.grade_points * c.credits), 0)
    INTO
        v_total_obtained,
        v_total_max,
        v_back_subjects,
        v_total_credits,
        v_grade_points
    FROM v_student_marks_summary vms
    JOIN courses c ON c.course_id = vms.course_id
    WHERE vms.student_id = p_student_id
      AND vms.session_id = p_session_id
      AND c.semester     = p_semester;

    -- Calculate percentage
    IF v_total_max > 0 THEN
        SET v_percentage = ROUND(v_total_obtained / v_total_max * 100, 2);
    END IF;

    -- Calculate SGPA
    IF v_total_credits > 0 THEN
        SET v_sgpa = ROUND(v_grade_points / v_total_credits, 2);
    END IF;

    -- Get grade letter
    SELECT grade_letter INTO v_grade_letter
    FROM grade_scale
    WHERE v_percentage BETWEEN min_percentage AND max_percentage
    LIMIT 1;

    -- Set result status
    IF v_back_subjects > 0 THEN
        SET v_result_status = 'Fail';
    END IF;

    -- Calculate CGPA (average of all published SGPAs for this student)
    SELECT COALESCE(AVG(sgpa), v_sgpa) INTO v_cgpa
    FROM semester_results
    WHERE student_id = p_student_id AND published = 1;

    -- Insert or update semester result
    INSERT INTO semester_results
        (student_id, session_id, semester, total_marks, max_marks, percentage, sgpa, cgpa, grade_letter, result_status, back_subjects, published)
    VALUES
        (p_student_id, p_session_id, p_semester, v_total_obtained, v_total_max, v_percentage, v_sgpa, v_cgpa, v_grade_letter, v_result_status, v_back_subjects, 0)
    ON DUPLICATE KEY UPDATE
        total_marks   = v_total_obtained,
        max_marks     = v_total_max,
        percentage    = v_percentage,
        sgpa          = v_sgpa,
        cgpa          = v_cgpa,
        grade_letter  = v_grade_letter,
        result_status = v_result_status,
        back_subjects = v_back_subjects;

    SELECT 'Result calculated successfully' AS message,
           v_percentage AS percentage, v_sgpa AS sgpa, v_grade_letter AS grade;
END$$

-- ============================================================
-- STORED PROCEDURE: Get full report card for a student
-- ============================================================
CREATE PROCEDURE sp_get_report_card(
    IN p_student_id INT,
    IN p_semester   INT,
    IN p_session_id INT
)
BEGIN
    -- Student info
    SELECT * FROM v_student_profile WHERE student_id = p_student_id;

    -- Subject-wise marks
    SELECT
        vms.course_code,
        vms.course_name,
        vms.credits,
        vms.total_obtained,
        vms.max_marks,
        vms.percentage,
        vms.grade_letter,
        vms.grade_points,
        vms.result
    FROM v_student_marks_summary vms
    JOIN courses c ON c.course_id = vms.course_id
    WHERE vms.student_id = p_student_id
      AND vms.session_id = p_session_id
      AND c.semester     = p_semester
    ORDER BY vms.course_code;

    -- Overall result
    SELECT * FROM v_report_card
    WHERE student_id = p_student_id
      AND semester   = p_semester;
END$$

DELIMITER ;
