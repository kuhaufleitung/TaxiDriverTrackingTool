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

import java.net.http.HttpClient;

@RestController
public class DriverController {
    HttpClient HueClient = HttpClient.newHttpClient();
    Driver drv1 = new Driver(1, HueClient);
    Driver drv2 = new Driver(2, HueClient);
    Driver drv3 = new Driver(3, HueClient);
    ObjectNode data = initJSONObject();

    @Autowired
    private ObjectMapper mapper;

    public DriverController() {
        mapper = new ObjectMapper();
    }

    @RequestMapping(value = "/driver/{id}", method = RequestMethod.PUT, consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public DriverRoute route(@PathVariable String id, @RequestBody String json) {
        ObjectNode input = null;
        try {
            input = mapper.readValue(json, new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        data.with(id).put("depart", input.findValue("depart"));
        data.with(id).put("arrival", input.findValue("arrival"));
        data.with(id).put("status", Status.DRIVING.toString());
        return new DriverRoute(getCorrectDriver(id), json, json);
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



    private Driver getCorrectDriver(String ID) {

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
            /*
            init.putArray(String.valueOf(i), mapper.createObjectNode()
                            .put("status", Status.NOT_INIT.ordinal())
                            .put("location", mapper.createObjectNode()
                                    .put("lat", 0)
                                    .put("lng", 0))
                            .put("departure", 0)
                            .put("arrival", 0)
                            .put("ttg", 0))
                    .put("passedTime", 0);
*/
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
