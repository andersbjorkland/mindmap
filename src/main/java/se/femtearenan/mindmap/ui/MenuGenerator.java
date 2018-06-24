package se.femtearenan.mindmap.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import se.femtearenan.mindmap.Display;

public class MenuGenerator {
    private MenuActions actions;
    private MenuBar menuBar;

    public MenuGenerator(MenuActions actions) {
        this.actions = actions;
        menuBar = generateMenuBar();
    }

    private MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setMinWidth(Display.SCENE_WIDTH + 20);
        Menu menuFile = new Menu("File");
        MenuItem newMindMap = new MenuItem("New");
        newMindMap.setOnAction(event -> actions.newMindMap());
        MenuItem save = new MenuItem("Save");
        save.setOnAction(event -> actions.save());
        MenuItem open = new MenuItem("Open");
        open.setOnAction(event -> actions.open());
        menuFile.getItems().addAll(newMindMap, save, open);
        menuBar.getMenus().add(menuFile);

        return menuBar;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
