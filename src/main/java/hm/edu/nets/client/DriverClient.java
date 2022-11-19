package hm.edu.nets.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hm.edu.nets.URI_Addresses;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class DriverClient {
    //ONLY REQUESTS HERE!
    //TODO: can query status(avail, pause, with guest, with guest and delay)
    private String driverID;

    public DriverClient(String driverID) {
        this.driverID = driverID;
        startRoute("Munich, Theresienstraße", "Starnberg, Hauptstraße");
        //getInformation();
    }

    private void startRoute(String departLoc, String arrivalLoc) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();
        body.put("depart", departLoc).put("arrival", arrivalLoc);

        URL specific;
        try {
            specific = new URL(URI_Addresses.ServerURI.toString() + "/" + driverID);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) specific.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            //osw.write(JSONObject.valueToString(body));
            osw.write(body.toString());
            osw.flush();
            os.close();
            System.out.println(con.getResponseCode());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void delay() {

    }

    private void setPause() {

    }

    private void setAvailable() {

    }

    private void getInformation() {

    }
}
