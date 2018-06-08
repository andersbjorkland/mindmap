package canvas;

import controller.IdeaController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Display extends Application {
    private static final double SCENE_WIDTH = 800.0;
    private static final double SCENE_HEIGHT = 600.0;
    private static final Color SCENE_BACKGROUND = Color.LIGHTGRAY;

    private Group root = new Group();
    private Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, SCENE_BACKGROUND);

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Image icon = new Image("icon.png");
            primaryStage.getIcons().add(icon);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        IdeaController controller = new IdeaController(scene);

        Node background = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        background.setOnContextMenuRequested(event -> controller.options(event));

        // Create shapes for progress testing.
        Group ideaGroup = controller.generateIdeaGroup(IdeaController.mindExample());

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
        menuBar.getMenus().add(menuFile);

        return menuBar;
    }

    private void save() {

    }
}
