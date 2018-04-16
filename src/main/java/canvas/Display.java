package canvas;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Bubble;
import model.BubbleType;
import model.Idea;
import model.IdeaConnectionType;

import java.io.File;

import static model.BubbleType.ELLIPSE;

public class Display extends Application {
    private static final double DEFAULT_SCENE_WIDTH = 600.0;
    private static final double DEFAULT_SCENE_HEIGHT = 600.0;
    private static final Color DEFAULT_SCENE_BACKGROUND = Color.LIGHTGRAY;
    private static final float DEFAULT_SHAPE_SIZE = 50.0f;
    private static final float DEFAULT_ELLIPSE_RATIO = 0.8f; // height to width ratio
    private static final Color DEFAULT_FILL = new Color(1.0, 1.0, 1.0, 0.7);

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Image icon = new Image("icon.png");
            primaryStage.getIcons().add(icon);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        // Create shapes for progress testing.
        Bubble bubble = new Bubble(Color.DARKRED, BubbleType.ELLIPSE, 5,80, 20);
        Idea idea = new Idea("Tester", true, bubble);
        Pane pane = ideaToPane(idea);

        Bubble anotherBubble = new Bubble(Color.RED, BubbleType.ELLIPSE, 3,40, 20);
        Idea anotherIdea = new Idea("Tester child", false, anotherBubble);
        Pane anotherPane = ideaToPane(anotherIdea);

        idea.addChild(anotherIdea);

        Group root = new Group();
        root.getChildren().addAll(pane, anotherPane);

        Scene scene = new Scene(root, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT, DEFAULT_SCENE_BACKGROUND);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Mind Map");
        primaryStage.show();
    }

    /**
     * Takes an Idea and adds it to a StackPane as a Shape, the Pane gets EventHandlers to be movable.
     * @param idea
     * @return a StackPane.
     */
    public Pane ideaToPane(Idea idea){
        Pane pane = new StackPane();
        Text text = new Text(idea.getTheme());
        Shape shape = ideaToShape(idea);

        text.setFill(getContrastColor((Color)shape.getFill()));

        pane.getChildren().addAll(shape, text);
        pane.setOnMousePressed(paneOnMousePressedEventHandler);
        pane.setOnMouseDragged(paneOnMouseDraggedEventHandler);
        return pane;
    }

    /**
     * Takes an Idea and generates a Shape according to instructions from the Ideas Bubble-object.
     * @param idea contains instructions to generate a Shape
     * @return a subclass-object of the Shape class.
     */
    public Shape ideaToShape(Idea idea) {
        Shape shape = new Ellipse(DEFAULT_SHAPE_SIZE, DEFAULT_SHAPE_SIZE*DEFAULT_ELLIPSE_RATIO);
        Bubble bubble = idea.getBubble();
        BubbleType shapeType = bubble.getType();

        float width = bubble.getSizeX();
        float height = bubble.getSizeY();

        // Change width and height of object if the text to be contained within is larger than the shape.
        Text text = new Text(idea.getTheme());
        float textWidth = (float)text.getLayoutBounds().getWidth();
        float textHeight = (float)text.getLayoutBounds().getHeight();
        if (width < textWidth) {
            width = textWidth + 5;
        }
        if (height < textHeight) {
            height = textHeight + 2;
        }

        shape.setStrokeWidth(bubble.getLineThickness());
        shape.setStroke(bubble.getColor());
        shape.setFill(DEFAULT_FILL);

        switch (shapeType) {
            case ELLIPSE:   ((Ellipse)shape).setRadiusX(width);
                            ((Ellipse)shape).setRadiusY(height);
                            break;
        }

        return shape;
    }

    /**
     * Takes a color and returns white or black depending on brightness of color.
     * Credit to brimborium on https://stackoverflow.com/questions/4672271/reverse-opposing-colors
     * @param color as the color to be contrasted to.
     * @return Color.WHITE or Color.BLACK depending on brightness of argument color.
     */
    private static Color getContrastColor(Color color) {
        // Multiplies with 255 to take into account that the example was taken from an awt implementation (not JavaFX)
        double y = (299 * (color.getRed()*255) + 587 * (color.getGreen()*255) + 114 * (color.getBlue()*255)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    // Movable object, credit to this tutorial: http://java-buddy.blogspot.se/2013/07/javafx-drag-and-move-something.html
    // Now with movable stack.
    private EventHandler<MouseEvent> paneOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgTranslateX = ((Pane)(event.getSource())).getTranslateX();
            orgTranslateY = ((Pane)(event.getSource())).getTranslateY();
        }
    };

    private EventHandler<MouseEvent> paneOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            double offsetX = event.getSceneX() - orgSceneX;
            double offsetY = event.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            // focus on newTranslateX/Y
            double sourceWidth = ((Pane)(event.getSource())).getWidth();
            double sourceHeight = ((Pane)(event.getSource())).getHeight();

            // BOUNDS
            // boundary left and right
            if (newTranslateX < -(sourceWidth/2)) {
                newTranslateX = -Math.round(sourceWidth/2);
            } else if (newTranslateX > (DEFAULT_SCENE_WIDTH - sourceWidth/2 + 10) ) {
                newTranslateX = DEFAULT_SCENE_WIDTH - Math.round(sourceWidth/2) + 10;
            }

            // boundary up and down
            if (newTranslateY < - (sourceHeight/2)) {
                newTranslateY = - Math.round(sourceHeight/2);
            } else if (newTranslateY > (DEFAULT_SCENE_HEIGHT - sourceHeight/2 + 10) ) {
                newTranslateY = DEFAULT_SCENE_HEIGHT - Math.round(sourceHeight/2) + 10;
            }

            ((Pane)(event.getSource())).setTranslateX(newTranslateX);
            ((Pane)(event.getSource())).setTranslateY(newTranslateY);

        }
    };
}
