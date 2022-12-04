package hm.edu.nets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DriverController {

    JSONData dataInst = new JSONData();

    private final HashMap<Integer, Driver> driverInstances = new HashMap<>();
    private final HashMap<Integer, DriverRoute> driverRoutes = new HashMap<>();

    @Autowired
    private ObjectMapper mapper;

    public DriverController() {
        mapper = new ObjectMapper();
        driverInstances.put(1, new Driver(1, dataInst));
        driverInstances.put(2, new Driver(2, dataInst));
        driverInstances.put(3, new Driver(3, dataInst));
        driverRoutes.put(1, null);
        driverRoutes.put(2, null);
        driverRoutes.put(3, null);


    }

    @RequestMapping(value = "/driver/{id}/route", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, Object> route(@PathVariable int id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        dataInst.setRouteLocations(String.valueOf(id), input);
        DriverRoute currentRoute;
        currentRoute = new DriverRoute(getDriver(id), input.findValue("departure").asText(), input.findValue("arrival").asText(), dataInst);
        HashMap<String, Object> timestamps;
        timestamps = dataInst.setRouteTimes(String.valueOf(id), currentRoute);
        driverRoutes.put(id, currentRoute);
        return timestamps;
    }

    @RequestMapping(value = "/driver/{id}/status", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String status(@PathVariable int id, @RequestBody String json) {
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
    public String update(@PathVariable int id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DriverRoute currentRoute = driverRoutes.get(id);
        currentRoute.evaluateNewDriverLocation(dataInst.parseNewLocationFromJSON(input));
        return currentRoute.driver.getStatus() == Status.DELAY ? "Cant reach destination in time, delay set.\n New arrival time: " + currentRoute.getArrivalTime() : "Still in time.";
    }

    @RequestMapping(value = "/driver/{id}/cancel", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String cancel(@PathVariable int id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DriverRoute currentRoute = driverRoutes.get(id);
        currentRoute.cancelRide();
        return "Ride canceled.";
    }


    @RequestMapping(value = "/driver", method = RequestMethod.GET)
    @ResponseBody
    public ObjectNode statusAll() {

        for (Map.Entry<Integer, DriverRoute> entry : driverRoutes.entrySet()) {
            DriverRoute currentRoute = entry.getValue();
            if (currentRoute != null) {
                if (currentRoute.isRouteActive) {
                    if (currentRoute.getNewLocationTime() == null) {
                        currentRoute.updateTTG(currentRoute.getArrivalTime());
                    } else {
                        currentRoute.updateTTG(currentRoute.getNewLocationTime());
                    }
                }
            }
        }
        return dataInst.data;
    }

    @RequestMapping(value = "/driver/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ObjectNode status(@PathVariable int id) {
            if (driverRoutes.get(id) != null) {
                if (driverRoutes.get(id).getNewLocationTime() == null) {
                    driverRoutes.get(id).updateTTG(driverRoutes.get(id).getArrivalTime());
                } else {
                    driverRoutes.get(id).updateTTG(driverRoutes.get(id).getNewLocationTime());
                }
            }
        return dataInst.data.with(String.valueOf(id));
    }


    private Driver getDriver(int ID) {
        return driverInstances.get(ID);
    }
}
