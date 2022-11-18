package hm.edu.nets;

public class Driver {
    private Status status;
    private int driverID;

    public Driver(int driverID) {
        status = Status.AVAILABLE;
        this.driverID = driverID;
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
}
