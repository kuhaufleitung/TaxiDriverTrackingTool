package hm.edu.nets.client;

import java.net.URI;
import java.net.http.HttpClient;

public class DriverClient {
    //TODO: connect to server, make request
    //TODO: can query status(avail, pause, with guest, with guest and delay)
    HttpClient client = HttpClient.newHttpClient();
    URI hostURI = URI.create("http://localhost:80/driver");
    private String driverID;

    public DriverClient(String driverID) {
        this.driverID = driverID;
    }

    private void startRoute() {

    }

    private void delay() {

    }

    private void setPause() {

    }

    private void setAvailable() {

    }
}
