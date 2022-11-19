package hm.edu.nets;

import java.net.http.HttpClient;

public class TotalTaxiTrackingToolServer {

    public TotalTaxiTrackingToolServer() {
        HttpClient MapsClient = HttpClient.newHttpClient();

        for (int i = 1; i <= 3; i++) {
            System.out.println("[HUE_RESPONSE]: " + RestCommunication.sendAndGetResponse(MapsClient, LightStateCommands.initLight(i)));
        }

        while (true) {

        }
    }
}
