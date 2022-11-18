package hm.edu.nets.client;

import hm.edu.nets.Driver;

public class ClientLauncher {
    public static void main(String[] args) {
        if (args.length == 1) {
            new DriverClient(args[0]);
        } else {
            System.out.println("USAGE: arg[0] -> driverID");
            System.exit(-1);
        }
    }
}
