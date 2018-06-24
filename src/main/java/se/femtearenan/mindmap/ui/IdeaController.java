package se.femtearenan.mindmap.ui;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import se.femtearenan.mindmap.model.*;
import se.femtearenan.mindmap.utility.*;

import java.util.*;

public class IdeaController {

    private Scene scene;
    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private Group ideaGroup = new Group();

    // State of manipulation
    private ContextMenuController contextMenuController;
    private LineDrawer lineDrawer;
    private Set<Idea> ideas;

    public IdeaController(Scene scene, Group ideaGroup) {
        this.scene = scene;
        this.ideaGroup = ideaGroup;
        contextMenuController = new ContextMenuController(this);
        this.scene.setOnMouseClicked(event -> contextMenuController.hideMenu());
        lineDrawer = new LineDrawer(this);
        ideas = new HashSet<>();
    }

    Pane ideaToPane(Idea idea){
        ideas.add(idea);
        Pane pane = new StackPane();
        Bubble bubble = idea.getBubble();
        Text text = new Text(idea.getTheme());
        text.setFill(bubble.getTextColor());
        Shape shape = ideaToShape(idea);
        lineDrawer.addIdeaLine(idea);
        lineDrawer.addAcquaintanceLines(idea);

        pane.getChildren().addAll(shape, text);
        pane.setOnContextMenuRequested(event -> contextMenuController.options(event));
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

        return bubble.getShape();
    }

    // Movable object, credit to this tutorial: http://java-buddy.blogspot.se/2013/07/javafx-drag-and-move-something.html
    // Now with movable stack.
    private EventHandler<MouseEvent> paneOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() != MouseButton.SECONDARY) {
                orgSceneX = event.getSceneX();
                orgSceneY = event.getSceneY();
                orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
                orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
            }
        }
    };

    public ContextMenuController getContextMenuController() {
        return contextMenuController;
    }

    void deleteIdea(Pane pane) {
        Idea deleteIdea = getIdeaFromPane(pane);

        // get all lines associated with this idea and delete them
        lineDrawer.removeIdeaLine(deleteIdea);

        for (Idea idea : ideas) {
            if (idea != deleteIdea) {
                if (idea.hasThisAcquaintance(deleteIdea)) {
                    idea.removeAcquaintance(deleteIdea);
                    lineDrawer.removeAcquaintanceLine(idea, deleteIdea);
                }
            }
        }


        for (Idea idea : deleteIdea.getAcquaintances().keySet()) {
            removeNodeFromScene(lineDrawer.getAcquaintanceLine(deleteIdea, idea));
            lineDrawer.removeAcquaintanceLine(deleteIdea, idea);
        }


        if (deleteIdea.hasParent()) {
            deleteIdea.getParent().removeChild(deleteIdea);
        }


        // Upon delete of an idea with a parent connection, instead of removing line, move it outside of display.
        // Else, the line will be drawn to empty space from an idea.
        lineDrawer.removeParentLines(deleteIdea);
        lineDrawer.removeIdeaLine(deleteIdea);

        ((Group)pane.getParent()).getChildren().remove(pane);
    }

    private EventHandler<MouseEvent> paneOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            int menuOffsetY = 20;
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
            if (newTranslateY < (menuOffsetY - (sourceHeight/2))) {
                newTranslateY = menuOffsetY - Math.round(sourceHeight/2);
            } else if (newTranslateY > (scene.getHeight() - sourceHeight/2 + 10) ) {
                newTranslateY = scene.getHeight() - Math.round(sourceHeight/2) + 10;
            }

            ((Pane)(event.getSource())).setTranslateX(newTranslateX);
            ((Pane)(event.getSource())).setTranslateY(newTranslateY);
            updateLines(ideaGroup);

        }
    };

    public void updateLines(Group ideaGroup) {
        for (Idea idea : ideas) {
            lineDrawer.drawIdeaLines(idea);
            lineDrawer.drawAcquaintanceLines(idea);
        }
    }

    private Point2D getScenePointFromPane(Pane pane) {

        double x = pane.getTranslateX();
        double y = pane.getTranslateY();

        return new Point2D(x, y);
    }

    public Point2D getScenePointFromIdea(Idea idea) {
        Pane pane = getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);
        return getScenePointFromPane(pane);
    }

    public Pane getIdeaPaneFromGroup(String theme, Group group) {
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

    public Pane getIdeaPaneFromGroup(String theme) {

        return getIdeaPaneFromGroup(theme, ideaGroup);
    }

    Idea getIdeaFromPane(Pane shapePane) {
        String theme = "";
        for (Node node : shapePane.getChildren()) {
            if (node instanceof Text) {
                theme = ((Text)node).getText();
            }
        }

        return getIdeaByTheme(theme);
    }

    Idea getIdeaByTheme(String theme) {
        Idea byTheme = null;
        for (Idea idea : ideas) {
            if (idea.getTheme().contentEquals(theme)) {
                byTheme = idea;
                break;
            }
        }
        return byTheme;
    }

    Pane getPaneFromEvent(ContextMenuEvent event) {
        Pane pane = null;
        if (event.getSource() instanceof Pane) {
            pane = (Pane)event.getSource();
        }
        return pane;
    }

    // returns Shape excluding Text
    Shape getShapeFromPane(Pane pane) {
        Shape shape = null;
        for (Node node : pane.getChildren()) {
            if (!(node instanceof Text) && node instanceof Shape) {
                shape = (Shape) node;
            }
        }
        return shape;
    }

    Text getTextFromPane(Pane pane) {
        Text text = null;
        for (Node node : pane.getChildren()) {
            if (node instanceof Text) {
                text = (Text) node;
            }
        }
        return text;
    }

    public Group getIdeaGroup() {
        return ideaGroup;
    }

    public Scene getScene() {
        return scene;
    }

    public void addNodeToScene(Node node) {
        ((Group)scene.getRoot()).getChildren().add(node);
    }

    public void removeNodeFromScene(Node node) {
        ((Group)scene.getRoot()).getChildren().remove(node);
    }

    public Set<Idea> getIdeas() {
        return ideas;
    }

    void removeAllIdeas() {
        for (Idea idea : ideas) {
            deleteIdea(getIdeaPaneFromGroup(idea.getTheme(), ideaGroup));
        }

        ideas.clear();
    }

    void unPackToScene(IdeaTracker tracker) {
        Map<Idea, PointSer> ideaPointMap = tracker.getPaneSceneTrack();

        for (Idea idea : ideaPointMap.keySet()) {
            Pane pane = ideaToPane(idea);
            PointSer point = ideaPointMap.get(idea);
            pane.setTranslateX(point.getX());
            pane.setTranslateY(point.getY());

            ideaGroup.getChildren().add(pane);
        }

        lineDrawer.drawLinesOnUnpacking(ideaPointMap);
    }

    LineDrawer getLineDrawer() {
        return lineDrawer;
    }
}