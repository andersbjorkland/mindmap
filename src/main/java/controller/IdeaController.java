package controller;

import canvas.BoundTrack;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.*;

import java.util.*;

public class IdeaController {

    private Scene scene;
    private BoundTrack track;
    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private Map<Idea, Line> ideaLineMap = new HashMap<>();
    private Map<Idea, Map<Idea, Line>> acquaintanceLineMap = new HashMap<>();
    private Group ideaGroup = new Group();

    // State of manipulation
    private SelectionState selectionState = SelectionState.NONE;
    private Idea manipulatedIdea;
    private ContextMenu contextMenu;

    public IdeaController(Scene scene, Group ideaGroup) {
        this.scene = scene;
        this.ideaGroup = ideaGroup;
        track = new BoundTrack(scene.getWidth(), scene.getHeight());
        contextMenu = new ContextMenu();

        this.scene.setOnMouseClicked(event -> contextMenu.hide());
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
        Bubble bubble = idea.getBubble();
        Text text = new Text(idea.getTheme());
        text.setFill(bubble.getTextColor());
        Shape shape = ideaToShape(idea);
        Line line = new Line();
        ideaLineMap.put(idea, line);

        Map<Idea, Line> lineMap = new HashMap<>();
        for (Idea acquaintance : idea.getAcquaintances().keySet()) {
            Line aLine = new Line();
            lineMap.put(acquaintance, aLine);
            ((Group)scene.getRoot()).getChildren().add(aLine);
        }
        acquaintanceLineMap.put(idea, lineMap);

        pane.getChildren().addAll(shape, text);
        pane.setOnContextMenuRequested(event -> options(event));
        pane.setOnMousePressed(paneOnMousePressedEventHandler);
        pane.setOnMouseDragged(paneOnMouseDraggedEventHandler);

        ((Group)scene.getRoot()).getChildren().add(line);
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
                //options(event);
            } else {
                orgSceneX = event.getSceneX();
                orgSceneY = event.getSceneY();
                orgTranslateX = ((Pane) (event.getSource())).getTranslateX();
                orgTranslateY = ((Pane) (event.getSource())).getTranslateY();
            }
        }
    };

    public void options(ContextMenuEvent event) {
        contextMenu = new ContextMenu();

        // Declare all menu items.
        MenuItem create;
        MenuItem setParent;
        MenuItem setAsParent;
        MenuItem setAcquaintance;
        MenuItem setAsAcquaintance;
        MenuItem setShape;
        MenuItem setThickness;
        MenuItem removeAConnection;
        MenuItem removeThisConnection;
        MenuItem delete;

        // Initialize the items as needed and set event handlers
        if (event.getSource() instanceof Canvas) {
            selectionState = SelectionState.NONE;
            create = new MenuItem("Create");
            create.setOnAction(contextEvent -> optionCreate(event));

            contextMenu.getItems().add(create);
            contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());

        } else if (event.getSource() instanceof Node) {
            if (selectionState == SelectionState.NONE) {
                setParent = new MenuItem("Set Parent");
                setParent.setOnAction(contextEvent -> optionSetParent(event));

                setAcquaintance = new MenuItem("Add an Acquaintance Connection");
                setAcquaintance.setOnAction(contextEvent -> optionSetAcquaintance(event));

                removeAConnection = new MenuItem("Remove connection");
                removeAConnection.setOnAction(contextEvent -> optionRemoveConnection(event));

                delete = new MenuItem("Delete this idea");
                delete.setOnAction(contextEvent -> deleteIdea(event));

                contextMenu.getItems().addAll(setParent,
                        setAcquaintance,
                        shapeMenu(event),
                        textMenu(event),
                        removeAConnection,
                        delete);

            } else if (selectionState == SelectionState.SELECT_PARENT) {
                setAsParent = new MenuItem("Set as Parent");
                setAsParent.setOnAction(contextEvent -> selectAsParent(event));

                contextMenu.getItems().add(setAsParent);

            } else if (selectionState == SelectionState.SELECT_ACQUAINTANCE) {
                setAsAcquaintance = new MenuItem("Set as an Acquaintance Connection");
                setAsAcquaintance.setOnAction(contextEvent -> selectAsAcquaintance(event));

                contextMenu.getItems().add(setAsAcquaintance);

            } else if (selectionState == SelectionState.REMOVE_CONNECTION) {
                removeThisConnection = new MenuItem("Remove this connection");
                removeThisConnection.setOnAction(contextEvent -> removeThisConnection(event));

                contextMenu.getItems().add(removeThisConnection);
            }
            contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());

        }

    }

    private Menu shapeMenu(ContextMenuEvent event) {
        Menu shapeMenu = new Menu("Shape Options");
        shapeMenu.getItems().addAll(changeShapeMenu(event), shapeSizeMenu(event), shapeColorMenu(event));

        return  shapeMenu;
    }

    private Menu changeShapeMenu(ContextMenuEvent event) {
        Menu changeShape = new Menu("Change Shape");

        MenuItem ellipse = new MenuItem("Ellipse");
        MenuItem rectangle = new MenuItem("Rectangle");
        MenuItem cloud = new MenuItem("Cloudy");
        MenuItem spiky = new MenuItem("Spiky");

        ellipse.setOnAction(contextEvent -> optionChangeShape(event, BubbleType.ELLIPSE));
        rectangle.setOnAction(contextEvent -> optionChangeShape(event, BubbleType.RECTANGLE));
        cloud.setOnAction(contextEvent -> optionChangeShape(event, BubbleType.CLOUD));
        spiky.setOnAction(contextEvent -> optionChangeShape(event, BubbleType.SPIKY));

        changeShape.getItems().addAll(ellipse, rectangle, cloud, spiky);

        return changeShape;
    }

    private Menu shapeSizeMenu(ContextMenuEvent event) {
        Menu setShapeSize = new Menu("Change Shape Size");

        MenuItem changeSmaller = new MenuItem("Smaller");
        MenuItem changeLarger = new MenuItem("Larger");
        changeSmaller.setOnAction(contextEvent -> optionSetShapeSize(event, false));
        changeLarger.setOnAction(contextEvent -> optionSetShapeSize(event, true));

        setShapeSize.getItems().addAll(changeSmaller, changeLarger);

        return setShapeSize;
    }

    private Menu shapeColorMenu(ContextMenuEvent event) {
        //Color choices
        Menu setShapeColor = new Menu("Set Shape Color");
        MenuItem setShapeBlack = new MenuItem("Black");
        MenuItem setShapeBlue = new MenuItem("Blue");
        MenuItem setShapeGreen = new MenuItem("Green");
        MenuItem setShapeRed = new MenuItem("Red");
        setShapeBlack.setOnAction(contextEvent -> optionSetShapeColor(event, Color.BLACK));
        setShapeBlue.setOnAction(contextEvent -> optionSetShapeColor(event, Color.BLUE));
        setShapeGreen.setOnAction(contextEvent -> optionSetShapeColor(event, Color.GREEN));
        setShapeRed.setOnAction(contextEvent -> optionSetShapeColor(event, Color.RED));
        setShapeColor.getItems().addAll(setShapeBlack, setShapeBlue, setShapeGreen, setShapeRed);

        return setShapeColor;
    }

    private Menu textMenu(ContextMenuEvent event) {
        Menu textMenu = new Menu("Text options");
        textMenu.getItems().addAll(textColorMenu(event), textSizeMenu(event));

        return textMenu;
    }

    private Menu textSizeMenu(ContextMenuEvent event) {
        Menu setTextSize = new Menu("Set Text Size");
        MenuItem setSmall = new MenuItem("Small");
        MenuItem setMedium = new MenuItem("Medium");
        MenuItem setLarge = new MenuItem("Large");
        setSmall.setOnAction(contextEvent -> optionSetTextSize(event, SizeChoice.SMALL));
        setMedium.setOnAction(contextEvent -> optionSetTextSize(event, SizeChoice.MEDIUM));
        setLarge.setOnAction(contextEvent -> optionSetTextSize(event, SizeChoice.LARGE));

        setTextSize.getItems().addAll(setSmall, setMedium, setLarge);

        return setTextSize;
    }

    private Menu textColorMenu(ContextMenuEvent event) {
        Menu setTextColor = new Menu("Set Text Color");
        MenuItem setTextBlack = new MenuItem("Black");
        MenuItem setTextBlue = new MenuItem("Blue");
        MenuItem setTextGreen = new MenuItem("Green");
        MenuItem setTextRed = new MenuItem("Red");
        setTextBlack.setOnAction(contextEvent -> optionSetTextColor(event, Color.BLACK));
        setTextBlue.setOnAction(contextEvent -> optionSetTextColor(event, Color.BLUE));
        setTextGreen.setOnAction(contextEvent -> optionSetTextColor(event, Color.GREEN));
        setTextRed.setOnAction(contextEvent -> optionSetTextColor(event, Color.RED));
        setTextColor.getItems().addAll(setTextBlack, setTextBlue, setTextGreen, setTextRed);

        return setTextColor;
    }

    private void optionChangeShape(ContextMenuEvent event, BubbleType bubbleType) {
        if (event.getSource() instanceof Pane) {
            Pane pane = (Pane)event.getSource();
            Idea idea = getIdeaFromPane(pane);
            Bubble bubble = idea.getBubble();
            bubble.setType(bubbleType);

            Shape originalShape = getShapeFromPane(pane);
            Shape newShape = bubble.getShape();

            replaceNodeOnPane(pane, originalShape, newShape);
        }
    }

    private void optionSetShapeColor(ContextMenuEvent event, Color color) {
        if (event.getSource() instanceof Pane) {
            Pane pane = (Pane)event.getSource();
            Idea idea = getIdeaFromPane(pane);
            Bubble bubble = idea.getBubble();
            bubble.setColor(color);

            Shape shape = getShapeFromPane(pane);
            shape.setStroke(color);
        }
    }

    private void optionSetShapeSize(ContextMenuEvent event, boolean larger) {

        Pane pane = getPaneFromEvent(event);
        Shape shape = getShapeFromPane(pane);
        Idea idea = getIdeaFromPane(pane);
        Bubble bubble = idea.getBubble();

        int sizeX = bubble.getSizeX();
        int sizeY = bubble.getSizeY();

        int incrementX = 10;
        int incrementY = 4;

        if (larger) {
            sizeX += incrementX;
            sizeY += incrementY;
        } else {
            sizeX -= incrementX;
            sizeY -= incrementY;
        }

        bubble.setSizeY(sizeY);
        bubble.setSizeX(sizeX);

        Node original = shape;
        Node newNode = bubble.getShape();

        replaceNodeOnPane(pane, original, newNode);

    }

    private void replaceNodeOnPane(Pane pane, Node paneNode, Node replaceWith) {

        int zIndex = 0;

        for (int i = 0; i < pane.getChildren().size(); i++) {
            if (pane.getChildren().get(i) == paneNode) {
                zIndex = i;
            }
        }

        boolean isRemoved = pane.getChildren().remove(paneNode);
        pane.getChildren().add(zIndex, replaceWith);

    }


    private void optionSetTextColor(ContextMenuEvent event, Color color) {
        if (event.getSource() instanceof Pane) {
            Pane pane = (Pane)event.getSource();
            Idea idea = getIdeaFromPane(pane);
            Bubble bubble = idea.getBubble();
            bubble.setTextColor(color);

            Text text = getTextFromPane(pane);
            text.setFill(color);


        }
    }

    private void optionSetTextSize(ContextMenuEvent event, SizeChoice size) {
        Pane pane = getPaneFromEvent(event);
        Text text = getTextFromPane(pane);

        switch (size) {
            case SMALL: text.setFont(new Font(10));
                break;
            case MEDIUM: text.setFont(new Font(14));
                break;
            case LARGE: text.setFont(new Font(18));
                break;
        }
    }

    private void deleteIdea(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        deleteIdea(pane);
    }

    private void deleteIdea(Pane pane) {
        Idea deleteIdea = getIdeaFromPane(pane);

        // get all lines associated with this idea and delete them
        removeNodeFromScene(ideaLineMap.get(deleteIdea));
        ideaLineMap.remove(deleteIdea);

        for (Idea idea : ideaLineMap.keySet()) {
            if (idea != deleteIdea) {
                if (idea.hasThisAcquaintance(deleteIdea)) {
                    idea.removeAcquaintance(deleteIdea);
                    removeNodeFromScene(acquaintanceLineMap.get(idea).get(deleteIdea));
                    acquaintanceLineMap.get(idea).remove(deleteIdea);
                }
            }
        }


        System.out.println("Clearing acquaintances..." + deleteIdea); //TODO: remove
        for (Idea idea : deleteIdea.getAcquaintances().keySet()) {
            removeNodeFromScene(acquaintanceLineMap.get(deleteIdea).get(idea));
            acquaintanceLineMap.get(deleteIdea).remove(idea);
        }


        if (deleteIdea.hasParent()) {
            deleteIdea.getParent().removeChild(deleteIdea);
        }


        // Upon delete of an idea with a parent connection, instead of removing line, move it outside of display.
        // Else, the line will be drawn to empty space from an idea.
        if (deleteIdea.getChildren().size() > 0) {
            for (Idea child : deleteIdea.getChildren()) {
                Line line =  ideaLineMap.get(child);
                line.setStartX(-10);
                line.setStartY(-10);
                line.setEndX(-10);
                line.setEndY(-10);
            }
        }

        ideaLineMap.remove(deleteIdea);
        ((Group)pane.getParent()).getChildren().remove(pane);

    }

    private void removeThisConnection(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        Idea connectedIdea = getIdeaFromPane(pane);
        if (connectedIdea != manipulatedIdea) {
            // check if connection is parent-child
            if (connectedIdea.hasThisChild(manipulatedIdea)) {
                connectedIdea.removeChild(manipulatedIdea);
                Line line =  ideaLineMap.get(manipulatedIdea);
                line.setStartX(-10);
                line.setStartY(-10);
                line.setEndX(-10);
                line.setEndY(-10);
            }
            // check if connection is acquaintance
            if (connectedIdea.hasThisAcquaintance(manipulatedIdea)) {
                connectedIdea.removeAcquaintance(manipulatedIdea);
                removeNodeFromScene(acquaintanceLineMap.get(connectedIdea).get(manipulatedIdea));
                acquaintanceLineMap.get(connectedIdea).remove(manipulatedIdea);
            }

            if (manipulatedIdea.hasThisChild(connectedIdea)) {
                manipulatedIdea.removeChild(connectedIdea);
                Line line =  ideaLineMap.get(connectedIdea);
                line.setStartX(-10);
                line.setStartY(-10);
                line.setEndX(-10);
                line.setEndY(-10);
            }

            if (manipulatedIdea.hasThisAcquaintance(connectedIdea)) {
                manipulatedIdea.removeAcquaintance(connectedIdea);
                removeNodeFromScene(acquaintanceLineMap.get(manipulatedIdea).get(connectedIdea));
                acquaintanceLineMap.get(manipulatedIdea).remove(connectedIdea);
            }
            // if no connection, do nothing
        }
        selectionState = SelectionState.NONE;
        manipulatedIdea = null;
        updateLines(ideaGroup);
    }

    private void optionRemoveConnection(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        Idea idea = getIdeaFromPane(pane);
        manipulatedIdea = idea;
        selectionState = SelectionState.REMOVE_CONNECTION;
    }

    public void optionCreate(ContextMenuEvent event) {
        double sceneX = event.getSceneX();
        double sceneY = event.getSceneY();

        optionCreateThoughtAt(sceneX, sceneY);
    }

    private void optionSetParent(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        Idea child = getIdeaFromPane(pane);
        manipulatedIdea = child;
        selectionState = SelectionState.SELECT_PARENT;
    }

    private void selectAsParent(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        Idea parent = getIdeaFromPane(pane);
        if (parent != manipulatedIdea) {
            // remove child from set of children of the previous parent
            if (manipulatedIdea.hasParent())
                manipulatedIdea.getParent().getChildren().remove(manipulatedIdea);
            parent.addChild(manipulatedIdea);
        }
        selectionState = SelectionState.NONE;
        manipulatedIdea = null;
        updateLines(ideaGroup);
    }

    private void optionSetAcquaintance(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        Idea acquaintance = getIdeaFromPane(pane);
        manipulatedIdea = acquaintance;
        selectionState = SelectionState.SELECT_ACQUAINTANCE;
    }

    private void selectAsAcquaintance(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        Idea acquaintance = getIdeaFromPane(pane);
        if (acquaintance != manipulatedIdea) {
            addAcquaintance(acquaintance, IdeaConnectionType.EXPLANATION);
        }
        selectionState = SelectionState.NONE;
        manipulatedIdea = null;
        updateLines(ideaGroup);
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
            if (getIdeaByTheme(theme) == null) {
                idea = new Idea(theme);
                Pane pane = ideaToPane(idea);
                pane.setTranslateX(x);
                pane.setTranslateY(y);
                ideaGroup.getChildren().add(pane);
            } else {
                System.out.println("Already exists!\n");
            }
        }

        // add options upon right clicking the Bubble;
        // shape, color, sizes

        // add options upon right clicking to remove, create child or update

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

    private void addAcquaintance(Idea idea, IdeaConnectionType connectionType) {
        Line line = new Line();
        idea.addAcquaintance(manipulatedIdea, connectionType);
        acquaintanceLineMap.get(idea).put(manipulatedIdea, line);
        addNodeToScene(line);
        drawAcquaintanceLines(idea);
    }

    public void updateLines(Group ideaGroup) {
        for (Idea idea : ideaLineMap.keySet()) {
            drawLineBetweenIdeaShapes(ideaGroup, idea);
            drawAcquaintanceLines(idea);
        }
    }

    private void drawLineBetweenIdeaShapes(Group ideaGroup, Idea idea) {
        Idea parent = idea;
        if (parent.hasChildren()) {
            Set<Idea> children = parent.getChildren();

            Pane start = getIdeaPaneFromGroup(parent.getTheme(), ideaGroup);

            for (Idea child : children) {

                Pane end = getIdeaPaneFromGroup(child.getTheme(), ideaGroup);
                System.out.println("End Pane: " + end);

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
                System.out.println("  Child-Parent line: " + line); //TODO: remove
            }
        }
    }

    private void drawAcquaintanceLines(Idea idea) {
        if (idea.getAcquaintances().size() > 0) {
            Pane start = getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);

            for (Idea acquaintance : idea.getAcquaintances().keySet()) {

                Pane end = getIdeaPaneFromGroup(acquaintance.getTheme(), ideaGroup);

                Line line = acquaintanceLineMap.get(idea).get(acquaintance);
                line.getStrokeDashArray().addAll(5.0, 5.0);

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

    public Point2D getScenePointFromPane(Pane pane) {

        double x = pane.getTranslateX();
        double y = pane.getTranslateY();

        return new Point2D(x, y);
    }

    public Point2D getScenePointFromIdea(Idea idea) {
        Pane pane = getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);
        return getScenePointFromPane(pane);
    }


    private Pane getIdeaPaneFromGroup(String theme, Group group) {
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

        double xIncrement = track.getHorizontalBinSize() + 10;
        double yIncrement = track.getVerticalBinSize() + 25;

        // Policy is to start looking downwards from top center scene
        // then from left to right
        // in increments of pane height and width
        double yMenuOffset = 30;
        double coordinateMinX = (scene.getWidth() - bounds.getWidth()) / 2;
        double coordinateMinY = generationLevel * yIncrement + yMenuOffset;
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


    private Pane getPaneFromEvent(ContextMenuEvent event) {
        Pane pane = null;
        if (event.getSource() instanceof Pane) {
            pane = (Pane)event.getSource();
        }
        return pane;
    }

    // not text
    private Shape getShapeFromPane(Pane pane) {
        Shape shape = null;
        for (Node node : pane.getChildren()) {
            if (!(node instanceof Text) && node instanceof Shape) {
                shape = (Shape) node;
            }
        }
        return shape;
    }

    private Text getTextFromPane(Pane pane) {
        Text text = null;
        for (Node node : pane.getChildren()) {
            if (node instanceof Text) {
                text = (Text) node;
            }
        }
        return text;
    }

    public SelectionState getSelectionState() {
        return selectionState;
    }

    private void addNodeToScene(Node node) {
        ((Group)scene.getRoot()).getChildren().add(node);
    }

    private void removeNodeFromScene(Node node) {
        ((Group)scene.getRoot()).getChildren().remove(node);
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
        chihuahua.addAcquaintance(cute, IdeaConnectionType.EXPLANATION);

        Idea typeOfDog = new Idea("Type of dog");
        big.addAcquaintance(typeOfDog, IdeaConnectionType.EXPLANATION);
        small.addAcquaintance(typeOfDog, IdeaConnectionType.EXPLANATION);

        Idea terrier = new Idea("Terrier");
        small.addChild(terrier, IdeaConnectionType.BRANCH);

        Idea goldenRetriever = new Idea("Golden Retriever");
        big.addChild(goldenRetriever, IdeaConnectionType.BRANCH);

        goldenRetriever.getBubble().setType(BubbleType.CLOUD);
        chihuahua.getBubble().setType(BubbleType.SPIKY);

        Idea a = new Idea("Happiness");
        dogs.addChild(a, IdeaConnectionType.POINT);

        Idea mainIdea = dogs;
        return mainIdea;
    }

    public Scene getScene() {
        return scene;
    }

    public Set<Idea> getAllIdeas() {
        Set<Idea> ideas = new HashSet<>();

        ideas.addAll(ideaLineMap.keySet());
        for (Idea idea : ideas) {
            ideas.addAll(idea.getAcquaintances().keySet());
        }

        return ideas;
    }

    public void removeAllIdeas() {
        Set<Idea> ideas = getAllIdeas();
        for (Idea idea : ideas) {
            deleteIdea(getIdeaPaneFromGroup(idea.getTheme(), ideaGroup));
        }

        //ideaLineMap.clear();      TODO: clear
        //acquaintanceLineMap.clear();  TODO: clear
    }

    public void unPackToScene(IdeaTracker tracker) {
        Map<Idea, PointSer> ideaPointMap = tracker.paneSceneTrack;

        for (Idea idea : ideaPointMap.keySet()) {
            Pane pane = ideaToPane(idea);
            PointSer point = ideaPointMap.get(idea);
            pane.setTranslateX(point.getX());
            pane.setTranslateY(point.getY());

            ideaGroup.getChildren().add(pane);
        }

    }
}