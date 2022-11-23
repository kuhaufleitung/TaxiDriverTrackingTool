package hm.edu.nets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Objects;

public class DriverRoute {
    public final Driver driver;
    private final String route;
    private String arrivalLocation;
    private ZonedDateTime newLocationTime;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;
    private final JSONData data;
    public boolean isRouteActive;
    private static final String API_KEY = "wiMfx6EU3WKC_1nwth9MxE8Sgh1DcvRcj9uR76T5h3E";

    public DriverRoute(Driver driver, String departureLocation, String arrivalLocation, JSONData data) {
        this.driver = driver;
        this.data = data;
        this.arrivalLocation = arrivalLocation;
        driver.setStatus(Status.DRIVING);
        this.route = queryRouteFromAPI(departureLocation, arrivalLocation);
        departureTime = setDepartureTimeFromJSON();
        arrivalTime = setArrivalTimeFromJSON();
        isRouteActive = true;
        new Thread(this::updateCurrentDriveStatus).start();
    }

    private String queryRouteFromAPI(String departureLocation, String arrivalLocation) {
        HashMap<String, String> startCoordinates = getCoordinatesFromAPI(departureLocation);
        HashMap<String, String> endCoordinates = getCoordinatesFromAPI(arrivalLocation);
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

    private HashMap<String, String> getCoordinatesFromAPI(String location) {
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

    public ZonedDateTime setDepartureTimeFromJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json = mapper.readValue(route, new TypeReference<>() {
            });
            String departure = json.get("routes").get(0).get("sections").get(0).get("departure").get("time").asText();
            return ZonedDateTime.parse(departure);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ZonedDateTime setArrivalTimeFromJSON() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json = mapper.readValue(route, new TypeReference<>() {
            });
            String arrival = json.get("routes").get(0).get("sections").get(0).get("arrival").get("time").asText();
            return ZonedDateTime.parse(arrival);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void cancelRide() {
        isRouteActive = false;
        data.data.with(String.valueOf(driver.getDriverID())).put("departure", "None");
        data.data.with(String.valueOf(driver.getDriverID())).put("arrival", "None");
        data.data.with(String.valueOf(driver.getDriverID())).put("departureAt", 0);
        data.data.with(String.valueOf(driver.getDriverID())).put("arrivalAt", 0);
        data.data.with(String.valueOf(driver.getDriverID())).put("ttg", "0");
        driver.setStatus(Status.AVAILABLE);
    }

    //existing buffer of 5min
    public void updateCurrentDriveStatus() {
        ZonedDateTime safetyMarginAdded = arrivalTime.plus(5, ChronoUnit.MINUTES);
        while (driver.getStatus() == Status.DRIVING) {
            if (ZonedDateTime.now().isAfter(safetyMarginAdded)) {
                driver.setStatus(Status.AVAILABLE);
                isRouteActive = false;
                data.data.with(String.valueOf(driver.getDriverID())).put("departure", "None");
                data.data.with(String.valueOf(driver.getDriverID())).put("arrival", "None");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Adding 5min safety margin to ttg
    public long updateTTG(ZonedDateTime destLocTime) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime finish = destLocTime.plus(5, ChronoUnit.MINUTES);
        ChronoUnit unit = ChronoUnit.MINUTES;
        long ttg = unit.between(now, finish);
        data.data.with(String.valueOf(driver.getDriverID())).put("ttg", ttg);
        return ttg;
    }

    public void evaluateNewDriverLocation(String currentLocation) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String newRoute = queryRouteFromAPI(currentLocation, arrivalLocation);
            ObjectNode json = mapper.readValue(newRoute, new TypeReference<>() {
            });
            String arrival = json.get("routes").get(0).get("sections").get(0).get("arrival").get("time").asText();
            ZonedDateTime newArrivalTime = ZonedDateTime.parse(arrival);
            this.newLocationTime = newArrivalTime;
            ZonedDateTime arrivalWithSafetyMargin = arrivalTime.plus(5, ChronoUnit.MINUTES);
            if (newArrivalTime.isAfter(arrivalWithSafetyMargin)) {
                driver.setStatus(Status.DELAY);
                updateTTG(newArrivalTime);
                data.setNewArrivalTimeInJSON(String.valueOf(driver.getDriverID()), newArrivalTime);

            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public ZonedDateTime getNewLocationTime() {
        return newLocationTime;
    }
}