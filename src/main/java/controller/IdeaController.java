package controller;

import canvas.BoundTrack;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.Bubble;
import model.BubbleType;
import model.Idea;
import model.IdeaConnectionType;

import java.util.*;

public class IdeaController {

    private Scene scene;
    private BoundTrack track;
    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private Map<Idea, Line> ideaLineMap = new HashMap<>();
    private Group ideaGroup = new Group();

    public IdeaController(Scene scene) {
        this.scene = scene;
        track = new BoundTrack(scene.getWidth(), scene.getHeight());
    }

    public Collection<Line> getLines() {
        return ideaLineMap.values();
    }

    public Group generateIdeaGroup(Idea masterIdea) {
        Group group = new Group();
        for (Idea idea : masterIdea.getFamily()) {
            group.getChildren().add(ideaToPane(idea));
        }
        ideaGroup = group;
        return group;
    }

    public Pane ideaToPane(Idea idea){
        Pane pane = new StackPane();
        Text text = new Text(idea.getTheme());
        Shape shape = ideaToShape(idea);
        Line line = new Line();
        ideaLineMap.put(idea, line);

        text.setFill(getContrastColor((Color)shape.getFill()));

        pane.getChildren().addAll(shape, text);
        pane.setOnMousePressed(paneOnMousePressedEventHandler);
        pane.setOnMouseDragged(paneOnMouseDraggedEventHandler);

        return pane;
    }

    /*
     * Takes an Idea and generates a Shape according to instructions from the Ideas Bubble-object.
     */
    private Shape ideaToShape(Idea idea) {
        Bubble bubble = idea.getBubble();
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

        bubble.setSizeX((int) width);
        bubble.setSizeY((int) height);

        Shape shape = bubble.getShape();

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

    private void updateTrack() {
        track.cleanBoundsTrack();
        for (Pane pane : extractPanesFromGroup(ideaGroup)) {
            track.addOnBoundsTrack(retrieveBoundsForPane(pane));
        }
    }

    public List<Pane> extractPanesFromGroup(Group group) {
        List<Pane> panes = new ArrayList<>();

        for (Node node : group.getChildren()) {
            if (node instanceof Pane) {
                panes.add((Pane)node);
            }
        }

        return panes;
    }

    private Bounds retrieveBoundsForPane(Pane pane) {
        return pane.localToScene(pane.getBoundsInLocal());
    }

    // Movable object, credit to this tutorial: http://java-buddy.blogspot.se/2013/07/javafx-drag-and-move-something.html
    // Now with movable stack.
    private EventHandler<MouseEvent> paneOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.SECONDARY) {
                options(event);
            } else {
                orgSceneX = event.getSceneX();
                orgSceneY = event.getSceneY();
                orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
                orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
            }
        }
    };

    public void options(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog with Custom Actions");
        alert.setHeaderText("Look, a Confirmation Dialog with Custom Actions");
        alert.setContentText("Choose your option.");

        ButtonType buttonCreate = new ButtonType("Create");
        ButtonType buttonSetParent = new ButtonType("Set Parent");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (event.getSource() instanceof Scene) {
            alert.getButtonTypes().setAll(buttonCreate, buttonTypeCancel);
        } else {
            alert.getButtonTypes().setAll(buttonSetParent, buttonTypeCancel);
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonCreate) {
            optionCreate(event);
        } else if (result.get() == buttonSetParent) {
            optionSetParent(event);
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    private void optionCreate(MouseEvent event) {
        double sceneX = event.getSceneX();
        double sceneY = event.getSceneY();

        optionCreateThoughtAt(sceneX, sceneY);
    }

    private void optionSetParent(MouseEvent event) {
        Pane pane = (Pane) event.getSource();
        System.out.println("SET");
        optionSetParent(pane);
    }

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
            } else if (newTranslateX > (scene.getWidth() - sourceWidth/2 + 10) ) {
                newTranslateX = scene.getWidth() - Math.round(sourceWidth/2) + 10;
            }

            // boundary up and down
            if (newTranslateY < - (sourceHeight/2)) {
                newTranslateY = - Math.round(sourceHeight/2);
            } else if (newTranslateY > (scene.getHeight() - sourceHeight/2 + 10) ) {
                newTranslateY = scene.getHeight() - Math.round(sourceHeight/2) + 10;
            }

            ((Pane)(event.getSource())).setTranslateX(newTranslateX);
            ((Pane)(event.getSource())).setTranslateY(newTranslateY);
            updateLines(ideaGroup);

        }
    };

    public void updateLines(Group ideaGroup) {
        for (Idea idea : ideaLineMap.keySet()) {
            drawLineBetweenIdeaShapes(ideaGroup, idea);
        }
    }

    private void drawLineBetweenIdeaShapes(Group ideaGroup, Idea idea) {
        Idea parent = idea;
        if (parent.hasChildren()) {
            List<Idea> children = idea.getChildren();

            Pane start = getThemePaneFromGroup(parent.getTheme(), ideaGroup);

            for (Idea child : children) {

                Pane end = getThemePaneFromGroup(child.getTheme(), ideaGroup);

                Line line = ideaLineMap.get(child);

                Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
                Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

                // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
                double startX = startBoundsInScene.getMinX() + startBoundsInScene.getWidth() / 2;
                double startY = startBoundsInScene.getMinY() + startBoundsInScene.getHeight() / 2;
                double endX = endBoundsInScene.getMinX() + endBoundsInScene.getWidth() / 2;
                double endY = endBoundsInScene.getMinY() + endBoundsInScene.getHeight() / 2;

                // START is to the left of END
                if (startBoundsInScene.getMaxX() < endBoundsInScene.getMinX()) {
                    startX = startBoundsInScene.getMaxX();
                    endX = endBoundsInScene.getMinX();
                } else if (startBoundsInScene.getMinX() > endBoundsInScene.getMaxX()) { // START is to the right of END
                    startX = startBoundsInScene.getMinX();
                    endX = endBoundsInScene.getMaxX();
                } else {
                    // START is above the END
                    if (startBoundsInScene.getMaxY() < endBoundsInScene.getMinY()) {
                        startY = startBoundsInScene.getMaxY();
                        endY = endBoundsInScene.getMinY();
                    } else if (startBoundsInScene.getMinY() > endBoundsInScene.getMaxY()) { // START is below the END
                        startY = startBoundsInScene.getMinY();
                        endY = endBoundsInScene.getMaxY();
                    }
                }

                line.setStartX(startX);
                line.setStartY(startY);
                line.setEndX(endX);
                line.setEndY(endY);
            }
        }
    }

    private Pane getThemePaneFromGroup(String theme, Group group) {
        Pane themePane = new Pane();

        for (Node node : group.getChildren()) {
            if (node instanceof Pane) {
                Pane pane = (Pane) node;
                for (Node paneNode : pane.getChildren()) {
                    if (paneNode instanceof Text) {
                        String text = ((Text)paneNode).getText();
                        if (text.equals(theme)) {
                            themePane = pane;
                        }
                    }
                }

            }
        }

        return themePane;
    }

    public void moveListOfPanesToFreeSpace(List<Pane> panes) {
        for (Pane pane : panes) {
            movePaneToFreeSpace(pane);
        }
    }

    private void movePaneToFreeSpace(Pane pane) {
        Bounds bounds = retrieveBoundsForPane(pane);

        int generationLevel = getIdeaFromPane(pane).getChildLevel();
        if (generationLevel == -1) {
            generationLevel = 4;
        }

        double xIncrement = track.getHorizontalBinSize();
        double yIncrement = track.getVerticalBinSize() + 10;

        // Policy is to start looking downwards from top center scene
        // then from left to right
        // in increments of pane height and width
        double coordinateMinX = (scene.getWidth() - bounds.getWidth()) / 2;
        double coordinateMinY = generationLevel * yIncrement;
        double coordinateMaxX = coordinateMinX + bounds.getWidth();
        double coordinateMaxY = coordinateMinY + bounds.getHeight();



        // test if area is free
        int horizontalIncrements = 0;
        while ( (!track.isCoordinateAreaFree(coordinateMinX, coordinateMinY, coordinateMaxX, coordinateMaxY)) &&
                coordinateMaxY < (scene.getHeight() - bounds.getHeight()) ) {


            // move from center to the edges in increments of bin-sizes
            double x = (scene.getWidth() - bounds.getWidth()) / 2;
            double leftX = x;
            double rightX = x;
            for (int i = 1; i <= BoundTrack.TRACK_RESOLUTION_X; i++) {

                if (i % 2 == 0) {
                    leftX -= xIncrement + 1;
                    x = leftX;
                } else {
                    rightX += xIncrement - 1;
                    x = rightX;
                }
                coordinateMinX = x;
                coordinateMaxX = coordinateMinX + bounds.getWidth();

                if (track.isCoordinateAreaFree(coordinateMinX, coordinateMinY, coordinateMaxX, coordinateMaxY)) {
                    break;
                }
            }

            // move down one increment
            if (horizontalIncrements != 0) {
                coordinateMinY += yIncrement;
                coordinateMaxY = coordinateMinY + bounds.getHeight();
            }
            horizontalIncrements++;
        }

        // if no space is available, don't move pane, so reset variables.
        if (!track.isCoordinateAreaFree(coordinateMinX, coordinateMinY, coordinateMaxX, coordinateMaxY)) {
            System.out.println("DEFAULTING");
            coordinateMinX = bounds.getMinX();
            coordinateMinY = bounds.getMinY();
        }

        // now move the pane to the coordinates acquired.
        pane.setTranslateX(coordinateMinX);
        pane.setTranslateY(coordinateMinY);

        updateTrack();
    }

    private Idea getIdeaFromPane(Pane shapePane) {
        String theme = "";
        for (Node node : shapePane.getChildren()) {
            if (node instanceof Text) {
                theme = ((Text)node).getText();
            }
        }

        return getIdeaByTheme(theme);
    }

    private Idea getIdeaByTheme(String theme) {
        Idea byTheme = null;
        for (Idea idea : ideaLineMap.keySet()) {
            if (idea.getTheme().contentEquals(theme)) {
                byTheme = idea;
                break;
            }
        }
        return byTheme;
    }

    public void optionCreateThoughtAt(double x, double y) {
        String theme;
        Idea idea;

        // on right click, open a dialog to take text; the theme for Idea
        double width = 280;
        Dialog dialog = new TextInputDialog();
        dialog.setX(x + width);
        dialog.setY(y + 60);
        dialog.setHeaderText("");
        dialog.setTitle("Add an idea");
        dialog.setContentText("Enter an idea:");
        Optional<String> result = dialog.showAndWait();

        // create the Idea with the info of a theme
        // create a basic Bubble (Ellipse shape)
        if (result.isPresent() && result.get().length() > 0) {
            theme = result.get();
            idea = new Idea(theme);
            Line line = new Line();
            ideaLineMap.put(idea, line);
            Pane pane = ideaToPane(idea);
            pane.setTranslateX(x);
            pane.setTranslateY(y);
            ideaGroup.getChildren().addAll(pane);
        }

        // add options upon right clicking the Bubble;
        // shape, color, sizes

        // add options upon right clicking to remove, create child or update

    }

    public void optionSetParent(Pane pane) {
        Idea child = getIdeaFromPane(pane);
        Idea parent = selectParent();
        child.setParent(parent);
        updateLines(ideaGroup);
    }

    private Idea selectParent() {
        return ideaLineMap.keySet().iterator().next();
    }

    /*
     * Example of how a mind can be structured.
     */
    public static Idea mindExample() {

        Idea dogs = new Idea("Dogs", true);
        dogs.getBubble().setColor(Color.RED);
        dogs.getBubble().setLineThickness(2);
        dogs.getBubble().setType(BubbleType.ELLIPSE);

        Idea big = new Idea("Big");
        dogs.addChild(big, IdeaConnectionType.BRANCH);
        big.getBubble().setColor(Color.BLUE);
        big.getBubble().setLineThickness(3);

        Idea small = new Idea("Small");
        dogs.addChild(small, IdeaConnectionType.BRANCH);
        small.getBubble().setColor(Color.DARKGREEN);

        Idea greyhound = new Idea("Greyhound");
        big.addChild(greyhound, IdeaConnectionType.BRANCH);

        Idea chihuahua = new Idea("Chihuahua");
        small.addChild(chihuahua, IdeaConnectionType.BRANCH);

        Idea cute = new Idea("Cute");
        chihuahua.addAcquitance(cute, IdeaConnectionType.EXPLANATION);

        Idea typeOfDog = new Idea("Type of dog");
        big.addAcquitance(typeOfDog, IdeaConnectionType.EXPLANATION);
        small.addAcquitance(typeOfDog, IdeaConnectionType.EXPLANATION);

        Idea terrier = new Idea("Terrier");
        small.addChild(terrier, IdeaConnectionType.BRANCH);

        Idea goldenRetriever = new Idea("Golden Retriever");
        big.addChild(goldenRetriever, IdeaConnectionType.BRANCH);

        goldenRetriever.getBubble().setType(BubbleType.CLOUD);

        Idea a = new Idea("Happiness");
        dogs.addChild(a, IdeaConnectionType.POINT);

        Idea mainIdea = dogs;
        return mainIdea;
    }

}