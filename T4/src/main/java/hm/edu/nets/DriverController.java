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

import java.time.ZonedDateTime;
import java.util.HashMap;

@RestController
public class DriverController {

    Driver drv1 = new Driver(1);
    Driver drv2 = new Driver(2);
    Driver drv3 = new Driver(3);
    ObjectNode data = initJSONObject();

    @Autowired
    private ObjectMapper mapper;

    public DriverController() {
        mapper = new ObjectMapper();
    }

    @RequestMapping(value = "/driver/{id}/route", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, ZonedDateTime> route(@PathVariable String id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        data.with(id).put("departure", input.findValue("departure"));
        data.with(id).put("arrival", input.findValue("arrival"));
        data.with(id).put("status", Status.DRIVING.toString());
        DriverRoute route = new DriverRoute(getDriver(id), input.findValue("departure").asText(), input.findValue("arrival").asText());
        HashMap<String, ZonedDateTime> timestamps = new HashMap<>();
        timestamps.put("departure", route.readDepartureTimeFromJSON());
        timestamps.put("arrival", route.readArrivalTimeFromJSON());
        return timestamps;
    }

    @RequestMapping(value = "/driver/{id}/status", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String status(@PathVariable String id, @RequestBody String json) {
        ObjectNode input;
        try {
            input = mapper.readValue(json, new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Status newStatus = Status.valueOf(input.findValue("status").asText());
        data.with(id).put("status", newStatus.toString());
        getDriver(id).setStatus(newStatus);
        return "Status set to " + newStatus;
    }

    @RequestMapping(value = "/driver", method = RequestMethod.GET)
    @ResponseBody
    public ObjectNode statusAll() {
        return data;
    }

    @RequestMapping(value = "/driver/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ObjectNode status(@PathVariable String id) {
        return data.with(id);
    }



    private Driver getDriver(String ID) {
        return switch (Integer.parseInt(ID)) {
            case 1 -> drv1;
            case 2 -> drv2;
            case 3 -> drv3;
            default -> throw new IllegalStateException("Unexpected value: " + ID);
        };
    }

    private ObjectNode initJSONObject() {
        ObjectMapper initMapper = new ObjectMapper();
        ObjectNode init = initMapper.createObjectNode();
        for (int i = 1; i <= 3; i++) {
            init.putObject(String.valueOf(i))
                    .put("status", Status.NOT_INIT.toString())
                    .put("departure", "null")
                    .put("arrival", "null")
                    .put("ttg", 0)
                    .putObject("location")
                    .put("lat", 0)
                    .put("lng", 0);
        }
        return init;
    }
}
