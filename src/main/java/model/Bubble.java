package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import shapes.Cloudy;
import shapes.Spiky;

import java.io.Serializable;

/**
 * Instructions for the shape of thought bubble in the mind map,
 * its color, its thickness and its size.
 */
public class Bubble implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Color color;
    private String colorString = "";     // used to save color state
    private String textColorString;
    private BubbleType type;
    private int lineThickness;
    private int sizeX;
    private int sizeY;

    private Bubble(Color color, BubbleType type, int lineThickness, int sizeX, int sizeY) {
        this.color = color;
        this.type = type;
        this.lineThickness = lineThickness;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.colorString = toRGBCode(color);
        this.textColorString =  toRGBCode(Color.BLACK);
    }

    Bubble() {
        this (Color.BLACK, BubbleType.ELLIPSE, 2, 10, 8);
    }

    public Shape getShape() {
        Shape shape;

        switch (type) {
            case ELLIPSE:   shape = new Ellipse(sizeX, sizeY);
                            break;
            case RECTANGLE: shape = new Rectangle(sizeX*2, sizeY*2);
                            break;
            case CLOUD:     shape = Cloudy.getShape(sizeX, sizeY);
                            break;
            case SPIKY:     shape = Spiky.getShape(sizeX, sizeY);
                            break;
            default: shape = new Ellipse(sizeX, sizeY);
        }

        if (colorString.length() > 0) {
            color = Color.web(colorString);
        }

        shape.setStroke(color);
        shape.setStrokeWidth(lineThickness);
        shape.setFill(Color.WHITE);
        return shape;
    }

    Color getColor() {
        if (color == null) {
            color = Color.web(colorString);
        }
        return color;
    }

    public void setTextColor(Color textColor) {
        this.textColorString = toRGBCode(textColor);
    }

    public Color getTextColor() {
        return Color.web(textColorString);
    }

    public void setColor(Color color) {
        this.color = color;
        this.colorString = toRGBCode(color);
    }

    BubbleType getType() {
        return type;
    }

    public void setType(BubbleType type) {
        this.type = type;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    private static String toRGBCode(Color color) {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }
}
