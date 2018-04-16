package canvas;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
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

import static model.BubbleType.ELLIPSE;

public class Display extends Application {
    private static final double DEFAULT_SCENE_WIDTH = 600.0;
    private static final double DEFAULT_SCENE_HEIGHT = 600.0;
    private static final float DEFAULT_SHAPE_SIZE = 50.0f;
    private static final float DEFAULT_ELLIPSE_RATIO = 0.8f; // height to width ratio
    private static final Color DEFAULT_FILL = Color.WHITE;

    // Movable object, credit to this tutorial: http://java-buddy.blogspot.se/2013/07/javafx-drag-and-move-something.html
    // Now with movable stack.

    Shape shapeRed;
    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create shapes for progress testing.
        Bubble bubble = new Bubble(30, 20);
        Idea idea = new Idea("Tester and a long ass line to see if size changes", true, bubble);
        Pane pane = ideaToPane(idea);

        Bubble anotherBubble = new Bubble(Color.RED, BubbleType.ELLIPSE, 40, 30);
        Idea anotherIdea = new Idea("Tester child", false, anotherBubble);
        Pane anotherPane = ideaToPane(anotherIdea);

        idea.addChild(anotherIdea);

        Group root = new Group();
        root.getChildren().addAll(pane, anotherPane);

        Scene scene = new Scene(root, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT, Color.WHITE);

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
            width = textWidth + 10;
        }
        if (height < textHeight) {
            height = textHeight + 10;
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

            ((Pane)(event.getSource())).setTranslateX(newTranslateX);
            ((Pane)(event.getSource())).setTranslateY(newTranslateY);
        }
    };
}
