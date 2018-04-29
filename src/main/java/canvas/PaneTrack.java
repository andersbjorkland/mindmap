package canvas;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

public class PaneTrack {
    static final int TRACK_RESOLUTION_X = 30;
    static final int TRACK_RESOLUTION_Y = 20;
    private PaneTrackStatus[][] gridTrack = new PaneTrackStatus[TRACK_RESOLUTION_X][TRACK_RESOLUTION_Y];
    private double trackedSceneWidth;
    private double trackedSceneHeight;

    public PaneTrack(double trackedSceneWidth, double trackedSceneHeight) {
        initializeStatusArray();
        this.trackedSceneWidth = trackedSceneWidth;
        this.trackedSceneHeight = trackedSceneHeight;
    }

    private void initializeStatusArray() {
        for (int x = 0; x < TRACK_RESOLUTION_X; x++) {
            for (int y = 0; y < TRACK_RESOLUTION_Y; y++) {
                gridTrack[x][y] = PaneTrackStatus.FREE;
            }
        }
    }

    public void cleanPaneTrack() {
        initializeStatusArray();
    }

    public void removePaneTrack(Pane pane) {
        changePaneTrack(pane, PaneTrackStatus.FREE);
    }

    public void addOnPaneTrack(Pane pane) {
        changePaneTrack(pane, PaneTrackStatus.OCCUPIED);
    }

    private void changePaneTrack(Pane pane, PaneTrackStatus status) {
        Bounds bounds = retrievePaneBounds(pane);
        int minX = getHorizontalBinNumberForXCoorinate(bounds.getMinX());
        int maxX = getHorizontalBinNumberForXCoorinate(bounds.getMaxX());
        int minY = getVerticalBinNumberForYCoorinate(bounds.getMinY());
        int maxY = getVerticalBinNumberForYCoorinate(bounds.getMaxY());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                gridTrack[x][y] = status;
            }
        }
    }

    private Bounds retrievePaneBounds(Pane pane) {
        return  pane.localToScene(pane.getBoundsInLocal());
    }

    public boolean isBinFree(int x, int y) {
        return gridTrack[x][y] == PaneTrackStatus.FREE;
    }

    public boolean isCoordinateFree(double xCoordinate, double yCoordinate) {
        int x = getHorizontalBinNumberForXCoorinate(xCoordinate);
        int y = getVerticalBinNumberForYCoorinate(yCoordinate);
        return gridTrack[x][y] == PaneTrackStatus.FREE;
    }

    public boolean isPaneAreaFree(Pane pane) {
        Bounds bounds = retrievePaneBounds(pane);

        return isCoordinateAreaFree(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
    }

    public boolean isCoordinateAreaFree(double coordinateMinX, double coordinateMinY, double coordinateMaxX, double coordinateMaxY) {
        int minX = getHorizontalBinNumberForXCoorinate(coordinateMinX);
        int maxX = getHorizontalBinNumberForXCoorinate(coordinateMaxX);
        int minY = getVerticalBinNumberForYCoorinate(coordinateMinY);
        int maxY = getVerticalBinNumberForYCoorinate(coordinateMaxY);

        return isBinAreaFree(minX, minY, maxX, maxY);
    }

    public boolean isBinAreaFree(int minX, int minY, int maxX, int maxY) {
        boolean isBinAreaFree = true;

        int x = minX;
        int y = minY;
        while (isBinAreaFree && x < maxX) {
            while (isBinAreaFree && y < maxY) {
                if (!isBinFree(x, y)) {
                    isBinAreaFree = false;
                }
                y++;
            }
            x++;
            y = minY;
        }

        return isBinAreaFree;
    }

    public double getHorizontalBinSize() {
        return (trackedSceneWidth / TRACK_RESOLUTION_X);
    }

    public double getVerticalBinSize() {
        return (trackedSceneHeight / TRACK_RESOLUTION_Y);
    }

    private int getHorizontalBinNumberForXCoorinate(double xCoordinate) {
        return (int) (xCoordinate / getHorizontalBinSize());
    }

    private int getVerticalBinNumberForYCoorinate(double yCoordinate) {
        return (int) (yCoordinate / getVerticalBinSize());
    }

    public String representTrackOnPane(Pane pane) {
        Bounds bounds = retrievePaneBounds(pane);
        String trackPaneString = "Coordinates: [" + bounds.getMinX() + ":" + bounds.getMinY() + "]\n" +
                "Bins: [" + getHorizontalBinNumberForXCoorinate(bounds.getMinX()) + ":" +
                getVerticalBinNumberForYCoorinate(bounds.getMinY()) + "]";

        return trackPaneString;
    }

    public String representTracks() {
        String tracks = "";
        for (int y = 0; y < TRACK_RESOLUTION_Y; y++) {
            for (int x = 0; x < TRACK_RESOLUTION_X; x++) {
                if  (gridTrack[x][y] == PaneTrackStatus.FREE) {
                    tracks += " - ";
                } else {
                    tracks += " + ";
                }
            }
            tracks += "\n";
        }
        return tracks;
    }
}
