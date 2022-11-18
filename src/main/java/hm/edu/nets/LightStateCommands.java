package hm.edu.nets;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;

public class LightStateCommands {

    private LightStateCommands() {
        throw new UnsupportedOperationException();
    }

    static HttpRequest initLight(URI host, int driverID) {
        JSONObject body = new JSONObject().put("sat", 254).put("bri", 254).put("hue", HueColor.GREEN);
        return HttpRequest.newBuilder()
                .uri(host.resolve("./" + driverID + "/state"))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightOff(URI host, int driverID) {
        JSONObject body = new JSONObject().put("bri", 0);
        return HttpRequest.newBuilder()
                .uri(host.resolve("./" + driverID + "/state"))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightColor(URI host, int driverID, HueColor color) {
        JSONObject body = new JSONObject().put("hue", color);
        return HttpRequest.newBuilder()
                .uri(host.resolve("./" + driverID + "/state"))
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    static HttpRequest lightBlinking(URI host, int driverID) {
        JSONObject bodyOn = new JSONObject().put("bri", 0);
        JSONObject bodyOff = new JSONObject().put("bri", 255);
        //TODO: fix infiniteLoop, doesnt work because it needs to be sent after every body. -> move to function call
        new Thread(() -> {
            boolean useOther = false;
            while (true) {
                if (useOther) {
                    HttpRequest.newBuilder()
                            .uri(host.resolve("./" + driverID + "/state"))
                            .PUT(HttpRequest.BodyPublishers.ofString(bodyOn.toString()))
                            .build();
                } else {
                    HttpRequest.newBuilder()
                            .uri(host.resolve("./" + driverID + "/state"))
                            .PUT(HttpRequest.BodyPublishers.ofString(bodyOff.toString()))
                            .build();
                }
                useOther = !useOther;
            }
        });
        return null;
    }
}
