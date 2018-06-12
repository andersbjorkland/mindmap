import controller.IdeaController;
import controller.IdeaTracker;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;


public class Display extends Application {
    private static final double SCENE_WIDTH = 800.0;
    private static final double SCENE_HEIGHT = 600.0;
    private static final Color SCENE_BACKGROUND = Color.LIGHTGRAY;

    private Group root = new Group();
    private Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, SCENE_BACKGROUND);

    private IdeaController controller;
    private IdeaTracker ideaTracker;
    private Stage stage;
    private Group ideaGroup;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Image icon = new Image("icon.png");
            primaryStage.getIcons().add(icon);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        stage = primaryStage;

        ideaGroup = new Group();
        controller = new IdeaController(scene, ideaGroup);
        ideaTracker = new IdeaTracker();

        Node background = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        background.setOnContextMenuRequested(event -> controller.options(event));

        MenuBar menuBar = generateMenuBar();

        root.getChildren().addAll(background, ideaGroup, menuBar);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Mind Map");
        primaryStage.show();

        controller.moveListOfPanesToFreeSpace(controller.extractPanesFromGroup(ideaGroup));
        controller.updateLines(ideaGroup);


    }

    private MenuBar generateMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setMinWidth(scene.getWidth() + 20);
        Menu menuFile = new Menu("File");
        MenuItem newMindMap = new MenuItem("New");
        newMindMap.setOnAction(event -> newMindMap());
        MenuItem save = new MenuItem("Save");
        save.setOnAction(event -> save());
        MenuItem open = new MenuItem("Open");
        open.setOnAction(event -> open());
        menuFile.getItems().addAll(newMindMap, save, open);
        menuBar.getMenus().add(menuFile);

        return menuBar;
    }

    private void newMindMap() {
        ideaTracker.update(controller);
        clearScene();
    }

    private void save() {
        ideaTracker.update(controller);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Mind Map");
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "Mindmap Format (.mmf)", "*.mmf");
        fileChooser.getExtensionFilters().add(fileExtensions);

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(ideaTracker);
                oos.close();
                fout.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void open() {
        ideaTracker.update(controller);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Mind Map");
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "Mind Map Format (.mmf)", "*.mmf");
        fileChooser.getExtensionFilters().add(fileExtensions);

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                clearScene();
                FileInputStream fin = new FileInputStream(file.getAbsolutePath());
                ObjectInputStream ois = new ObjectInputStream(fin);
                ideaTracker = (IdeaTracker) ois.readObject();
                controller.unPackToScene(ideaTracker);
            } catch (ClassNotFoundException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    private void clearScene() {
        ObservableList<Node> nodes =  ideaGroup.getChildren();
        if (nodes.size() > 0) {
            controller.removeAllIdeas();
            ideaTracker.clearTrack();
            if (ideaGroup.getChildren().size() > 0) {
                ideaGroup.getChildren().clear();
            }
        }
    }


}
