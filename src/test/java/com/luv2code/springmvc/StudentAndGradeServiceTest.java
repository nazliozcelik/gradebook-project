package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    public void setupDatabase(){
        jdbc.execute("insert into student(id, firstname, lastname, email_address)" + "values(1, 'Eric', 'Roby', 'eric.roby@test.com')");
        jdbc.execute("insert into math_grade(id, student_id, grade) values(1,1,100.00)");
        jdbc.execute("insert into science_grade(id, student_id,grade) values(1,1,100.00)");
        jdbc.execute("insert into history_grade(id, student_id, grade) values(1,1,100.00)");
    }

    @Test
    public void createStudentService(){
        studentService.createStudent("Chad", "Darby", "chad.darby@test.com");
        CollegeStudent student = studentDao.findByEmailAddress("chad.darby@test.com");
        assertEquals("chad.darby@test.com", student.getEmailAddress());
    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService(){
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(), "Return true");

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(), "Return False");
    }

    @Test
    @Sql("/insertData.sql")
    public void getGradebookService(){
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents){
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService(){
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(92.50, 1, "science"));
        assertTrue(studentService.createGrade(72.50, 1, "history"));

        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student has math grades");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student has science grades");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has history grades");
    }

    @Test
    public void createGradeServiceReturnFalse(){
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-5, 1, "math"));
        assertFalse(studentService.createGrade(92.50, 2, "math"));
        assertFalse(studentService.createGrade(92.50, 1,"literature"));
    }

    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute("DELETE FROM student");
        jdbc.execute("DELETE FROM math_grade");
        jdbc.execute("DELETE FROM science_grade");
        jdbc.execute("DELETE FROM history_grade");
    }
}
