package test;

import main.Student;
import test.utils.TestData;

public class BaseTest {
    protected Student createDefaultStudent() {
        Student student = new Student(TestData.STUDENT_NAME);
        for (int grade : TestData.VALID_GRADES) {
            student.addGrade(grade);
        }
        return student;
    }
}
