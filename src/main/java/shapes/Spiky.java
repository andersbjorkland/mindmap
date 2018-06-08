package shapes;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Spiky {

    public static Shape getShape(double width, double height) {
        Shape shape = defineSpike(width, height);

        return shape;
    }

    private static Shape defineSpike(double width, double height) {
        // add padding
        width *= 2;
        height *= 2;

        double widthAt30And150 = widthAtAngleOfEllipse(30, width, height) * 1.3;

        Double[] diamondPoints = new Double[]{
                0.0, height / 2,        //W
                width / 2, 0.0,         //N
                width, height / 2,      //E
                width / 2, height       //S
        };
        Polygon diamond = new Polygon();
        diamond.getPoints().addAll(diamondPoints);

        Double[] polyPointsToBeRotated = new Double[] {
                (width - widthAt30And150)/2, height/2,
                widthAt30And150/2 + (width - widthAt30And150)/2, 0.0,
                widthAt30And150 + (width - widthAt30And150)/2, height/2,
                widthAt30And150/2 +(width - widthAt30And150)/2, height
        };
        Polygon diamondAt45Degrees = new Polygon();
        diamondAt45Degrees.getPoints().addAll(polyPointsToBeRotated);
        diamondAt45Degrees.setRotate(30);

        Polygon diamondAt135Degrees = new Polygon();
        diamondAt135Degrees.getPoints().addAll(polyPointsToBeRotated);
        diamondAt135Degrees.setRotate(150);

        Shape diamonds = Shape.union(diamondAt45Degrees, diamondAt135Degrees);


        return Shape.union(diamond, diamonds);
    }

    private static double widthAtAngleOfEllipse(double angle, double width, double height) {
        double x = Math.sqrt(Math.pow(width, 2) + Math.pow((Math.tan(Math.toRadians(angle)) * Math.pow(width, 2)) / (2 * height), 2)) -
                (Math.tan(Math.toRadians(angle)) * Math.pow(width, 2)) / (2 * height);
        double a = x;
        double b = x * Math.tan(Math.toRadians(25));
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}
