package shapes;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Spiky {

    public static Shape getShape(double width, double height) {
        Shape shape = defineSpike(width, height);

        return shape;
    }

    private static Shape defineSpike(double width, double height) {

        double cloudPartThickness = height * 0.5;

        double widthAt45And135 = widthAtAngleOfEllipse(45, width, height);
        double widthAt90 = height;
        double widthAt25And165 = widthAtAngleOfEllipse(25, width, height);
        double widthAt12And168 = widthAtAngleOfEllipse(12, width, height);
        double widthAt6And174 = widthAtAngleOfEllipse(6, width, height);

        Polygon spike = new Polygon();
        spike.getPoints().addAll(new Double[]{
            0.0, height/2,
            width/2, 0.0,
            width, height/2,
            width/2, height
        });

        double rotatedHeight = Math.sqrt(Math.pow(50, 2) + Math.pow(50, 2));

        //spike.setScaleX(width / rotatedHeight);
        //spike.setScaleY(height / rotatedHeight);

        /*
        Shape cloudAt45 = new Ellipse(widthAt45And135, cloudPartThickness);
        Shape cloudAt90 = new Ellipse(widthAt90, widthAt90);
        Shape cloudAt135 = new Ellipse(widthAt45And135, cloudPartThickness);
        Shape cloudAt25 = new Ellipse(widthAt25And165, cloudPartThickness);
        Shape cloudAt165 = new Ellipse(widthAt25And165, cloudPartThickness*1.2);
        Shape cloudAt12 = new Ellipse(widthAt12And168, cloudPartThickness*1.2);
        Shape cloudAt168 = new Ellipse(widthAt12And168, cloudPartThickness*1.2);
        Shape cloudAt6 = new Ellipse(widthAt6And174, cloudPartThickness*1.2);
        Shape cloudAt174 = new Ellipse(widthAt6And174, cloudPartThickness*1.2);

        cloudAt45.setRotate(45);
        cloudAt90.setRotate(90);
        cloudAt135.setRotate(135);
        cloudAt25.setRotate(25);
        cloudAt165.setRotate(155);
        cloudAt12.setRotate(12);
        cloudAt168.setRotate(168);
        cloudAt6.setRotate(6);
        cloudAt174.setRotate(174);

        cloud = Shape.union(cloud, cloudAt12);
        cloud = Shape.union(cloud, cloudAt25);
        cloud = Shape.union(cloud, cloudAt45);
        cloud = Shape.union(cloud, cloudAt90);
        cloud = Shape.union(cloud, cloudAt135);
        cloud = Shape.union(cloud, cloudAt165);
        cloud = Shape.union(cloud, cloudAt168);
        cloud = Shape.union(cloud, cloudAt6);
        cloud = Shape.union(cloud, cloudAt174);

        */
        return spike;
    }

    private static double widthAtAngleOfEllipse(double angle, double width, double height) {
        double x = Math.sqrt(Math.pow(width, 2) + Math.pow((Math.tan(Math.toRadians(angle)) * Math.pow(width, 2)) / (2 * height), 2)) -
                (Math.tan(Math.toRadians(angle)) * Math.pow(width, 2)) / (2 * height);
        double a = x;
        double b = x * Math.tan(Math.toRadians(25));
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}
