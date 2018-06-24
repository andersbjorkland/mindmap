package se.femtearenan.mindmap.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import se.femtearenan.mindmap.Display;

public class MenuBarGenerator {
    private MenuBarActions actions;
    private MenuBar menuBar;

    public MenuBarGenerator(MenuBarActions actions) {
        this.actions = actions;
        menuBar = generateMenuBar();
    }

    private MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setMinWidth(Display.SCENE_WIDTH + 20);
        Menu menuFile = new Menu("File");
        MenuItem newMindMap = new MenuItem("New");
        newMindMap.setOnAction(event -> actions.newMindMap());
        MenuItem open = new MenuItem("Open");
        open.setOnAction(event -> actions.open());
        MenuItem save = new MenuItem("Save");
        save.setOnAction(event -> actions.save());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> actions.close());
        menuFile.getItems().addAll(newMindMap, open, save, exit);
        menuBar.getMenus().add(menuFile);

        return menuBar;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
