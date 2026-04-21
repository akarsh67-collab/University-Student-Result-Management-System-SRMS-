# University Student Result Management System (SRMS)
## Java + MySQL | Senior Developer Grade

---

## Project Structure

```
university-srms/
├── pom.xml                              ← Maven build file
├── sql/
│   ├── 01_schema.sql                    ← Full database schema (14 tables)
│   ├── 02_seed_data.sql                 ← Sample data (students, marks, results)
│   └── 03_views_procedures.sql         ← Views & stored procedures
├── src/main/
│   ├── java/com/university/srms/
│   │   ├── controller/
│   │   │   └── ApiServlet.java          ← REST API (all endpoints)
│   │   ├── dao/
│   │   │   ├── StudentDAO.java          ← Student DB operations
│   │   │   └── MarksDAO.java           ← Marks & results DB operations
│   │   ├── model/
│   │   │   ├── Student.java            ← Student entity
│   │   │   ├── Mark.java               ← Mark entity
│   │   │   └── Course.java             ← Course, SemesterResult, etc.
│   │   └── util/
│   │       ├── DBConnection.java       ← HikariCP connection pool
│   │       └── AppInitializer.java     ← Servlet context lifecycle
│   ├── resources/
│   │   └── db.properties               ← Database credentials
│   └── webapp/
│       └── index.html                  ← Complete frontend dashboard
└── README.md
```

---

## Prerequisites

| Tool        | Version Required |
|-------------|-----------------|
| Java (JDK)  | 17 or higher    |
| Maven       | 3.8+            |
| MySQL       | 8.0+            |
| Apache Tomcat | 10.1+         |

---

## Step-by-Step Setup

### Step 1 — Create the Database

Open MySQL Workbench or MySQL CLI and run all 3 SQL files in order:

```sql
mysql -u root -p < sql/01_schema.sql
mysql -u root -p < sql/02_seed_data.sql
mysql -u root -p < sql/03_views_procedures.sql
```

Or paste and run each file manually in MySQL Workbench.

**What gets created:**
- `university_srms` database
- 14 tables: departments, programs, students, faculty, courses, marks,
  semester_results, attendance, users, grade_scale, exam_types,
  academic_sessions, course_assignments
- 4 views + 2 stored procedures
- Sample data: 10 students, 18 courses, 5 faculty, marks for 3 students

---

### Step 2 — Configure Database Connection

Edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/university_srms?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

---

### Step 3 — Build the Project

```bash
cd university-srms
mvn clean package -DskipTests
```

This creates `target/srms.war`

---

### Step 4 — Deploy to Tomcat

1. Copy `target/srms.war` to `{TOMCAT_HOME}/webapps/`
2. Start Tomcat: `{TOMCAT_HOME}/bin/startup.sh` (Linux/Mac) or `startup.bat` (Windows)
3. Open browser: **http://localhost:8080/srms/**

---

## API Reference

Base URL: `http://localhost:8080/srms/api`

| Method | Endpoint                          | Description               |
|--------|-----------------------------------|---------------------------|
| GET    | `/students`                       | List all students         |
| GET    | `/students?id=1`                  | Get student by ID         |
| GET    | `/students?enrollment=2023CSE001` | Get by enrollment number  |
| GET    | `/students?search=rahul`          | Search students           |
| POST   | `/students`                       | Create new student        |
| PUT    | `/students`                       | Update student            |
| GET    | `/marks?studentId=1&sessionId=3`  | Get marks for student     |
| GET    | `/marks?studentId=1&sessionId=3&type=summary` | Marks summary  |
| POST   | `/marks`                          | Save/update a mark        |
| POST   | `/marks/calculate`                | Calculate semester result |
| POST   | `/marks/publish`                  | Publish result            |
| GET    | `/report?studentId=1&semester=1`  | Get report card data      |
| GET    | `/dashboard`                      | Dashboard statistics      |
| GET    | `/courses?programId=1&semester=1` | List courses              |
| GET    | `/sessions`                       | List academic sessions    |

---

## Sample API Calls

**Add a student:**
```json
POST /api/students
{
  "enrollmentNo": "2024CSE011",
  "firstName": "Neha",
  "lastName": "Agarwal",
  "email": "neha.agarwal@student.edu",
  "phone": "9999888777",
  "gender": "Female",
  "programId": 1,
  "admissionYear": 2024,
  "currentSemester": 1
}
```

**Enter a mark:**
```json
POST /api/marks
{
  "studentId": 1,
  "courseId": 1,
  "sessionId": 5,
  "examTypeId": 3,
  "marksObtained": 45,
  "maxMarks": 50,
  "isAbsent": false,
  "enteredBy": 1
}
```

**Calculate semester result:**
```json
POST /api/marks/calculate
{
  "studentId": 1,
  "sessionId": 3,
  "semester": 1
}
```

---

## Default Login Credentials

| Username    | Password      | Role    |
|-------------|---------------|---------|
| admin       | Admin@123     | Admin   |
| fac001      | Faculty@123   | Faculty |
| 2023CSE001  | Student@123   | Student |

*(Passwords are BCrypt hashed in the database)*

---

## Grade Scale (10-point system)

| Grade | Range  | Points | Description  |
|-------|--------|--------|--------------|
| O     | 90-100 | 10.0   | Outstanding  |
| A+    | 80-89  | 9.0    | Excellent    |
| A     | 70-79  | 8.0    | Very Good    |
| B+    | 60-69  | 7.0    | Good         |
| B     | 50-59  | 6.0    | Above Average|
| C     | 45-49  | 5.0    | Average      |
| P     | 40-44  | 4.0    | Pass         |
| F     | 0-39   | 0.0    | Fail         |

---

## Key Design Decisions

1. **HikariCP connection pool** — handles 20 concurrent connections efficiently
2. **DAO pattern** — clean separation of DB logic from controllers
3. **Stored procedure** for grade calculation — runs in DB, faster and consistent
4. **Views** for complex joins — `v_student_marks_summary`, `v_report_card`, `v_class_toppers`
5. **Upsert marks** — `ON DUPLICATE KEY UPDATE` prevents double-entry errors
6. **Soft delete** — students are "Dropped" not deleted, preserving data integrity
7. **UNIQUE constraints** — prevent duplicate marks entries per student/course/exam
8. **Batch insert** for marks — saves entire class's marks in one transaction

---

## Extending the Project

- **Add Spring Boot**: Replace servlet with Spring MVC controllers
- **Add JWT auth**: Secure API endpoints per role
- **Add iText PDF**: Generate printable PDF report cards
- **Add email**: Send results to students automatically
- **Add Excel export**: Use Apache POI for marks sheets
- **Frontend SPA**: Replace HTML with React or Angular
