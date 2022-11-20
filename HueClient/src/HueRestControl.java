import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HueRestControl {
    //Emulator
    URI hostURI = URI.create("http://localhost:80/api/newdeveloper/lights/");
    //Bridge1
    // URI hostURI = URI.create("http://10.28.9.120/api/197ea42c25303cef1a68c4042ed56887/lights/");
    //Bridge2
    // URI hostURI = URI.create("http://10.28.9.121/api/3dc1d8f23e55321f3c049c03ac88dff/lights/");
    //Bridge3
    // URI hostURI = URI.create("http://10.28.9.122/api/2217334838210e7f244460f83b42026f/lights/");
    //Bridge4
    // URI hostURI = URI.create("http://10.28.9.123/api/2b2d3ff23d63751f10c1d8c0332d50ff/lights/");
    private final int amountOfLights;
    final private String lightID;
    final private String hue;
    HttpClient client;

    public HueRestControl(String lightID, String programMode, boolean state, String hue) throws InterruptedException {
        this.lightID = lightID;
        this.hue = hue;
        client = HttpClient.newHttpClient();
        amountOfLights = new JSONObject(sendAndGetResponse(HttpRequest.newBuilder().uri(hostURI).GET().build())).length();

        Thread loop = null;
        switch (programMode) {
            case "fade" -> loop = new Thread(setFade());
            case "linear" -> loop = new Thread(setLinear());
            case "rainbow" -> loop = new Thread(setRainbow());
        }
        if (state) {
            HttpRequest request = HttpRequest.newBuilder().uri(hostURI).GET().build();
            try {
                showState(request);
            } catch (IOException e) {
                System.err.println("I/O Error!");
            } catch (InterruptedException e) {
                System.err.println("Something interrupted the ongoing operation.");
            }
        }
        loop.join();
    }

    String sendAndGetResponse(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.err.println("I/O Error!");
        } catch (InterruptedException e) {
            System.err.println("Something interrupted the ongoing operation.");
        }
        return response.body();
    }

    Runnable setFade() {
        HttpRequest request = HttpRequest.newBuilder().uri(hostURI.resolve("./" + lightID)).GET().build();
        int currentBrightness = new JSONObject(sendAndGetResponse(request)).getJSONObject("state").getInt("bri");
        JSONObject body = new JSONObject().put("bri", currentBrightness);
        boolean shouldAdd = true;
        try {
            while (true) {
                HttpRequest requests = HttpRequest.newBuilder()
                        .uri(hostURI.resolve("./" + lightID + "/state"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
                sendAndGetResponse(requests);
                if (shouldAdd) {
                    currentBrightness += 5;
                } else {
                    currentBrightness -= 5;
                }
                if (currentBrightness > 255 || currentBrightness < 0) {
                    shouldAdd = !shouldAdd;
                }

                body = body.put("bri", currentBrightness);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.err.println("Something interrupted the ongoing operation.");
        }
        return null;
    }

    Runnable setLinear() {
        String body = new JSONObject().put("hue", hue).toString();
        if (Integer.parseInt(lightID) != -1) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(hostURI.resolve("./" + lightID + "/state"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            sendAndGetResponse(request);
        } else {
            for (int i = 1; i <= amountOfLights; i++) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(hostURI.resolve("./" + i + "/state"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                sendAndGetResponse(request);
            }
        }
        return null;
    }

    Runnable setRainbow() {
        HttpRequest request = HttpRequest.newBuilder().uri(hostURI.resolve("./" + lightID)).GET().build();
        int currentHue = new JSONObject(sendAndGetResponse(request)).getJSONObject("state").getInt("hue");
        JSONObject body = new JSONObject().put("hue", currentHue);
        try {
            while (true) {
                HttpRequest requests = HttpRequest.newBuilder()
                        .uri(hostURI.resolve("./" + lightID + "/state"))
                        .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
                sendAndGetResponse(requests);
                currentHue += 1000;
                currentHue %= 65535;
                body = body.put("hue", currentHue);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.err.println("Something interrupted the ongoing operation.");
        }
        return null;

    }

    void showState(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
