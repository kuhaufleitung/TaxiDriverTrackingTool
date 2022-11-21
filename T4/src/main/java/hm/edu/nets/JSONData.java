package hm.edu.nets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONData {

    public ObjectNode data;
    public JSONData() {
        initJSONObject();
    }

    private void initJSONObject() {
        ObjectMapper initMapper = new ObjectMapper();
        data = initMapper.createObjectNode();
        for (int i = 1; i <= 3; i++) {
            data.putObject(String.valueOf(i))
                    .put("status", Status.NOT_INIT.toString())
                    .put("departure", "None")
                    .put("arrival", "None")
                    //.putObject("location")
                    .put("departureAt", 0)
                    .put("arrivalAt", 0)
                    .put("ttg", 0);

        }
    }

    public void setRouteLocations(String driverID, ObjectNode input) {
        data.with(driverID).put("departure", input.findValue("departure"));
        data.with(driverID).put("arrival", input.findValue("arrival"));
        data.with(driverID).put("status", Status.DRIVING.toString());

    }
    //TTG is set with a 5min time buffer. arrival is still the original time.
    public void setRouteTimes(String driverID, DriverRoute route) {
        data.with(driverID).put("departureAt", route.getDepartureTime().toString());
        data.with(driverID).put("arrivalAt", route.getArrivalTime().toString());
        data.with(driverID).put("ttg", route.getTTG());
    }
}
