package hm.edu.nets;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HueRestCommunication {
    private HueRestCommunication() {
        throw new UnsupportedOperationException();
    }
    static String sendAndGetResponse(HttpClient client, HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            System.err.println("I/O Error!");
        } catch (InterruptedException e) {
            System.err.println("Something interrupted the ongoing operation.");
        }
        return null;
    }

}
