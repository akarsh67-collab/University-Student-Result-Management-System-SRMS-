-- ============================================================
-- University Student Result Management System
-- Database Schema
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS university_srms
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE university_srms;

-- ============================================================
-- DEPARTMENTS
-- ============================================================
CREATE TABLE departments (
    dept_id       INT AUTO_INCREMENT PRIMARY KEY,
    dept_code     VARCHAR(10)  NOT NULL UNIQUE,
    dept_name     VARCHAR(100) NOT NULL,
    hod_name      VARCHAR(100),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- PROGRAMS (B.Tech, M.Tech, BCA, MCA, etc.)
-- ============================================================
CREATE TABLE programs (
    program_id    INT AUTO_INCREMENT PRIMARY KEY,
    dept_id       INT          NOT NULL,
    program_code  VARCHAR(20)  NOT NULL UNIQUE,
    program_name  VARCHAR(100) NOT NULL,
    total_semesters INT        NOT NULL DEFAULT 8,
    degree_type   ENUM('UG','PG','PhD') NOT NULL DEFAULT 'UG',
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- ============================================================
-- ACADEMIC SESSIONS (e.g. 2023-24 ODD, 2023-24 EVEN)
-- ============================================================
CREATE TABLE academic_sessions (
    session_id    INT AUTO_INCREMENT PRIMARY KEY,
    session_name  VARCHAR(50)  NOT NULL UNIQUE,  -- e.g. "2024-25 ODD"
    start_date    DATE         NOT NULL,
    end_date      DATE         NOT NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 0
);

-- ============================================================
-- STUDENTS
-- ============================================================
CREATE TABLE students (
    student_id      INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_no   VARCHAR(20)  NOT NULL UNIQUE,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(15),
    date_of_birth   DATE,
    gender          ENUM('Male','Female','Other'),
    program_id      INT          NOT NULL,
    admission_year  YEAR         NOT NULL,
    current_semester INT         NOT NULL DEFAULT 1,
    status          ENUM('Active','Detained','Graduated','Dropped') DEFAULT 'Active',
    photo_path      VARCHAR(255),
    address         TEXT,
    guardian_name   VARCHAR(100),
    guardian_phone  VARCHAR(15),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (program_id) REFERENCES programs(program_id)
);

-- ============================================================
-- FACULTY / TEACHERS
-- ============================================================
CREATE TABLE faculty (
    faculty_id    INT AUTO_INCREMENT PRIMARY KEY,
    employee_id   VARCHAR(20)  NOT NULL UNIQUE,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(15),
    dept_id       INT          NOT NULL,
    designation   VARCHAR(50),
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- ============================================================
-- COURSES / SUBJECTS
-- ============================================================
CREATE TABLE courses (
    course_id     INT AUTO_INCREMENT PRIMARY KEY,
    course_code   VARCHAR(20)  NOT NULL UNIQUE,
    course_name   VARCHAR(150) NOT NULL,
    program_id    INT          NOT NULL,
    semester      INT          NOT NULL,
    credits       INT          NOT NULL DEFAULT 4,
    course_type   ENUM('Theory','Practical','Project') DEFAULT 'Theory',
    max_marks     INT          NOT NULL DEFAULT 100,
    pass_marks    INT          NOT NULL DEFAULT 40,
    FOREIGN KEY (program_id) REFERENCES programs(program_id)
);

-- ============================================================
-- COURSE ASSIGNMENTS (which faculty teaches which course in a session)
-- ============================================================
CREATE TABLE course_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id     INT NOT NULL,
    faculty_id    INT NOT NULL,
    session_id    INT NOT NULL,
    UNIQUE KEY uq_assignment (course_id, session_id),
    FOREIGN KEY (course_id)  REFERENCES courses(course_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id),
    FOREIGN KEY (session_id) REFERENCES academic_sessions(session_id)
);

-- ============================================================
-- EXAM TYPES (Internal, Mid-Sem, End-Sem, Practical, etc.)
-- ============================================================
CREATE TABLE exam_types (
    exam_type_id  INT AUTO_INCREMENT PRIMARY KEY,
    exam_name     VARCHAR(50)  NOT NULL,
    weightage_pct DECIMAL(5,2) NOT NULL,  -- percentage of total marks
    description   VARCHAR(200)
);

-- ============================================================
-- MARKS / RESULTS
-- ============================================================
CREATE TABLE marks (
    mark_id         INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT          NOT NULL,
    course_id       INT          NOT NULL,
    session_id      INT          NOT NULL,
    exam_type_id    INT          NOT NULL,
    marks_obtained  DECIMAL(6,2) NOT NULL,
    max_marks       INT          NOT NULL DEFAULT 100,
    is_absent       TINYINT(1)   NOT NULL DEFAULT 0,
    entered_by      INT,                           -- faculty_id
    entered_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remarks         VARCHAR(200),
    UNIQUE KEY uq_marks (student_id, course_id, session_id, exam_type_id),
    FOREIGN KEY (student_id)   REFERENCES students(student_id),
    FOREIGN KEY (course_id)    REFERENCES courses(course_id),
    FOREIGN KEY (session_id)   REFERENCES academic_sessions(session_id),
    FOREIGN KEY (exam_type_id) REFERENCES exam_types(exam_type_id),
    FOREIGN KEY (entered_by)   REFERENCES faculty(faculty_id)
);

-- ============================================================
-- GRADE SCALE (configurable per university)
-- ============================================================
CREATE TABLE grade_scale (
    grade_id      INT AUTO_INCREMENT PRIMARY KEY,
    grade_letter  VARCHAR(5)   NOT NULL UNIQUE,
    min_percentage DECIMAL(5,2) NOT NULL,
    max_percentage DECIMAL(5,2) NOT NULL,
    grade_points  DECIMAL(4,2) NOT NULL,
    description   VARCHAR(50)
);

-- ============================================================
-- SEMESTER RESULTS (aggregated per student per semester)
-- ============================================================
CREATE TABLE semester_results (
    result_id     INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT          NOT NULL,
    session_id    INT          NOT NULL,
    semester      INT          NOT NULL,
    total_marks   DECIMAL(8,2),
    max_marks     DECIMAL(8,2),
    percentage    DECIMAL(5,2),
    sgpa          DECIMAL(4,2),
    cgpa          DECIMAL(4,2),
    grade_letter  VARCHAR(5),
    result_status ENUM('Pass','Fail','Detained','Withheld') DEFAULT 'Pass',
    back_subjects INT          NOT NULL DEFAULT 0,
    published     TINYINT(1)   NOT NULL DEFAULT 0,
    published_at  TIMESTAMP    NULL,
    UNIQUE KEY uq_semester_result (student_id, session_id, semester),
    FOREIGN KEY (student_id)  REFERENCES students(student_id),
    FOREIGN KEY (session_id)  REFERENCES academic_sessions(session_id)
);

-- ============================================================
-- ATTENDANCE (optional but important for universities)
-- ============================================================
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT  NOT NULL,
    course_id     INT  NOT NULL,
    session_id    INT  NOT NULL,
    total_classes INT  NOT NULL DEFAULT 0,
    attended      INT  NOT NULL DEFAULT 0,
    percentage    DECIMAL(5,2) GENERATED ALWAYS AS
                  (CASE WHEN total_classes = 0 THEN 0
                   ELSE ROUND((attended / total_classes) * 100, 2) END) STORED,
    UNIQUE KEY uq_attendance (student_id, course_id, session_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id)  REFERENCES courses(course_id),
    FOREIGN KEY (session_id) REFERENCES academic_sessions(session_id)
);

-- ============================================================
-- USERS (login system)
-- ============================================================
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN','FACULTY','STUDENT') NOT NULL,
    ref_id        INT,    -- student_id or faculty_id
    is_active     TINYINT(1) NOT NULL DEFAULT 1,
    last_login    TIMESTAMP NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- INDEXES for performance
-- ============================================================
CREATE INDEX idx_marks_student    ON marks(student_id);
CREATE INDEX idx_marks_course     ON marks(course_id);
CREATE INDEX idx_marks_session    ON marks(session_id);
CREATE INDEX idx_students_program ON students(program_id);
CREATE INDEX idx_courses_program  ON courses(program_id, semester);
CREATE INDEX idx_sem_result_std   ON semester_results(student_id);
