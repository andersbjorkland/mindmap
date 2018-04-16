package model;

import javafx.scene.paint.Color;

/**
 * Instructions for the shape of thought bubble in the mind map,
 * its color, its thickness and its size
 */
public class Bubble {
    private Color color;
    private BubbleType type;
    private int lineThickness;
    private int sizeX;
    private int sizeY;

    public Bubble(Color color, BubbleType type, int lineThickness, int sizeX, int sizeY) {
        this.color = color;
        this.type = type;
        this.lineThickness = lineThickness;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public Bubble(Color color, BubbleType type, int sizeX, int sizeY) {
        this(color, type, 2, sizeX, sizeY);
    }

    /**
     * Default bubble type i Ellipse, and color black
     * @param sizeX is width in pixels
     * @param sizeY is height in pixels
     */
    public Bubble(int sizeX, int sizeY) {
        this(Color.BLACK, BubbleType.ELLIPSE, sizeX, sizeY);
    }

    public Bubble() {
        color = Color.BLACK;
        type = BubbleType.ELLIPSE;
        lineThickness = 2;
        sizeX = 10;
        sizeY = 8;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public BubbleType getType() {
        return type;
    }

    public void setType(BubbleType type) {
        this.type = type;
    }

    public int getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(int lineThickness) {
        this.lineThickness = lineThickness;
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
}
