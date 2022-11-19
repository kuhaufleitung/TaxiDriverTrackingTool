package hm.edu.nets;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestCommunication {
    private RestCommunication() {
        throw new UnsupportedOperationException();
    }
    public static void sendAndGetResponse(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            System.err.println("I/O Error!");
        } catch (InterruptedException e) {
            System.err.println("Something interrupted the ongoing operation.");
        }
    }
}
