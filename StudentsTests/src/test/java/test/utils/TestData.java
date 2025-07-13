package test.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestData {
    public static final String STUDENT_NAME = "Ivan";
    public static final String NEW_STUDENT_NAME = "Oleg";
    public static final String TEMP_STUDENT_NAME = "Test";
    public static final String INVALID_STUDENT_NAME = "Invalid";
    public static final String ERROR_MESSAGE_PART = "is wrong grade";
    public static final List<Integer> VALID_GRADES = List.of(4, 5);
    public static final List<Integer> INVALID_GRADES = List.of(1, 6);

    public static final String BASE_URL = "http://localhost:8080";
    public static final String STUDENT_ID_URL = "/student/";
    public static final String STUDENT_URL = "/student";
    public static final String TOP_STUDENT_URL = "/topStudent";
    public static final String TOPPER_STUDENT = "Topper";
    public static final String API_ID_STUDENT_TEXT = "id";
    public static final String API_NAME_STUDENT_TEXT = "name";
    public static final String API_MARKS_STUDENT_TEXT = "marks";

    public static String jsonBody(int id, String name, List<Integer> marks) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = new HashMap<>();
        body.put(API_ID_STUDENT_TEXT, id);
        body.put(API_NAME_STUDENT_TEXT, name);
        body.put(API_MARKS_STUDENT_TEXT, marks);
        return mapper.writeValueAsString(body);
    }
}
