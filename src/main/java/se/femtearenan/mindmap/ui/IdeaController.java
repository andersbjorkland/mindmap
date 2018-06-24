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
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
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
    private SelectionState selectionState = SelectionState.NONE;
    private Idea manipulatedIdea;
    private ContextMenu contextMenu;
    private LineDrawer lineDrawer;
    private Set<Idea> ideas;

    public IdeaController(Scene scene, Group ideaGroup) {
        this.scene = scene;
        this.ideaGroup = ideaGroup;
        contextMenu = new ContextMenu();
        this.scene.setOnMouseClicked(event -> contextMenu.hide());
        lineDrawer = new LineDrawer(this);
        ideas = new HashSet<>();
    }

    private Pane ideaToPane(Idea idea){
        ideas.add(idea);
        Pane pane = new StackPane();
        Bubble bubble = idea.getBubble();
        Text text = new Text(idea.getTheme());
        text.setFill(bubble.getTextColor());
        Shape shape = ideaToShape(idea);
        lineDrawer.addIdeaLine(idea);
        lineDrawer.addAcquaintanceLines(idea);

        pane.getChildren().addAll(shape, text);
        pane.setOnContextMenuRequested(this::options);
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

    private void removeThisConnection(ContextMenuEvent event) {
        Pane pane = (Pane) event.getSource();
        Idea connectedIdea = getIdeaFromPane(pane);
        if (connectedIdea != manipulatedIdea) {
            // check if connection is parent-child
            if (connectedIdea.hasThisChild(manipulatedIdea)) {
                connectedIdea.removeChild(manipulatedIdea);
                lineDrawer.hideLine(manipulatedIdea);
            }
            // check if connection is acquaintance
            if (connectedIdea.hasThisAcquaintance(manipulatedIdea)) {
                connectedIdea.removeAcquaintance(manipulatedIdea);
                removeNodeFromScene(lineDrawer.getAcquaintanceLine(connectedIdea, manipulatedIdea));
                lineDrawer.removeAcquaintanceLine(connectedIdea, manipulatedIdea);
            }

            if (manipulatedIdea.hasThisChild(connectedIdea)) {
                manipulatedIdea.removeChild(connectedIdea);
                lineDrawer.hideLine(connectedIdea);
            }

            if (manipulatedIdea.hasThisAcquaintance(connectedIdea)) {
                manipulatedIdea.removeAcquaintance(connectedIdea);
                lineDrawer.removeAcquaintanceLine(manipulatedIdea, connectedIdea);
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
        ideas.add(idea);
        idea.addAcquaintance(manipulatedIdea, connectionType);
        lineDrawer.addAcquaintanceLine(idea, manipulatedIdea);
    }

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
        for (Idea idea : ideas) {
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

    public Set<Idea> getAllIdeas() {
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
}