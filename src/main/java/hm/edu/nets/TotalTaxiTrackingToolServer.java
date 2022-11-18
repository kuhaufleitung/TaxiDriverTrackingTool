package hm.edu.nets;

import java.net.http.HttpClient;

import static hm.edu.nets.URI_Addresses.HueURI;

public class TotalTaxiTrackingToolServer {

    public TotalTaxiTrackingToolServer() {
        HttpClient MapsClient = HttpClient.newHttpClient();

        for (int i = 1; i <= 3; i++) {
            System.out.println("[HUE_RESPONSE]: " + HueRestCommunication.sendAndGetResponse(MapsClient, LightStateCommands.initLight(HueURI, i)));
        }

        while (true) {

        }
    }
}
