package hm.edu.nets;

public class DriverRoute {
    private String currentPosition;
    private final Driver driver;
    private final String API_KEY = "wiMfx6EU3WKC_1nwth9MxE8Sgh1DcvRcj9uR76T5h3E";

    public DriverRoute(Driver driver, String departureLocation, String arrivalLocation) {
        this.driver = driver;
        driver.setStatus(Status.DRIVING);
        queryLocation(departureLocation, arrivalLocation);
    }

    private void queryLocation(String departureLocation, String arrivalLocation) {

    }

    public void lateByXTime(String additionalTime) {
        driver.setStatus(Status.DELAY);

    }

    public int getTimeToArrival() {
        return 0; //Integer.parseInt(arrivalLocation) - Integer.parseInt(currentPosition);
    }

    public int reportLocation() {
        return 0;
    }

    public void cancelRide() {

    }

    public void update() {

    }
}