package hm.edu.nets;

public enum HueColor {

    GREEN(20000),
    YELLOW(10000),
    RED(0);

    final int color;

    HueColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.valueOf(color);
    }
}
