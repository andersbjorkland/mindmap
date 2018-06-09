package model;

import java.io.Serializable;

public class PointSer implements Serializable {
    private static final long serialVersionUID = 1L;

    double x;
    double y;

    public PointSer(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
