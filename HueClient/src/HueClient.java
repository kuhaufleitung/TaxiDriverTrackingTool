public class HueClient {
    public static void main(String... args) {
        if (args.length < 3 || (args.length < 4 && args[1].equalsIgnoreCase("linear"))) {
            System.out.println("""
                    USAGE:  [0]: lightID (-1 == all lights are controlled.)
                            [1]: lightingMode (fade, linear, rainbow)
                            [2]: showLightState (Bool)
                            [3]: color (only in conjunction with linear mode. HSL values.)
                    """);
            System.exit(-1);
        }
        final String lightID = args[0];
        final String lightingMode = args[1];
        final boolean showLightState = Boolean.parseBoolean(args[2]);
        final int hue;
        if (args[1].equalsIgnoreCase("linear")) {
            hue = Integer.parseInt(args[3]);
        } else {
            hue = -1;
        }
        try {
            new HueRestControl(lightID, lightingMode, showLightState, hue);
        } catch (InterruptedException e) {
            System.err.println("Something interrupted the ongoing operation.");
        }
    }

}