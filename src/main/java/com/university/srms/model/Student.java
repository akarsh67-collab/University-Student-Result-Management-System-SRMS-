package com.university.srms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Student {

    private int studentId;
    private String enrollmentNo;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private int programId;
    private String programCode;
    private String programName;
    private String deptName;
    private int admissionYear;
    private int currentSemester;
    private String status;
    private String guardianName;
    private String guardianPhone;
    private String address;
    private String photoPath;
    private LocalDateTime createdAt;

    public Student() {}

    // ---- Getters & Setters ----

    public int getStudentId()                      { return studentId; }
    public void setStudentId(int studentId)         { this.studentId = studentId; }

    public String getEnrollmentNo()                 { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo){ this.enrollmentNo = enrollmentNo; }

    public String getFirstName()                    { return firstName; }
    public void setFirstName(String firstName)      { this.firstName = firstName; }

    public String getLastName()                     { return lastName; }
    public void setLastName(String lastName)        { this.lastName = lastName; }

    public String getFullName() {
        if (fullName != null) return fullName;
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    public void setFullName(String fullName)        { this.fullName = fullName; }

    public String getEmail()                        { return email; }
    public void setEmail(String email)              { this.email = email; }

    public String getPhone()                        { return phone; }
    public void setPhone(String phone)              { this.phone = phone; }

    public LocalDate getDateOfBirth()               { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth){ this.dateOfBirth = dateOfBirth; }

    public String getGender()                       { return gender; }
    public void setGender(String gender)            { this.gender = gender; }

    public int getProgramId()                       { return programId; }
    public void setProgramId(int programId)         { this.programId = programId; }

    public String getProgramCode()                  { return programCode; }
    public void setProgramCode(String programCode)  { this.programCode = programCode; }

    public String getProgramName()                  { return programName; }
    public void setProgramName(String programName)  { this.programName = programName; }

    public String getDeptName()                     { return deptName; }
    public void setDeptName(String deptName)        { this.deptName = deptName; }

    public int getAdmissionYear()                   { return admissionYear; }
    public void setAdmissionYear(int admissionYear) { this.admissionYear = admissionYear; }

    public int getCurrentSemester()                 { return currentSemester; }
    public void setCurrentSemester(int s)           { this.currentSemester = s; }

    public String getStatus()                       { return status; }
    public void setStatus(String status)            { this.status = status; }

    public String getGuardianName()                 { return guardianName; }
    public void setGuardianName(String guardianName){ this.guardianName = guardianName; }

    public String getGuardianPhone()                { return guardianPhone; }
    public void setGuardianPhone(String p)          { this.guardianPhone = p; }

    public String getAddress()                      { return address; }
    public void setAddress(String address)          { this.address = address; }

    public String getPhotoPath()                    { return photoPath; }
    public void setPhotoPath(String photoPath)      { this.photoPath = photoPath; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime c)       { this.createdAt = c; }

    @Override
    public String toString() {
        return "Student{id=" + studentId + ", enrollment=" + enrollmentNo +
               ", name=" + getFullName() + ", semester=" + currentSemester + "}";
    }
}
