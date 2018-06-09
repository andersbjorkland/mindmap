package controller;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import model.Idea;
import model.PointSer;

import java.io.Serializable;
import java.util.*;

public class IdeaTracker implements Serializable{
    private static final long serialVersionUID = 1L;
    Map<Idea, PointSer> paneSceneTrack;

    public IdeaTracker(IdeaController controller) {
        this.paneSceneTrack = new HashMap<>();
    }

    public void update(IdeaController controller) {
        paneSceneTrack.clear();
        populateMap(controller);
    }

    public void clearTrack() {
        paneSceneTrack.clear();
    }

    private void populateMap(IdeaController controller) {
        System.out.println("I'm called on save in IdeaTracker.populateMap()");  //TODO: Remove
        Set<Idea> ideas = controller.getAllIdeas();

        for (Idea idea : ideas) {
            addIdeaToMap(idea, controller);
        }

    }

    private void addIdeaToMap(Idea idea, IdeaController controller) {
        Point2D point = controller.getScenePointFromIdea(idea);
        PointSer pointSer = new PointSer(point.getX(), point.getY());
        System.out.println(pointSer);   //TODO: Remove

        paneSceneTrack.put(idea, pointSer);
    }

    public Map<Idea, PointSer> getPaneSceneTrack(IdeaController controller) {
        update(controller);
        return paneSceneTrack;
    }


}
