package test;

import main.StudentCheckGrade;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import test.stub.FakeCheckGradeServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static test.utils.TestData.STUDENT_NAME;

class StudentTestWithStub {
    static FakeCheckGradeServer stubServer;

    @BeforeAll
    static void setUp() throws Exception {
        stubServer = new FakeCheckGradeServer();
        stubServer.start();
    }

    @AfterAll
    static void tearDown() {
        stubServer.stop();
    }

    @Test
    void testAddValidGrade() {
        StudentCheckGrade student = new StudentCheckGrade(STUDENT_NAME);
        student.addGrade(4);
        assertEquals(List.of(4), student.getGrades());
    }

    @Test
    void testAddInvalidGradeThrows() {
        StudentCheckGrade student = new StudentCheckGrade(STUDENT_NAME);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> student.addGrade(10));
        assertTrue(ex.getMessage().contains("is wrong grade"));
    }
}
