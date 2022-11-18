package hm.edu.nets;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpClient;

@RestController
public class DriverController {
    HttpClient HueClient = HttpClient.newHttpClient();
    Driver drv1 = new Driver(1);
    Driver drv2 = new Driver(2);
    Driver drv3 = new Driver(3);

    //TODO: show HTML with delta to arrival and passedTime
    @GetMapping("/driver")
    public DriverRoute route(@RequestParam(value = "id") int ID, @RequestParam(value = "depart") String depart, @RequestParam(value = "arrival") String arrival) {
        return new DriverRoute(HueClient, getCorrectDriver(ID), depart, arrival);
    }

    Driver getCorrectDriver(int ID) {
        return switch (ID) {
            case 1 -> drv1;
            case 2 -> drv2;
            case 3 -> drv3;
            default -> throw new IllegalStateException("Unexpected value: " + ID);
        };
    }

}
