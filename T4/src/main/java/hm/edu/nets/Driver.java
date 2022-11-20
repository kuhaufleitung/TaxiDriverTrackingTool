package hm.edu.nets;

public class Driver {
    private Status status;
    private Status oldStatus;
    private final int driverID;
    private final JSONData driverData;

    public Driver(int driverID, JSONData driverData) {
        status = Status.AVAILABLE;
        this.driverID = driverID;
        this.driverData = driverData;
        RestCommunication.sendAndGetResponse(LightStateCommands.initLight(driverID));
        new Thread(this::updateLights).start();
    }

    public void setStatus(Status status) {
        this.status = status;
        driverData.data.with(String.valueOf(driverID)).put("status", status.toString());
    }

    public Status getStatus() {
        return status;
    }

    public int getDriverID() {
        return driverID;
    }

    private void updateLights() {
        while (true) {
            if (oldStatus != status) {
                switch (status) {
                    case AVAILABLE -> {
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightColor(driverID, HueColor.GREEN.color));
                        oldStatus = status;
                    }
                    case DRIVING -> {
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightColor(driverID, HueColor.YELLOW.color));
                        oldStatus = status;
                    }
                    case ON_BREAK -> {
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightOff(driverID));
                        oldStatus = status;
                    }
                    case DELAY -> {
                        RestCommunication.sendAndGetResponse(LightStateCommands.lightBlinking(driverID));
                        oldStatus = status;
                    }

                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.err.println("Runtime Excep:" + e);
            }
        }
    }
}
