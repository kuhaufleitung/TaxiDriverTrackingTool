package hm.edu.nets;

public class Driver {
    private Status status;
    private final int driverID;

    public Driver(int driverID) {
        status = Status.AVAILABLE;
        this.driverID = driverID;
        new Thread(this::updateLights).start();
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public int getDriverID() {
        return driverID;
    }

    private void updateLights() {
        while(true) {
            switch (status) {
                case AVAILABLE ->
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightColor(driverID, HueColor.GREEN.color));
                case DRIVING ->
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightColor(driverID, HueColor.YELLOW.color));
                case ON_BREAK ->
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightOff(driverID));
                case DELAY ->
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightBlinking(URI_Addresses.HueURI, driverID));
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
