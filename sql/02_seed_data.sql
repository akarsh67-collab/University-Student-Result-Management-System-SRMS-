-- ============================================================
-- Seed / Sample Data
-- Run after 01_schema.sql
-- ============================================================

USE university_srms;

-- Grade Scale (10-point system)
INSERT INTO grade_scale (grade_letter, min_percentage, max_percentage, grade_points, description) VALUES
('O',   90, 100, 10.0, 'Outstanding'),
('A+',  80,  89,  9.0, 'Excellent'),
('A',   70,  79,  8.0, 'Very Good'),
('B+',  60,  69,  7.0, 'Good'),
('B',   50,  59,  6.0, 'Above Average'),
('C',   45,  49,  5.0, 'Average'),
('P',   40,  44,  4.0, 'Pass'),
('F',    0,  39,  0.0, 'Fail');

-- Exam Types
INSERT INTO exam_types (exam_name, weightage_pct, description) VALUES
('Internal Assessment', 20.00, 'Continuous internal evaluation'),
('Mid Semester',        30.00, 'Mid semester examination'),
('End Semester',        50.00, 'End semester examination');

-- Departments
INSERT INTO departments (dept_code, dept_name, hod_name) VALUES
('CSE',  'Computer Science & Engineering',        'Dr. Ramesh Gupta'),
('ECE',  'Electronics & Communication Engineering','Dr. Sunita Sharma'),
('ME',   'Mechanical Engineering',                 'Dr. Anil Verma'),
('MATH', 'Mathematics & Statistics',               'Dr. Priya Singh');

-- Programs
INSERT INTO programs (dept_id, program_code, program_name, total_semesters, degree_type) VALUES
(1, 'BTECH-CSE', 'B.Tech Computer Science & Engineering', 8,  'UG'),
(1, 'MTECH-CSE', 'M.Tech Computer Science',               4,  'PG'),
(1, 'MCA',       'Master of Computer Applications',       6,  'PG'),
(2, 'BTECH-ECE', 'B.Tech Electronics & Communication',    8,  'UG'),
(3, 'BTECH-ME',  'B.Tech Mechanical Engineering',         8,  'UG');

-- Academic Sessions
INSERT INTO academic_sessions (session_name, start_date, end_date, is_active) VALUES
('2022-23 ODD',  '2022-08-01', '2022-12-31', 0),
('2022-23 EVEN', '2023-01-01', '2023-05-31', 0),
('2023-24 ODD',  '2023-08-01', '2023-12-31', 0),
('2023-24 EVEN', '2024-01-01', '2024-05-31', 0),
('2024-25 ODD',  '2024-08-01', '2024-12-31', 1);  -- Active session

-- Courses (B.Tech CSE Sem 1 & 2)
INSERT INTO courses (course_code, course_name, program_id, semester, credits, course_type, max_marks, pass_marks) VALUES
-- Semester 1
('CS101', 'Mathematics I',                      1, 1, 4, 'Theory',    100, 40),
('CS102', 'Physics',                             1, 1, 4, 'Theory',    100, 40),
('CS103', 'Programming in C',                   1, 1, 4, 'Theory',    100, 40),
('CS104', 'Programming Lab',                    1, 1, 2, 'Practical',  50, 20),
('CS105', 'Engineering Drawing',                1, 1, 3, 'Theory',    100, 40),
('CS106', 'Communication Skills',               1, 1, 2, 'Theory',     50, 20),
-- Semester 2
('CS201', 'Mathematics II',                     1, 2, 4, 'Theory',    100, 40),
('CS202', 'Data Structures',                    1, 2, 4, 'Theory',    100, 40),
('CS203', 'Digital Electronics',                1, 2, 4, 'Theory',    100, 40),
('CS204', 'Data Structures Lab',                1, 2, 2, 'Practical',  50, 20),
('CS205', 'Object Oriented Programming (Java)', 1, 2, 4, 'Theory',    100, 40),
('CS206', 'Environmental Science',              1, 2, 2, 'Theory',     50, 20),
-- Semester 3
('CS301', 'Discrete Mathematics',               1, 3, 4, 'Theory',    100, 40),
('CS302', 'Database Management Systems',        1, 3, 4, 'Theory',    100, 40),
('CS303', 'Computer Organization',              1, 3, 4, 'Theory',    100, 40),
('CS304', 'DBMS Lab',                           1, 3, 2, 'Practical',  50, 20),
('CS305', 'Operating Systems',                  1, 3, 4, 'Theory',    100, 40),
('CS306', 'Web Technologies',                   1, 3, 3, 'Theory',    100, 40);

-- Faculty
INSERT INTO faculty (employee_id, name, email, phone, dept_id, designation) VALUES
('FAC001', 'Dr. Ramesh Gupta',   'ramesh.gupta@university.edu',   '9876543210', 1, 'Professor'),
('FAC002', 'Dr. Kavita Joshi',   'kavita.joshi@university.edu',   '9876543211', 1, 'Associate Professor'),
('FAC003', 'Prof. Manish Tiwari','manish.tiwari@university.edu',  '9876543212', 1, 'Assistant Professor'),
('FAC004', 'Dr. Seema Pandey',   'seema.pandey@university.edu',   '9876543213', 1, 'Associate Professor'),
('FAC005', 'Prof. Rohit Sinha',  'rohit.sinha@university.edu',    '9876543214', 1, 'Assistant Professor');

-- Students (B.Tech CSE 2023 batch)
INSERT INTO students (enrollment_no, first_name, last_name, email, phone, date_of_birth, gender, program_id, admission_year, current_semester, status, guardian_name, guardian_phone) VALUES
('2023CSE001', 'Rahul',    'Kumar',    'rahul.kumar@student.edu',    '9111000001', '2005-03-15', 'Male',   1, 2023, 3, 'Active', 'Suresh Kumar',   '9222000001'),
('2023CSE002', 'Priya',    'Sharma',   'priya.sharma@student.edu',   '9111000002', '2005-07-22', 'Female', 1, 2023, 3, 'Active', 'Vijay Sharma',   '9222000002'),
('2023CSE003', 'Amit',     'Singh',    'amit.singh@student.edu',     '9111000003', '2005-01-10', 'Male',   1, 2023, 3, 'Active', 'Rakesh Singh',   '9222000003'),
('2023CSE004', 'Sneha',    'Mishra',   'sneha.mishra@student.edu',   '9111000004', '2004-11-30', 'Female', 1, 2023, 3, 'Active', 'Dinesh Mishra',  '9222000004'),
('2023CSE005', 'Vivek',    'Pandey',   'vivek.pandey@student.edu',   '9111000005', '2005-05-18', 'Male',   1, 2023, 3, 'Active', 'Arun Pandey',    '9222000005'),
('2023CSE006', 'Anjali',   'Gupta',    'anjali.gupta@student.edu',   '9111000006', '2004-09-25', 'Female', 1, 2023, 3, 'Active', 'Mohan Gupta',    '9222000006'),
('2023CSE007', 'Rohit',    'Verma',    'rohit.verma@student.edu',    '9111000007', '2005-02-14', 'Male',   1, 2023, 3, 'Active', 'Sunil Verma',    '9222000007'),
('2023CSE008', 'Pooja',    'Tiwari',   'pooja.tiwari@student.edu',   '9111000008', '2004-12-03', 'Female', 1, 2023, 3, 'Active', 'Ramesh Tiwari',  '9222000008'),
('2023CSE009', 'Arjun',    'Yadav',    'arjun.yadav@student.edu',    '9111000009', '2005-08-20', 'Male',   1, 2023, 3, 'Active', 'Mahesh Yadav',   '9222000009'),
('2023CSE010', 'Divya',    'Srivastava','divya.sri@student.edu',     '9111000010', '2005-04-12', 'Female', 1, 2023, 3, 'Active', 'Govind Sri',     '9222000010');

-- Users
INSERT INTO users (username, password_hash, role, ref_id) VALUES
-- admin (password: Admin@123)
('admin',       '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhi', 'ADMIN',   NULL),
-- faculty (password: Faculty@123)
('fac001',      '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhi', 'FACULTY', 1),
('fac002',      '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhi', 'FACULTY', 2),
-- students (password: Student@123)
('2023CSE001',  '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhi', 'STUDENT', 1),
('2023CSE002',  '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhi', 'STUDENT', 2),
('2023CSE003',  '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhi', 'STUDENT', 3);

-- Sample marks for Semester 1 (session: 2023-24 ODD = session_id 3)
-- Student 1 (Rahul Kumar) - Sem 1
INSERT INTO marks (student_id, course_id, session_id, exam_type_id, marks_obtained, max_marks, entered_by) VALUES
(1,1,3,1,18,20,1),(1,1,3,2,27,30,1),(1,1,3,3,42,50,1),  -- CS101 Math I
(1,2,3,1,16,20,1),(1,2,3,2,25,30,1),(1,2,3,3,40,50,1),  -- CS102 Physics
(1,3,3,1,19,20,1),(1,3,3,2,28,30,1),(1,3,3,3,45,50,1),  -- CS103 C Programming
(1,4,3,1,17,20,1),(1,4,3,2,22,30,1),(1,4,3,3,35,50,1),  -- CS104 Lab (max 50)
(1,5,3,1,15,20,1),(1,5,3,2,24,30,1),(1,5,3,3,38,50,1),  -- CS105
(1,6,3,1,14,20,1),(1,6,3,2,21,30,1),(1,6,3,3,30,50,1);  -- CS106

-- Student 2 (Priya Sharma) - Sem 1 (top scorer)
INSERT INTO marks (student_id, course_id, session_id, exam_type_id, marks_obtained, max_marks, entered_by) VALUES
(2,1,3,1,20,20,1),(2,1,3,2,30,30,1),(2,1,3,3,48,50,1),
(2,2,3,1,19,20,1),(2,2,3,2,28,30,1),(2,2,3,3,46,50,1),
(2,3,3,1,20,20,1),(2,3,3,2,29,30,1),(2,3,3,3,49,50,1),
(2,4,3,1,18,20,1),(2,4,3,2,27,30,1),(2,4,3,3,44,50,1),
(2,5,3,1,19,20,1),(2,5,3,2,28,30,1),(2,5,3,3,47,50,1),
(2,6,3,1,17,20,1),(2,6,3,2,26,30,1),(2,6,3,3,40,50,1);

-- Student 3 (Amit Singh) - Sem 1 (average)
INSERT INTO marks (student_id, course_id, session_id, exam_type_id, marks_obtained, max_marks, entered_by) VALUES
(3,1,3,1,14,20,1),(3,1,3,2,20,30,1),(3,1,3,3,35,50,1),
(3,2,3,1,13,20,1),(3,2,3,2,19,30,1),(3,2,3,3,33,50,1),
(3,3,3,1,15,20,1),(3,3,3,2,22,30,1),(3,3,3,3,36,50,1),
(3,4,3,1,14,20,1),(3,4,3,2,20,30,1),(3,4,3,3,32,50,1),
(3,5,3,1,12,20,1),(3,5,3,2,18,30,1),(3,5,3,3,30,50,1),
(3,6,3,1,11,20,1),(3,6,3,2,17,30,1),(3,6,3,3,28,50,1);

-- Attendance data
INSERT INTO attendance (student_id, course_id, session_id, total_classes, attended) VALUES
(1,1,3,40,38),(1,2,3,40,36),(1,3,3,40,40),(1,4,3,30,28),(1,5,3,35,30),(1,6,3,25,22),
(2,1,3,40,40),(2,2,3,40,39),(2,3,3,40,40),(2,4,3,30,30),(2,5,3,35,35),(2,6,3,25,24),
(3,1,3,40,32),(3,2,3,40,30),(3,3,3,40,35),(3,4,3,30,25),(3,5,3,35,28),(3,6,3,25,20);

-- Semester results (computed)
INSERT INTO semester_results (student_id, session_id, semester, total_marks, max_marks, percentage, sgpa, cgpa, grade_letter, result_status, back_subjects, published) VALUES
(1, 3, 1, 451, 550, 82.0, 8.2, 8.2, 'A+', 'Pass', 0, 1),
(2, 3, 1, 540, 550, 98.2, 9.8, 9.8, 'O',  'Pass', 0, 1),
(3, 3, 1, 383, 550, 69.6, 6.8, 6.8, 'B+', 'Pass', 0, 1);
