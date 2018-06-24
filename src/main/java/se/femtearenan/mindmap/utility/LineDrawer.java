package se.femtearenan.mindmap.utility;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import se.femtearenan.mindmap.model.Idea;
import se.femtearenan.mindmap.ui.IdeaController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LineDrawer {

    private IdeaController controller;
    private Scene scene;
    private Group ideaGroup;
    private Map<Idea, Map<Idea, Line>> acquaintanceLineMap;
    private Map<Idea, Line> ideaLineMap;

    public LineDrawer(IdeaController controller) {
        this.controller = controller;
        scene = controller.getScene();
        ideaGroup = controller.getIdeaGroup();
        acquaintanceLineMap = new HashMap<>();
        ideaLineMap = new HashMap<>();
    }

    public void addAcquaintanceLines(Idea idea) {
        Map<Idea, Line> lineMap = new HashMap<>();
        for (Idea acquaintance : idea.getAcquaintances().keySet()) {
            Line aLine = new Line();
            lineMap.put(acquaintance, aLine);
            ((Group) scene.getRoot()).getChildren().add(aLine);
        }
        acquaintanceLineMap.put(idea, lineMap);
    }

    public void addAcquaintanceLine(Idea origin, Idea acquaintance) {
        Line line = new Line();
        acquaintanceLineMap.get(origin).put(acquaintance, line);
        controller.addNodeToScene(line);
        drawAcquaintanceLines(origin);

    }

    public Line getAcquaintanceLine(Idea origin, Idea acquaintance) {
        return acquaintanceLineMap.get(origin).get(acquaintance);
    }

    public void removeAcquaintanceLine(Idea origin, Idea remove) {
        controller.removeNodeFromScene(getAcquaintanceLine(origin, remove));
        acquaintanceLineMap.get(origin).remove(remove);
    }

    public void addIdeaLine(Idea origin) {
        Line line = new Line();
        ideaLineMap.put(origin, line);
        ((Group)scene.getRoot()).getChildren().add(line);
    }

    private Line getIdeaLine(Idea origin) {
        return ideaLineMap.get(origin);
    }

    public void removeIdeaLine(Idea origin) {
        controller.removeNodeFromScene(getIdeaLine(origin));
        ideaLineMap.remove(origin);
    }

    public void hideLine(Idea origin) {
        Line line =  ideaLineMap.get(origin);
        line.setStartX(-10);
        line.setStartY(-10);
        line.setEndX(-10);
        line.setEndY(-10);
    }

    public void removeParentLines(Idea parent) {
        if (parent.getChildren().size() > 0) {
            for (Idea child : parent.getChildren()) {
                hideLine(child);
            }
        }
    }

    public void drawAcquaintanceLines(Idea idea) {
        if (idea.getAcquaintances().size() > 0) {
            Pane start = controller.getIdeaPaneFromGroup(idea.getTheme());

            for (Idea acquaintance : idea.getAcquaintances().keySet()) {

                Pane end = controller.getIdeaPaneFromGroup(acquaintance.getTheme());

                Line line = acquaintanceLineMap.get(idea).get(acquaintance);
                line.getStrokeDashArray().addAll(5.0, 5.0);

                Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
                Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

                // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
                double startX = startBoundsInScene.getMinX() + startBoundsInScene.getWidth() / 2;
                double startY = startBoundsInScene.getMinY() + startBoundsInScene.getHeight() / 2;
                double endX = endBoundsInScene.getMinX() + endBoundsInScene.getWidth() / 2;
                double endY = endBoundsInScene.getMinY() + endBoundsInScene.getHeight() / 2;

                // START is to the left of END
                if (startBoundsInScene.getMaxX() < endBoundsInScene.getMinX()) {
                    startX = startBoundsInScene.getMaxX();
                    endX = endBoundsInScene.getMinX();
                } else if (startBoundsInScene.getMinX() > endBoundsInScene.getMaxX()) { // START is to the right of END
                    startX = startBoundsInScene.getMinX();
                    endX = endBoundsInScene.getMaxX();
                } else {
                    // START is above the END
                    if (startBoundsInScene.getMaxY() < endBoundsInScene.getMinY()) {
                        startY = startBoundsInScene.getMaxY();
                        endY = endBoundsInScene.getMinY();
                    } else if (startBoundsInScene.getMinY() > endBoundsInScene.getMaxY()) { // START is below the END
                        startY = startBoundsInScene.getMinY();
                        endY = endBoundsInScene.getMaxY();
                    }
                }

                line.setStartX(startX);
                line.setStartY(startY);
                line.setEndX(endX);
                line.setEndY(endY);
            }
        }
    }

    public void drawIdeaLines(Idea idea) {
        if (idea.hasChildren()) {
            Set<Idea> children = idea.getChildren();

            Pane start = controller.getIdeaPaneFromGroup(idea.getTheme());

            for (Idea child : children) {

                Pane end = controller.getIdeaPaneFromGroup(child.getTheme());

                Line line = ideaLineMap.get(child);

                Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
                Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

                // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
                double startX = startBoundsInScene.getMinX() + startBoundsInScene.getWidth() / 2;
                double startY = startBoundsInScene.getMinY() + startBoundsInScene.getHeight() / 2;
                double endX = endBoundsInScene.getMinX() + endBoundsInScene.getWidth() / 2;
                double endY = endBoundsInScene.getMinY() + endBoundsInScene.getHeight() / 2;

                // START is to the left of END
                if (startBoundsInScene.getMaxX() < endBoundsInScene.getMinX()) {
                    startX = startBoundsInScene.getMaxX();
                    endX = endBoundsInScene.getMinX();
                } else if (startBoundsInScene.getMinX() > endBoundsInScene.getMaxX()) { // START is to the right of END
                    startX = startBoundsInScene.getMinX();
                    endX = endBoundsInScene.getMaxX();
                } else {
                    // START is above the END
                    if (startBoundsInScene.getMaxY() < endBoundsInScene.getMinY()) {
                        startY = startBoundsInScene.getMaxY();
                        endY = endBoundsInScene.getMinY();
                    } else if (startBoundsInScene.getMinY() > endBoundsInScene.getMaxY()) { // START is below the END
                        startY = startBoundsInScene.getMinY();
                        endY = endBoundsInScene.getMaxY();
                    }
                }

                line.setStartX(startX);
                line.setStartY(startY);
                line.setEndX(endX);
                line.setEndY(endY);
            }
        }
    }

    public void drawLinesOnUnpacking(Map<Idea, PointSer> pointmap) {
        for (Idea idea : ideaLineMap.keySet()) {
            if (idea.hasChildren()) {
                Set<Idea> children = idea.getChildren();

                Pane start = controller.getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);
                Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
                double startWidth = startBoundsInScene.getWidth();
                double startHeight = startBoundsInScene.getHeight();

                for (Idea child : children) {

                    Pane end = controller.getIdeaPaneFromGroup(child.getTheme(), ideaGroup);

                    Line line = ideaLineMap.get(child);

                    Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

                    // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
                    double endWidth = endBoundsInScene.getWidth();
                    double endHeight = endBoundsInScene.getHeight();

                    double startX = pointmap.get(idea).getX();
                    double startY = pointmap.get(idea).getY() + startHeight / 2;
                    double endX = pointmap.get(child).getX();
                    double endY = pointmap.get(child).getY() + endHeight / 2;

                    // START is to the left of END
                    if ((startX + startWidth)  < endX) {
                        startX += startWidth;
                    } else if (startX > (endX + endWidth)) { // START is to the right of END
                        endX += endWidth;
                    } else {
                        // START is above the END
                        if ((startY + startHeight/2) < endY) {
                            endY -= endHeight/2;
                            endX += endWidth/2;
                            startY += startHeight/2;
                            startX += startWidth/2;
                        } else if (startY > (endY + endHeight/2)) { // START is below the END
                            endY += endHeight/2;
                            endX += endWidth/2;
                            startY -= startHeight/2;
                            startX += startWidth/2;
                        }
                    }

                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(endX);
                    line.setEndY(endY);
                }
            }

            if (idea.getAcquaintances().size() > 0) {
                Pane start = controller.getIdeaPaneFromGroup(idea.getTheme(), ideaGroup);
                Bounds startBoundsInScene = start.localToScene(start.getBoundsInLocal());
                double startWidth = startBoundsInScene.getWidth();
                double startHeight = startBoundsInScene.getHeight();

                for (Idea acquaintance : idea.getAcquaintances().keySet()) {
                    Pane end = controller.getIdeaPaneFromGroup(acquaintance.getTheme(), ideaGroup);

                    Line line = getAcquaintanceLine(idea, acquaintance);
                    line.getStrokeDashArray().addAll(5.0, 5.0);

                    Bounds endBoundsInScene = end.localToScene(end.getBoundsInLocal());

                    // ADJUST START AND END DEPENDING ON WHERE THE SHAPES ARE IN RELATION TO EACH OTHER.
                    double endWidth = endBoundsInScene.getWidth();
                    double endHeight = endBoundsInScene.getHeight();

                    double startX = pointmap.get(idea).getX();
                    double startY = pointmap.get(idea).getY() + startHeight / 2;
                    double endX = pointmap.get(acquaintance).getX();
                    double endY = pointmap.get(acquaintance).getY() + endHeight / 2;

                    // START is to the left of END
                    if ((startX + startWidth) < endX) {
                        startX += startWidth;
                    } else if (startX > (endX + endWidth)) { // START is to the right of END
                        endX += endWidth;
                    } else {
                        // START is above the END
                        if ((startY + startHeight / 2) < endY) {
                            endY -= endHeight / 2;
                            endX += endWidth / 2;
                            startY += startHeight / 2;
                            startX += startWidth / 2;
                        } else if (startY > (endY + endHeight / 2)) { // START is below the END
                            endY += endHeight / 2;
                            endX += endWidth / 2;
                            startY -= startHeight / 2;
                            startX += startWidth / 2;
                        }
                    }

                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(endX);
                    line.setEndY(endY);
                }
            }
        }
    }

}
