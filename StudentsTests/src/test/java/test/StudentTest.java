package test;


import main.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test.utils.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentTest extends BaseTest {

    @Test
    @DisplayName("Должен возвращать корректное имя студента")
    public void testGetName() {
        Student student = createDefaultStudent();
        assertEquals(TestData.STUDENT_NAME, student.getName());
    }

    @Test
    @DisplayName("Должен устанавливать новое имя студента")
    public void testSetName() {
        Student student = createDefaultStudent();
        student.setName(TestData.NEW_STUDENT_NAME);
        assertEquals(TestData.NEW_STUDENT_NAME, student.getName());
    }

    @Test
    @DisplayName("Должен добавлять только допустимые оценки")
    public void testAddGradeValid() {
        Student student = new Student(TestData.TEMP_STUDENT_NAME);
        for (int grade : TestData.VALID_GRADES) {
            student.addGrade(grade);
        }
        assertEquals(TestData.VALID_GRADES, student.getGrades());
    }

    @Test
    @DisplayName("Должен выбрасывать исключение при добавлении недопустимых оценок")
    public void testAddGradeInvalid() {
        Student student = new Student(TestData.INVALID_STUDENT_NAME);
        for (int invalid : TestData.INVALID_GRADES) {
            Exception ex = assertThrows(IllegalArgumentException.class, () -> student.addGrade(invalid));
            assertTrue(ex.getMessage().contains(TestData.ERROR_MESSAGE_PART));
        }
    }

    @Test
    @DisplayName("Список оценок должен быть защищён от внешнего изменения")
    public void testGradesEncapsulation() {
        Student student = createDefaultStudent();
        List<Integer> externalGrades = student.getGrades();
        externalGrades.clear();

        assertEquals(TestData.VALID_GRADES, student.getGrades());
    }

    @Test
    @DisplayName("Методы equals и hashCode должны работать корректно для одинаковых студентов")
    public void testEqualsAndHashCode() {
        Student s1 = createDefaultStudent();
        Student s2 = createDefaultStudent();

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    @DisplayName("Метод toString должен содержать имя и оценки студента")
    public void testToStringContainsNameAndGrades() {
        Student student = createDefaultStudent();
        String output = student.toString();
        assertTrue(output.contains(TestData.STUDENT_NAME));
        assertTrue(output.contains("4") || output.contains("5"));
    }

    @Test
    @DisplayName("Метод equals должен возвращать false при сравнении с null")
    void testEqualsWithNull() {
        Student student = createDefaultStudent();
        assertNotEquals(null, student);
    }

    @Test
    @DisplayName("Метод equals должен возвращать false при сравнении с объектом другого класса")
    void testEqualsWithDifferentClass() {
        Student student = createDefaultStudent();
        assertNotEquals("Some String", student);
    }

    @Test
    @DisplayName("Метод equals должен возвращать false при разных именах студентов")
    void testEqualsWithDifferentName() {
        Student s1 = createDefaultStudent();
        Student s2 = new Student("Another");
        for (int grade : TestData.VALID_GRADES) {
            s2.addGrade(grade);
        }
        assertNotEquals(s1, s2);
    }

    @Test
    @DisplayName("Метод equals должен возвращать false при разных оценках")
    void testEqualsWithDifferentGrades() {
        Student s1 = createDefaultStudent();
        Student s2 = new Student(TestData.STUDENT_NAME);
        s2.addGrade(2);
        assertNotEquals(s1, s2);
    }
}
