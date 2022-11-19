package hm.edu.nets;

import java.net.http.HttpClient;

public class DriverRoute {
    private String currentPosition;
    private final Driver driver;
    private final HttpClient client;
    private final String API_KEY = "wiMfx6EU3WKC_1nwth9MxE8Sgh1DcvRcj9uR76T5h3E";

    public DriverRoute(HttpClient client, Driver driver, String departureLocation, String arrivalLocation) {
        this.client = client;
        this.driver = driver;
        driver.setStatus(Status.DRIVING);
        queryLocation(departureLocation, arrivalLocation);
        updateLights();
    }

    private void queryLocation(String departureLocation, String arrivalLocation) {

    }

    public void lateByXTime(String additionalTime) {
        driver.setStatus(Status.DELAY);

    }

    public int getTimeToArrival() {
        return 0; //Integer.parseInt(arrivalLocation) - Integer.parseInt(currentPosition);
    }

    public int reportLocation() {
        return 0;
    }

    public void cancelRide() {

    }

    public void update() {

    }

    private void updateLights() {
        switch (driver.getStatus()) {
            case AVAILABLE ->
                    RestCommunication.sendAndGetResponse(client, LightStateCommands.lightColor(driver.getDriverID(), HueColor.GREEN.toString()));
            case DRIVING ->
                    RestCommunication.sendAndGetResponse(client, LightStateCommands.lightColor(driver.getDriverID(), HueColor.YELLOW.toString()));
            case ON_BREAK ->
                    RestCommunication.sendAndGetResponse(client, LightStateCommands.lightOff(driver.getDriverID()));
            case DELAY ->
                    RestCommunication.sendAndGetResponse(client, LightStateCommands.lightBlinking(URI_Addresses.HueURI, driver.getDriverID()));
        }
    }
}