package se.femtearenan.mindmap.utility;

import java.io.Serializable;

public class PointSer implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x;
    private double y;

    PointSer(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    @Override
    public String toString() {
        return "PointSer{" +
                x +
                ":" + y +
                '}';
    }
}
