package hm.edu.nets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpClient;

@RestController
public class DriverController {
    HttpClient HueClient = HttpClient.newHttpClient();
    Driver drv1 = new Driver(1);
    Driver drv2 = new Driver(2);
    Driver drv3 = new Driver(3);
    ObjectNode data = initJSONObject();

    @Autowired
    private ObjectMapper mapper;

    public DriverController() {
        mapper = new ObjectMapper();
    }


    @RequestMapping(value = "/driver", method = RequestMethod.PUT)
    public DriverRoute route(@RequestParam(value = "id") String ID, @RequestParam(value = "depart") String depart, @RequestParam(value = "arrival") String arrival) {
        //data.put(ID, new JSONObject().put("status", getCorrectDriver(ID).getStatus()));
        return new DriverRoute(HueClient, getCorrectDriver(ID), depart, arrival);
    }

    @RequestMapping(value = "/driver", method = RequestMethod.GET)
    @ResponseBody
    public ObjectNode status() {
        return data;
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
                    .put("departure", 0)
                    .put("arrival", 0)
                    .put("ttg", 0)
                    .putObject("location")
                    .put("lat", 0)
                    .put("lng", 0);
        }
        return init;
    }
}
