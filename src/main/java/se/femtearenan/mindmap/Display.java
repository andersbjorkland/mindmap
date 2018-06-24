package se.femtearenan.mindmap;

import se.femtearenan.mindmap.ui.IdeaController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import se.femtearenan.mindmap.ui.MenuActions;
import se.femtearenan.mindmap.ui.MenuGenerator;


public class Display extends Application {
    public static final double SCENE_WIDTH = 800.0;
    private static final double SCENE_HEIGHT = 600.0;
    private static final Color SCENE_BACKGROUND = Color.LIGHTGRAY;

    private Group root = new Group();
    private Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, SCENE_BACKGROUND);

    private IdeaController controller;
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

        Node background = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        background.setOnContextMenuRequested(event -> controller.options(event));

        MenuActions menuActions = new MenuActions(controller, stage);
        MenuBar menuBar = new MenuGenerator(menuActions).getMenuBar();

        root.getChildren().addAll(background, ideaGroup, menuBar);

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Mind Map");
        primaryStage.show();

        controller.updateLines(ideaGroup);

    }
}
