package hm.edu.nets;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;

public class LightStateCommands {

    private LightStateCommands() {
        throw new UnsupportedOperationException();
    }

    static HttpRequest initLight(int driverID) {
        String hostRequestPath = URI_Addresses.HueURI +  "/" + driverID + "/state";
        JSONObject body = new JSONObject().put("sat", 254).put("bri", 254).put("hue", HueColor.GREEN.color);
        return HttpRequest.newBuilder()
                .uri(URI.create(hostRequestPath))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightOff(int driverID) {
        String hostRequestPath = URI_Addresses.HueURI +  "/" + driverID + "/state";
        JSONObject body = new JSONObject().put("bri", 0);
        return HttpRequest.newBuilder()
                .uri(URI.create(hostRequestPath))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightColor(int driverID, int color) {
        String hostRequestPath = URI_Addresses.HueURI +  "/" + driverID + "/state";
        JSONObject body = new JSONObject().put("hue", color);
        return HttpRequest.newBuilder()
                .uri(URI.create(hostRequestPath))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightBlinking(String host, int driverID) {
        JSONObject body = new JSONObject().put("alert", "select");
        return null;
    }
}
