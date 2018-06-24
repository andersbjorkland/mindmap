package se.femtearenan.mindmap.ui;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Color;
import se.femtearenan.mindmap.model.BubbleType;
import se.femtearenan.mindmap.utility.SelectionState;
import se.femtearenan.mindmap.utility.SizeChoice;

public class ContextMenuController {
    private SelectionState selectionState;
    private ContextMenuActions actions;

    ContextMenuController(IdeaController controller) {
        actions = new ContextMenuActions(controller, this);
    }

    void setSelectionState(SelectionState state) {
        selectionState = state;
    }

    public void options(ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();

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
            create.setOnAction(contextEvent -> actions.optionCreate(event));

            contextMenu.getItems().add(create);
            contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());

        } else if (event.getSource() instanceof Node) {
            if (selectionState == SelectionState.NONE) {
                setParent = new MenuItem("Set Parent");
                setParent.setOnAction(contextEvent -> actions.optionSetParent(event));

                setAcquaintance = new MenuItem("Add an Acquaintance Connection");
                setAcquaintance.setOnAction(contextEvent -> actions.optionSetAcquaintance(event));

                removeAConnection = new MenuItem("Remove connection");
                removeAConnection.setOnAction(contextEvent -> actions.optionRemoveConnection(event));

                delete = new MenuItem("Delete this idea");
                delete.setOnAction(contextEvent -> actions.optionDeleteIdea(event));

                contextMenu.getItems().addAll(setParent,
                        setAcquaintance,
                        shapeMenu(event),
                        textMenu(event),
                        removeAConnection,
                        delete);

            } else if (selectionState == SelectionState.SELECT_PARENT) {
                setAsParent = new MenuItem("Set as Parent");
                setAsParent.setOnAction(contextEvent -> actions.selectAsParent(event));

                contextMenu.getItems().add(setAsParent);

            } else if (selectionState == SelectionState.SELECT_ACQUAINTANCE) {
                setAsAcquaintance = new MenuItem("Set as an Acquaintance Connection");
                setAsAcquaintance.setOnAction(contextEvent -> actions.selectAsAcquaintance(event));

                contextMenu.getItems().add(setAsAcquaintance);

            } else if (selectionState == SelectionState.REMOVE_CONNECTION) {
                removeThisConnection = new MenuItem("Remove this connection");
                removeThisConnection.setOnAction(contextEvent -> actions.removeThisConnection(event));

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

        ellipse.setOnAction(contextEvent -> actions.optionChangeShape(event, BubbleType.ELLIPSE));
        rectangle.setOnAction(contextEvent -> actions.optionChangeShape(event, BubbleType.RECTANGLE));
        cloud.setOnAction(contextEvent -> actions.optionChangeShape(event, BubbleType.CLOUD));
        spiky.setOnAction(contextEvent -> actions.optionChangeShape(event, BubbleType.SPIKY));

        changeShape.getItems().addAll(ellipse, rectangle, cloud, spiky);

        return changeShape;
    }

    private Menu shapeSizeMenu(ContextMenuEvent event) {
        Menu setShapeSize = new Menu("Change Shape Size");

        MenuItem changeSmaller = new MenuItem("Smaller");
        MenuItem changeLarger = new MenuItem("Larger");
        changeSmaller.setOnAction(contextEvent -> actions.optionSetShapeSize(event, false));
        changeLarger.setOnAction(contextEvent -> actions.optionSetShapeSize(event, true));

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
        setShapeBlack.setOnAction(contextEvent -> actions.optionSetShapeColor(event, Color.BLACK));
        setShapeBlue.setOnAction(contextEvent -> actions.optionSetShapeColor(event, Color.BLUE));
        setShapeGreen.setOnAction(contextEvent -> actions.optionSetShapeColor(event, Color.GREEN));
        setShapeRed.setOnAction(contextEvent -> actions.optionSetShapeColor(event, Color.RED));
        setShapeColor.getItems().addAll(setShapeBlack, setShapeBlue, setShapeGreen, setShapeRed);

        return setShapeColor;
    }

    private Menu textMenu(ContextMenuEvent event) {
        Menu textMenu = new Menu("Text options");
        textMenu.getItems().addAll(textSizeMenu(event), textColorMenu(event));

        return textMenu;
    }

    private Menu textSizeMenu(ContextMenuEvent event) {
        Menu setTextSize = new Menu("Set Text Size");
        MenuItem setSmall = new MenuItem("Small");
        MenuItem setMedium = new MenuItem("Medium");
        MenuItem setLarge = new MenuItem("Large");
        setSmall.setOnAction(contextEvent -> actions.optionSetTextSize(event, SizeChoice.SMALL));
        setMedium.setOnAction(contextEvent -> actions.optionSetTextSize(event, SizeChoice.MEDIUM));
        setLarge.setOnAction(contextEvent -> actions.optionSetTextSize(event, SizeChoice.LARGE));

        setTextSize.getItems().addAll(setSmall, setMedium, setLarge);

        return setTextSize;
    }

    private Menu textColorMenu(ContextMenuEvent event) {
        Menu setTextColor = new Menu("Set Text Color");
        MenuItem setTextBlack = new MenuItem("Black");
        MenuItem setTextBlue = new MenuItem("Blue");
        MenuItem setTextGreen = new MenuItem("Green");
        MenuItem setTextRed = new MenuItem("Red");
        setTextBlack.setOnAction(contextEvent -> actions.optionSetTextColor(event, Color.BLACK));
        setTextBlue.setOnAction(contextEvent -> actions.optionSetTextColor(event, Color.BLUE));
        setTextGreen.setOnAction(contextEvent -> actions.optionSetTextColor(event, Color.GREEN));
        setTextRed.setOnAction(contextEvent -> actions.optionSetTextColor(event, Color.RED));
        setTextColor.getItems().addAll(setTextBlack, setTextBlue, setTextGreen, setTextRed);

        return setTextColor;
    }
}
