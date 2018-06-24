package se.femtearenan.mindmap.ui;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import se.femtearenan.mindmap.model.*;
import se.femtearenan.mindmap.utility.IdeaTracker;
import se.femtearenan.mindmap.utility.PointSer;
import se.femtearenan.mindmap.utility.SelectionState;
import se.femtearenan.mindmap.utility.SizeChoice;

import java.util.*;

public class IdeaController {

    private Scene scene;
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
        contextMenu = new ContextMenu();
        this.scene.setOnMouseClicked(event -> contextMenu.hide());
    }

    private Pane ideaToPane(Idea idea){
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
        pane.setOnContextMenuRequested(this::options);
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

        return bubble.getShape();
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
            if (event.getButton() != MouseButton.SECONDARY) {
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

        Node newNode = bubble.getShape();

        replaceNodeOnPane(pane, shape, newNode);

    }

    private void replaceNodeOnPane(Pane pane, Node paneNode, Node replaceWith) {

        int zIndex = 0;

        for (int i = 0; i < pane.getChildren().size(); i++) {
            if (pane.getChildren().get(i) == paneNode) {
                zIndex = i;
            }
        }

        pane.getChildren().remove(paneNode);
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
        manipulatedIdea = getIdeaFromPane(pane);
        selectionState = SelectionState.REMOVE_CONNECTION;
    }

    private void optionCreate(ContextMenuEvent event) {
        double sceneX = event.getSceneX();
        double sceneY = event.getSceneY();

        optionCreateThoughtAt(sceneX, sceneY);
    }

    private void optionSetParent(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        manipulatedIdea = getIdeaFromPane(pane);
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
        manipulatedIdea = getIdeaFromPane(pane);
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

    private void optionCreateThoughtAt(double x, double y) {
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
        Optional result = dialog.showAndWait();

        // create the Idea with the info of a theme
        // create a basic Bubble (Ellipse shape)
        if (result.isPresent() && ((String)result.get()).length() > 0) {
            theme = (String)result.get();
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
        if (idea.hasChildren()) {
            Set<Idea> children = idea.getChildren();

            Pane start = getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);

            for (Idea child : children) {

                Pane end = getIdeaPaneFromGroup(child.getTheme(), ideaGroup);

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

    private Point2D getScenePointFromPane(Pane pane) {

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

    Group getIdeaGroup() {
        return ideaGroup;
    }

    private void addNodeToScene(Node node) {
        ((Group)scene.getRoot()).getChildren().add(node);
    }

    private void removeNodeFromScene(Node node) {
        ((Group)scene.getRoot()).getChildren().remove(node);
    }

    public Set<Idea> getAllIdeas() {
        Set<Idea> ideas = new HashSet<>();

        ideas.addAll(ideaLineMap.keySet());
        for (Idea idea : ideas) {
            ideas.addAll(idea.getAcquaintances().keySet());
        }

        return ideas;
    }

    void removeAllIdeas() {
        Set<Idea> ideas = getAllIdeas();
        for (Idea idea : ideas) {
            deleteIdea(getIdeaPaneFromGroup(idea.getTheme(), ideaGroup));
        }

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

        drawLinesOnUnpacking(ideaPointMap);
    }

    private void drawLinesOnUnpacking(Map<Idea, PointSer> pointmap) {
        for (Idea idea : ideaLineMap.keySet()) {
            if (idea.hasChildren()) {
                Set<Idea> children = idea.getChildren();

                Pane start = getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);
                Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
                double startWidth = startBoundsInScene.getWidth();
                double startHeight = startBoundsInScene.getHeight();

                for (Idea child : children) {

                    Pane end = getIdeaPaneFromGroup(child.getTheme(), ideaGroup);

                    Line line = ideaLineMap.get(child);

                    Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

                    // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
                    double endWidth = endBoundsInScene.getWidth();
                    double endHeight = endBoundsInScene.getHeight();

                    double startX = pointmap.get(idea).getX();
                    double startY = pointmap.get(idea).getY() + startHeight / 2;
                    double endX = pointmap.get(child).getX();
                    double endY = pointmap.get(child).getY() + endHeight / 2;

                    // START is to the left of END
                    if ((startX + startWidth)  < endX) {
                        startX += startWidth;
                    } else if (startX > (endX + endWidth)) { // START is to the right of END
                        endX += endWidth;
                    } else {
                        // START is above the END
                        if ((startY + startHeight/2) < endY) {
                            endY -= endHeight/2;
                            endX += endWidth/2;
                            startY += startHeight/2;
                            startX += startWidth/2;
                        } else if (startY > (endY + endHeight/2)) { // START is below the END
                            endY += endHeight/2;
                            endX += endWidth/2;
                            startY -= startHeight/2;
                            startX += startWidth/2;
                        }
                    }

                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(endX);
                    line.setEndY(endY);
                }
            }

            if (idea.getAcquaintances().size() > 0) {
                Pane start = getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);
                Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
                double startWidth = startBoundsInScene.getWidth();
                double startHeight = startBoundsInScene.getHeight();

                for (Idea acquaintance : idea.getAcquaintances().keySet()) {
                    Pane end = getIdeaPaneFromGroup(acquaintance.getTheme(), ideaGroup);

                    Line line = acquaintanceLineMap.get(idea).get(acquaintance);
                    line.getStrokeDashArray().addAll(5.0, 5.0);

                    Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

                    // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
                    double endWidth = endBoundsInScene.getWidth();
                    double endHeight = endBoundsInScene.getHeight();

                    double startX = pointmap.get(idea).getX();
                    double startY = pointmap.get(idea).getY() + startHeight / 2;
                    double endX = pointmap.get(acquaintance).getX();
                    double endY = pointmap.get(acquaintance).getY() + endHeight / 2;

                    // START is to the left of END
                    if ((startX + startWidth) < endX) {
                        startX += startWidth;
                    } else if (startX > (endX + endWidth)) { // START is to the right of END
                        endX += endWidth;
                    } else {
                        // START is above the END
                        if ((startY + startHeight / 2) < endY) {
                            endY -= endHeight / 2;
                            endX += endWidth / 2;
                            startY += startHeight / 2;
                            startX += startWidth / 2;
                        } else if (startY > (endY + endHeight / 2)) { // START is below the END
                            endY += endHeight / 2;
                            endX += endWidth / 2;
                            startY -= startHeight / 2;
                            startX += startWidth / 2;
                        }
                    }

                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(endX);
                    line.setEndY(endY);
                }
            }
        }
    }
}