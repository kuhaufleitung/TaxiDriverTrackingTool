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
        JSONObject body = new JSONObject().put("on", false).put("alert", "none");
        return HttpRequest.newBuilder()
                .uri(URI.create(hostRequestPath))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightColor(int driverID, int color) {
        String hostRequestPath = URI_Addresses.HueURI +  "/" + driverID + "/state";
        JSONObject body = new JSONObject().put("hue", color).put("on", true).put("alert", "none");
        return HttpRequest.newBuilder()
                .uri(URI.create(hostRequestPath))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightBlinking(int driverID) {
        String hostRequestPath = URI_Addresses.HueURI +  "/" + driverID + "/state";
        JSONObject body = new JSONObject().put("alert", "select").put("on", true).put("hue", HueColor.RED);
        return HttpRequest.newBuilder()
                .uri(URI.create(hostRequestPath))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }
}
