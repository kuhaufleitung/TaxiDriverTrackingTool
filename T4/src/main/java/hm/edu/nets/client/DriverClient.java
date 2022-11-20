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
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class DriverClient {
    //ONLY REQUESTS HERE!
    private final String driverID;
    private String JSONRouteResponse;
    private ZonedDateTime departure;
    private ZonedDateTime rightNow;
    private ZonedDateTime arrival;

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
                    (6):     Exit CLient
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
                    System.out.println(startRoute(startLoc,endLoc));
                }
                case 2 -> getResponseFromServer();
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

    private String startRoute(String departLoc, String arrivalLoc) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("depart", departLoc).put("arrival", arrivalLoc);
        JSONRouteResponse = sendPUTRequest(body, "route");
        return JSONRouteResponse;

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

    //TODO: time driven already, ttg request. Ausgabe current time + ttg +5min
    private String getResponseFromServer() {
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
            //Output to user
            return sb.toString();
            //TODO: Ausgabe von Zeit: aktuelle Zeit, vorraussichtl. Fahrzeit, 5min reserve -> Ankunftszeit
        } catch (IOException e) {
            System.err.println("Exception!: " + e);
        }
        return null;
    }

    private void setStatus(Status status) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("status", status.toString());
        sendPUTRequest(body, "set");

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
            return getResponseFromServer();
        } catch (IOException ex) {
            System.err.println("I/O Error. " + ex);
        }
        return null;
    }
    private void parseRouteRespose() {
        if(JSONRouteResponse.isEmpty()) {
            System.out.println("Response is empty!");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
        ObjectNode response = null;
        try {
            response = mapper.readValue(JSONRouteResponse, new TypeReference<>(){});
            departure = ZonedDateTime.parse(response.findValue("departure").asText(), formatter);
            rightNow = ZonedDateTime.now();
            arrival = ZonedDateTime.parse(response.findValue("arrival").asText(), formatter);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
