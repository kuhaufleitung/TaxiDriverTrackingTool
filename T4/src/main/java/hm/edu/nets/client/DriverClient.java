package hm.edu.nets.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hm.edu.nets.Status;
import hm.edu.nets.URI_Addresses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;


public class DriverClient {
    private final String driverID;
    private String JSONRouteResponse;
    private ZonedDateTime departureAt;
    private ZonedDateTime arrivalAt;

    public DriverClient(String driverID) {
        this.driverID = driverID;
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("""
                                        
                    (1):     start new route
                    (2):     output current information
                    (3):     Status: Delay
                    (4):     Status: Available
                    (5):     Status: Taking a break
                    (6):     Exit Client
                    """);
            System.out.print("Selection: ");
            int selection = in.nextInt();
            in.nextLine();

            switch (selection) {
                case 1 -> {
                    System.out.print("From: ");
                    String startLoc = in.nextLine();
                    System.out.println();
                    System.out.print("To: ");
                    String endLoc = in.nextLine();
                    startRoute(startLoc, endLoc);
                    parseRouteResponse();
                }
                case 2 -> {
                    JSONRouteResponse = sendGETRequest();
                    parseRouteResponse();
                    displayInformation();
                }
                case 3 -> {
                    delay();
                    System.out.println("Delay transmitted.");
                }
                case 4 -> {
                    available();
                    System.out.println("Now Available.");
                }
                case 5 -> {
                    pause();
                    System.out.println("Go eat a burger.");
                }
                case 6 -> System.exit(0);
                case default -> System.out.println("Incorrect input!");
            }
        }
    }

    private void startRoute(String departLoc, String arrivalLoc) {
        //TODO: evtl 200 return
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("departure", departLoc).put("arrival", arrivalLoc);
        JSONRouteResponse = sendPUTRequest(body, "route");
    }

    private void delay() {
        setStatus(Status.DELAY);
    }

    private void pause() {
        setStatus(Status.ON_BREAK);
    }

    private void available() {
        setStatus(Status.AVAILABLE);
    }

    private String sendGETRequest() {
        try {
            URL specific = new URL(URI_Addresses.ServerURI + "/" + driverID);
            HttpURLConnection con = (HttpURLConnection) specific.openConnection();
            BufferedReader br;
            if (100 <= con.getResponseCode() && con.getResponseCode() <= 399) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        } catch (IOException e) {
            System.err.println("Exception!: " + e);
        }
        return null;
    }

    private String sendPUTRequest(ObjectNode body, String action) {
        URL specific;
        String buildURL = switch (action) {
            case "set" -> URI_Addresses.ServerURI + "/" + driverID + "/status";
            case "route" -> URI_Addresses.ServerURI + "/" + driverID + "/route";
            case default -> null;
        };
        try {
            assert buildURL != null;
            specific = new URL(buildURL);
            HttpURLConnection con = (HttpURLConnection) specific.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            osw.write(body.toString());
            osw.flush();
            os.close();
            System.out.println(con.getResponseCode());
            return sendGETRequest();
        } catch (IOException ex) {
            System.err.println("I/O Error. " + ex);
        }
        return null;
    }

    private void setStatus(Status status) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("status", status.toString());
        sendPUTRequest(body, "set");

    }

    private void parseRouteResponse() {
        ObjectMapper mapper = new ObjectMapper();
        //TODO: Eval if Formatter necessary
        //final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
        ObjectNode response;
        try {
                response = mapper.readValue(JSONRouteResponse, new TypeReference<>() {
                });
                if (response.findValue("departureAt").asText().equals("0") || response.findValue("arrivalAt").asText().equals("0")) {
                    return;
                }
                departureAt = ZonedDateTime.parse(response.findValue("departureAt").asText());
                arrivalAt = ZonedDateTime.parse(response.findValue("arrivalAt").asText());
        } catch (JsonProcessingException e) {
            System.err.println("Couldnt find departureAt or arrivalAt entries! " + e);
        }
    }

    private void displayInformation() {
        if (JSONRouteResponse.contains("NOT_INIT")) {
            System.out.println("No route set!");
            return;
        }
        ChronoUnit unit = ChronoUnit.MINUTES;
        ZonedDateTime now = ZonedDateTime.now();

        //verstrichene Zeit
        long timeDrivenAlready = Math.abs(unit.between(now, departureAt));
        //TTG
        long timeToGo = Math.abs(unit.between(arrivalAt, now));


        //Ankunftszeit
        int arrivalHour = arrivalAt.getHour();
        int arrivalMinute = arrivalAt.getMinute();

        System.out.println("Time passed since start: " + timeDrivenAlready + "min");
        System.out.println("Time until arrival: " + timeToGo + "min" + " (including 5min buffer)");
        System.out.println("Clock at arrival: " + arrivalHour + ":" + arrivalMinute + " eventual 5mins on top");

    }
}
