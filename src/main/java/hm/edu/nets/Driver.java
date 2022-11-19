package hm.edu.nets;

import java.net.http.HttpClient;

public class Driver {
    private Status status;
    private final int driverID;
    private final HttpClient client;

    public Driver(int driverID, HttpClient client) {
        this.client = client;
        status = Status.AVAILABLE;
        this.driverID = driverID;
        //new Thread(updateLights());
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public int getDriverID() {
        return driverID;
    }
/*
    private Runnable updateLights() {
        while(true) {
            switch (status) {
                case AVAILABLE ->
                        RestCommunication.sendAndGetResponse(client, LightStateCommands.lightColor(driverID, HueColor.GREEN.color));
                case DRIVING ->
                        RestCommunication.sendAndGetResponse(client, LightStateCommands.lightColor(driverID, HueColor.YELLOW.color));
                case ON_BREAK ->
                        RestCommunication.sendAndGetResponse(client, LightStateCommands.lightOff(driverID));
                case DELAY ->
                        RestCommunication.sendAndGetResponse(client, LightStateCommands.lightBlinking(URI_Addresses.HueURI, driverID));
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
 */
}
