package hm.edu.nets.client;

import hm.edu.nets.RestCommunication;
import hm.edu.nets.URI_Addresses;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class DriverClient {
    //ONLY REQUESTS HERE!
    //TODO: can query status(avail, pause, with guest, with guest and delay)
    HttpClient client = HttpClient.newHttpClient();
    private String driverID;

    public DriverClient(String driverID) {
        this.driverID = driverID;
        //startRoute("Munich, Theresienstraße", "Starnberg, Hauptstraße");
        getInformation();
    }

    private void startRoute(String departLoc, String arrivalLoc) {
        JSONObject body = new JSONObject().put(driverID, new JSONObject().put("depart", departLoc).put("arrival", arrivalLoc));
        HttpRequest request = HttpRequest.newBuilder().uri(URI_Addresses.ServerURI).PUT(HttpRequest.BodyPublishers.ofString(body.toString())).build();
        String response = RestCommunication.sendAndGetResponse(client, request);
        System.out.println(response);
    }

    private void delay() {

    }

    private void setPause() {

    }

    private void setAvailable() {

    }

    private void getInformation() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI_Addresses.ServerURI).GET().build();
        String response = RestCommunication.sendAndGetResponse(client, request);
        System.out.println(response);

    }
}
