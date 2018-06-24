package se.femtearenan.mindmap.ui;

import controller.IdeaController;
import controller.IdeaTracker;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class MenuActions {
    private IdeaController controller;
    private Stage stage;
    private Group ideaGroup;
    private IdeaTracker ideaTracker;


    public MenuActions(IdeaController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        this.ideaGroup = controller.getIdeaGroup();
        ideaTracker = new IdeaTracker();
    }

    void newMindMap() {
        ideaTracker.update(controller);
        clearScene();
    }

    void save() {
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

    void open() {
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
