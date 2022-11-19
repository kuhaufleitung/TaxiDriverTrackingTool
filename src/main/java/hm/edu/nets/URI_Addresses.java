package hm.edu.nets;


import java.net.URI;

public record URI_Addresses() {
    //Emulator
    public final static String HueURI = "http://localhost:80/api/newdeveloper/lights";
    //Bridge1
    // public final static String HueURI = "http://10.28.9.120/api/197ea42c25303cef1a68c4042ed56887/lights/";
    //Bridge2
    // public final static String HueURI = "http://10.28.9.121/api/3dc1d8f23e55321f3c049c03ac88dff/lights/";
    //Bridge3
    // public final static String HueURI = "http://10.28.9.122/api/2217334838210e7f244460f83b42026f/lights/";
    //Bridge4
    // public final static String HueURI = "http://10.28.9.123/api/2b2d3ff23d63751f10c1d8c0332d50ff/lights/";
    public final static String HERE_Geocode = "https://geocode.search.hereapi.com/v1/geocode";
    public final static URI ServerURI = URI.create("http://localhost:8080/driver");
}
