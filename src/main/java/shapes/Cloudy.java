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

        // width = height / sinV

        Shape cloud = new Ellipse(width, height * 0.6);
        Shape cloud2 = new Ellipse(width, height * 0.6);


        int numberOfEllipses = 8;
        double rotateByDegreeIncrement = 180 / numberOfEllipses;
        double degree;
        double ellipseWidth = width;
        double ellipseHeight = height * 0.4;
        /*

        for (int i = 1; i < numberOfEllipses; i++) {
            degree = rotateByDegreeIncrement * i;

            // width / sin V = height
            if (width / Math.sin(Math.toRadians(degree)) > height) {
                ellipseWidth = height / Math.sin(Math.toRadians(degree));
            } else {
                ellipseWidth = width;
            }

            Shape rotateShape = new Ellipse(ellipseWidth, ellipseHeight);
            rotateShape.setRotate(degree);
            cloud = Shape.union(cloud, rotateShape);
        }
        */

        cloud2.setRotate(90);
        cloud = Shape.union(cloud, cloud2);
        Shape cloud3 = Shape.union(cloud, cloud2);
        cloud3.setRotate(45);
        cloud = Shape.union(cloud, cloud3);

        //cloud.setScaleY(0.6);
        return cloud;
    }

    private static Shape defineEllipse(double width, double height) {
        Shape ellipse = new Ellipse(RADIUS_X, RADIUS_Y);

        return ellipse;
    }
}
