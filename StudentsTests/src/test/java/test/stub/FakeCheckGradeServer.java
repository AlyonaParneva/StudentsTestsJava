package test.stub;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class FakeCheckGradeServer {
    private HttpServer server;

    public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(5352), 0);
        server.createContext("/checkGrade", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            int grade = Integer.parseInt(query.split("=")[1]);

            boolean isValid = grade >= 2 && grade <= 5;
            String response = Boolean.toString(isValid);

            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
