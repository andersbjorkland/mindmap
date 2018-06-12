package controller;

import javafx.geometry.Point2D;
import model.Idea;
import model.PointSer;

import java.io.Serializable;
import java.util.*;

public class IdeaTracker implements Serializable{
    private static final long serialVersionUID = 1L;
    private Map<Idea, PointSer> paneSceneTrack;

    public IdeaTracker() {
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
        Set<Idea> ideas = controller.getAllIdeas();

        for (Idea idea : ideas) {
            addIdeaToMap(idea, controller);
        }

    }

    private void addIdeaToMap(Idea idea, IdeaController controller) {
        Point2D point = controller.getScenePointFromIdea(idea);
        PointSer pointSer = new PointSer(point.getX(), point.getY());

        paneSceneTrack.put(idea, pointSer);
    }

    Map<Idea, PointSer> getPaneSceneTrack() {
        return paneSceneTrack;
    }
}
