package hm.edu.nets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class DriverController {

    JSONData dataInst = new JSONData();
    Driver drv1 = new Driver(1, dataInst);
    DriverRoute routeDrv1;
    Driver drv2 = new Driver(2, dataInst);
    DriverRoute routeDrv2;
    Driver drv3 = new Driver(3, dataInst);
    DriverRoute routeDrv3;

    @Autowired
    private ObjectMapper mapper;

    public DriverController() {
        mapper = new ObjectMapper();
    }

    @RequestMapping(value = "/driver/{id}/route", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, Object> route(@PathVariable String id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        dataInst.setRouteLocations(id, input);
        DriverRoute currentRoute;
        currentRoute = new DriverRoute(getDriver(id), input.findValue("departure").asText(), input.findValue("arrival").asText(), dataInst);
        HashMap<String, Object> timestamps;
        timestamps = dataInst.setRouteTimes(id, currentRoute);
        switch (Integer.parseInt(id)) {
            case 1 -> routeDrv1 = currentRoute;
            case 2 -> routeDrv2 = currentRoute;
            case 3 -> routeDrv3 = currentRoute;
        }
        return timestamps;
    }

    @RequestMapping(value = "/driver/{id}/status", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String status(@PathVariable String id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Status newStatus = Status.valueOf(input.findValue("status").asText());
        getDriver(id).setStatus(newStatus);
        return "Status set to " + newStatus;
    }

    @RequestMapping(value = "/driver/{id}/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String update(@PathVariable String id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DriverRoute currentRoute;
        currentRoute = switch (id) {
            case "1" -> routeDrv1;
            case "2" -> routeDrv2;
            case "3" -> routeDrv3;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
        currentRoute.evaluateNewDriverLocation(dataInst.parseNewLocationFromJSON(input));
        return currentRoute.driver.getStatus() == Status.DELAY ? "Cant reach destination in time, delay set.\n New arrival time: " + currentRoute.getArrivalTime() : "Still in time.";
    }

    @RequestMapping(value = "/driver/{id}/cancel", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String cancel(@PathVariable String id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DriverRoute currentRoute;
        currentRoute = switch (id) {
            case "1" -> routeDrv1;
            case "2" -> routeDrv2;
            case "3" -> routeDrv3;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
        currentRoute.cancelRide();
        return "Ride canceled.";
    }


    @RequestMapping(value = "/driver", method = RequestMethod.GET)
    @ResponseBody
    public ObjectNode statusAll() {
        if (routeDrv1 != null) {
            if (routeDrv1.isRouteActive) {
                if (routeDrv1.getNewLocationTime() == null) {
                    routeDrv1.updateTTG(routeDrv1.getArrivalTime());
                } else {
                    routeDrv1.updateTTG(routeDrv1.getNewLocationTime());
                }
            }
        }
        if (routeDrv2 != null) {
            if (routeDrv2.isRouteActive) {
                if (routeDrv2.getNewLocationTime() == null) {
                    routeDrv2.updateTTG(routeDrv2.getArrivalTime());
                } else {
                    routeDrv2.updateTTG(routeDrv2.getNewLocationTime());
                }
            }
        }
        if (routeDrv3 != null) {
            if (routeDrv3.isRouteActive) {
                if (routeDrv3.getNewLocationTime() == null) {
                    routeDrv3.updateTTG(routeDrv3.getArrivalTime());
                } else {
                    routeDrv3.updateTTG(routeDrv3.getNewLocationTime());
                }
            }
        }
        return dataInst.data;
    }

    @RequestMapping(value = "/driver/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ObjectNode status(@PathVariable String id) {
        switch (Integer.parseInt(id)) {
            case 1 -> {
                if (routeDrv1.getNewLocationTime() == null) {
                    routeDrv1.updateTTG(routeDrv1.getArrivalTime());
                } else {
                    routeDrv1.updateTTG(routeDrv1.getNewLocationTime());
                }
            }
            case 2 -> {
                if (routeDrv2.getNewLocationTime() == null) {
                    routeDrv2.updateTTG(routeDrv2.getArrivalTime());
                } else {
                    routeDrv2.updateTTG(routeDrv2.getNewLocationTime());
                }
            }
            case 3 -> {
                if (routeDrv3.getNewLocationTime() == null) {
                    routeDrv3.updateTTG(routeDrv3.getArrivalTime());
                } else {
                    routeDrv3.updateTTG(routeDrv3.getNewLocationTime());
                }
            }
        }
        return dataInst.data.with(id);
    }


    private Driver getDriver(String ID) {
        return switch (Integer.parseInt(ID)) {
            case 1 -> drv1;
            case 2 -> drv2;
            case 3 -> drv3;
            default -> throw new IllegalStateException("Unexpected value: " + ID);
        };
    }
}
