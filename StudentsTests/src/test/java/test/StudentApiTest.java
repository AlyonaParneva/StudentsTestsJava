package test;

import com.fasterxml.jackson.databind.ObjectMapper;

import main.StudentApi;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.utils.TestData.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentApiTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CloseableHttpClient client = HttpClients.createDefault();

    @AfterEach
    void cleanUp() throws IOException {
        for (int id = 1; id <= 100; id++) {
            client.execute(new HttpDelete(BASE_URL + STUDENT_ID_URL + id)).close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("GET /student/{id} — код 404, если не найден")
    void testGetStudentNotFound() throws IOException {
        HttpGet request = new HttpGet(BASE_URL + STUDENT_ID_URL+"9999");
        try (CloseableHttpResponse response = client.execute(request)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test @Order(2)
    @DisplayName("POST /student — добавление нового, имя заполнено, id есть")
    void testPostNewStudentWithId() throws IOException {
        HttpPost request = new HttpPost(BASE_URL + STUDENT_URL);
        request.setEntity(new StringEntity(jsonBody(1, STUDENT_NAME, List.of(5)), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = client.execute(request)) {
            assertEquals(201, response.getStatusLine().getStatusCode());
        }
    }

    @Test @Order(3)
    @DisplayName("GET /student/{id} — найден, имя и оценки есть")
    void testGetStudentSuccess() throws IOException {
        HttpPost postRequest = new HttpPost(BASE_URL + STUDENT_URL);
        postRequest.setEntity(new StringEntity(jsonBody(1, STUDENT_NAME, List.of(5)), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse postResponse = client.execute(postRequest)) {
            assertEquals(201, postResponse.getStatusLine().getStatusCode());
        }

        HttpGet getRequest = new HttpGet(BASE_URL + STUDENT_ID_URL+"1");
        try (CloseableHttpResponse getResponse = client.execute(getRequest)) {
            assertEquals(200, getResponse.getStatusLine().getStatusCode());

            StudentApi student = mapper.readValue(getResponse.getEntity().getContent(), StudentApi.class);
            assertEquals(STUDENT_NAME, student.getName());
            assertTrue(student.getGrades().contains(5));
        }
    }

    @Test @Order(4)
    @DisplayName("POST /student — обновление существующего по id")
    void testPostUpdateStudent() throws IOException {
        HttpPost create = new HttpPost(BASE_URL + STUDENT_URL);
        create.setEntity(new StringEntity(jsonBody(1, STUDENT_NAME, List.of(5)), ContentType.APPLICATION_JSON));
        client.execute(create).close();

        HttpPost update = new HttpPost(BASE_URL + STUDENT_URL);
        update.setEntity(new StringEntity(jsonBody(1, NEW_STUDENT_NAME, List.of(5, 5)), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = client.execute(update)) {
            assertEquals(201, response.getStatusLine().getStatusCode());}
    }

    @Test @Order(5)
    @DisplayName("POST /student — добавление без id, возвращает назначенный id")
    void testPostStudentWithoutId() throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put(API_ID_STUDENT_TEXT, null);
        body.put(API_NAME_STUDENT_TEXT, STUDENT_NAME);
        body.put(API_MARKS_STUDENT_TEXT, List.of(4, 5));
        HttpPost request = new HttpPost(BASE_URL + STUDENT_URL);
        request.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = client.execute(request)) {
            assertEquals(201, response.getStatusLine().getStatusCode());

            String raw = new String(response.getEntity().getContent().readAllBytes());
            int id = Integer.parseInt(raw.trim());
            assertTrue(id > 0);
        }
    }

    @Test @Order(6)
    @DisplayName("POST /student — имя не заполнено, код 400")
    void testPostStudentNoName() throws IOException {
        Map<String, Object> body = Map.of("Id", 999,  "marks", List.of(3));
        HttpPost request = new HttpPost(BASE_URL + STUDENT_URL);
        request.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = client.execute(request)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test @Order(7)
    @DisplayName("DELETE /student/{id} — удаление успешно")
    void testDeleteStudent() throws IOException {
        HttpPost postRequest = new HttpPost(BASE_URL + STUDENT_URL);
        postRequest.setEntity(new StringEntity(jsonBody(1, STUDENT_NAME, List.of(5, 5)), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse postResponse = client.execute(postRequest)) {
            assertEquals(201, postResponse.getStatusLine().getStatusCode());
        }

        HttpDelete request = new HttpDelete(BASE_URL + STUDENT_ID_URL+"1");
        try (CloseableHttpResponse response = client.execute(request)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test @Order(8)
    @DisplayName("DELETE /student/{id} — не найден, код 404")
    void testDeleteStudentNotFound() throws IOException {
        HttpDelete request = new HttpDelete(BASE_URL + STUDENT_ID_URL+"9999");
        try (CloseableHttpResponse response = client.execute(request)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    @Order(9)
    @DisplayName("GET /topStudent — никого нет, пусто")
    void testTopStudentEmptyDB() throws IOException {
        HttpGet request = new HttpGet(BASE_URL + TOP_STUDENT_URL);
        try (CloseableHttpResponse response = client.execute(request)) {
            String entityContent = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertTrue(entityContent.isEmpty() || entityContent.equals("{}") || entityContent.equals("null"));
        }
    }


    @Test @Order(10)
    @DisplayName("GET /topStudent — студенты есть, но без оценок")
    void testTopStudentNoMarks() throws IOException {
        HttpPost request = new HttpPost(BASE_URL + STUDENT_URL);
        request.setEntity(new StringEntity(jsonBody(10, STUDENT_NAME, List.of()), ContentType.APPLICATION_JSON));
        client.execute(request).close();

        HttpGet top = new HttpGet(BASE_URL + TOP_STUDENT_URL);
        try (CloseableHttpResponse response = client.execute(top)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals(0, response.getEntity().getContent().readAllBytes().length);
        }
    }

    @Test @Order(11)
    @DisplayName("GET /topStudent — один лучший студент")
    void testTopStudentOneBest() throws IOException {
        HttpPost request = new HttpPost(BASE_URL + STUDENT_URL);
        request.setEntity(new StringEntity(jsonBody(20, TOPPER_STUDENT, List.of(5, 5, 5)), ContentType.APPLICATION_JSON));
        client.execute(request).close();

        HttpGet top = new HttpGet(BASE_URL + TOP_STUDENT_URL);
        try (CloseableHttpResponse response = client.execute(top)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String json = new String(response.getEntity().getContent().readAllBytes());
            assertTrue(json.contains(TOPPER_STUDENT));
        }
    }

    @Test @Order(12)
    @DisplayName("GET /topStudent — несколько студентов с одинаковой средней и количеством")
    void testTopStudentMultiple() throws IOException {
        HttpPost p1 = new HttpPost(BASE_URL + STUDENT_URL);
        HttpPost p2 = new HttpPost(BASE_URL + STUDENT_URL);
        p1.setEntity(new StringEntity(jsonBody(30, STUDENT_NAME, List.of(4, 5)), ContentType.APPLICATION_JSON));
        p2.setEntity(new StringEntity(jsonBody(31, NEW_STUDENT_NAME, List.of(5, 4)), ContentType.APPLICATION_JSON));
        client.execute(p1).close();
        client.execute(p2).close();

        HttpGet top = new HttpGet(BASE_URL + TOP_STUDENT_URL);
        try (CloseableHttpResponse response = client.execute(top)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String json = new String(response.getEntity().getContent().readAllBytes());
            assertTrue(json.contains(STUDENT_NAME) && json.contains(NEW_STUDENT_NAME));
        }
    }
}