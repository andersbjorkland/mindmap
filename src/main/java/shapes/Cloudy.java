package shapes;


import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

public class Cloudy {
    private final static double RADIUS_X = 25;
    private final static double RADIUS_Y = 5;

    public static Shape getShape(double width, double height) {
        Shape shape = defineCloud(width, height);

        // if radius is 25 (which would be 50 in width)
        // and the width to be is 100
        // then scaleX to be set as 2
        // which would be formulated as:
        // setScaleX = (width / RADIUS_X)
        //shape.setScaleX(width / RADIUS_X);
        //shape.setScaleY(height / RADIUS_X); // yes, it is _X since that is also defining height for in defineCloud.

        return shape;
    }

    private static Shape defineCloud(double width, double height) {

         double cloudPartThickness = height * 0.4;

        Shape cloud = new Ellipse(width, cloudPartThickness);
        Shape cloudAt45 = new Ellipse((height+width)/2.7, cloudPartThickness);
        Shape cloudAt90 = new Ellipse(height, cloudPartThickness);
        Shape cloudAt135 = new Ellipse((height+width)/2.7, cloudPartThickness);
        Shape cloudAt25 = new Ellipse((height+width)/2, cloudPartThickness);
        Shape cloudAt165 = new Ellipse((height+width)/2, cloudPartThickness);

        cloudAt45.setRotate(45);
        cloudAt90.setRotate(90);
        cloudAt135.setRotate(135);
        cloudAt25.setRotate(25);
        cloudAt165.setRotate(155);

        cloud = Shape.union(cloud, cloudAt25);
        cloud = Shape.union(cloud, cloudAt45);
        cloud = Shape.union(cloud, cloudAt90);
        cloud = Shape.union(cloud, cloudAt135);
        cloud = Shape.union(cloud, cloudAt165);

        //cloud.setScaleY(0.6);
        return cloud;
    }

    private static Shape defineEllipse(double width, double height) {
        Shape ellipse = new Ellipse(RADIUS_X, RADIUS_Y);

        return ellipse;
    }
}
