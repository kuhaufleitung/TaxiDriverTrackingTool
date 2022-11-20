package hm.edu.nets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;

public class DriverRoute {
    private final Driver driver;
    private final String route;
    private ZonedDateTime departure;
    private ZonedDateTime arrival;
    private static final String API_KEY = "wiMfx6EU3WKC_1nwth9MxE8Sgh1DcvRcj9uR76T5h3E";

    public DriverRoute(Driver driver, String departureLocation, String arrivalLocation) {
        this.driver = driver;
        driver.setStatus(Status.DRIVING);
        this.route = queryRoute(departureLocation, arrivalLocation);
    }

    private String queryRoute(String departureLocation, String arrivalLocation) {
        HashMap<String, String> startCoordinates = getCoordinates(departureLocation);
        HashMap<String, String> endCoordinates = getCoordinates(arrivalLocation);
        String query = URI_Addresses.HERE_Routing
                + "?transportMode=car&origin="
                + startCoordinates.get("lat")
                + ","
                + startCoordinates.get("lng")
                + "&destination="
                + endCoordinates.get("lat")
                + ","
                + endCoordinates.get("lng")
                + "&apiKey="
                + API_KEY;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(query)).GET().build();
        return Objects.requireNonNull(RestCommunication.sendAndGetResponse(request)).body();

    }

    private HashMap<String, String> getCoordinates(String location) {
        location = location.replace(" ", "+");
        String query = URI_Addresses.HERE_Geocode + "?q=" + location + "&apiKey=" + API_KEY;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(query)).GET().build();
            HttpResponse<String> response = RestCommunication.sendAndGetResponse(request);
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> hmap = new HashMap<>();
            assert response != null;
            ObjectNode json = mapper.readValue(response.body(), new TypeReference<>() {
            });
            hmap.put("lat", json.findValue("lat").asText());
            hmap.put("lng", json.findValue("lng").asText());
            return hmap;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRoute() {
        return route;
    }

    public void lateByXTime(String additionalTime) {
        driver.setStatus(Status.DELAY);
    }

    public ZonedDateTime readDepartureTimeFromJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json = mapper.readValue(route, new TypeReference<>() {});
            String departure = json.with("routes").get(0).with("sections").get(0).with("departure").get("time").asText();
            return ZonedDateTime.parse(departure);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ZonedDateTime readArrivalTimeFromJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json = mapper.readValue(route, new TypeReference<>() {});
            String departure = json.with("routes").get(0).with("sections").get(0).with("arrival").get("time").asText();
            return ZonedDateTime.parse(departure);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public int reportLocation() {
        return 0;
    }

    public void cancelRide() {

    }

    public void update() {

    }
}