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
