package canvas;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

public class BoundTrack {
    static final int TRACK_RESOLUTION_X = 30;
    static final int TRACK_RESOLUTION_Y = 20;
    private BoundTrackStatus[][] gridTrack = new BoundTrackStatus[TRACK_RESOLUTION_X][TRACK_RESOLUTION_Y];
    private double trackedWidth;
    private double trackedHeight;

    public BoundTrack(double trackedWidth, double trackedHeight) {
        initializeStatusArray();
        this.trackedWidth = trackedWidth;
        this.trackedHeight = trackedHeight;
    }

    private void initializeStatusArray() {
        for (int x = 0; x < TRACK_RESOLUTION_X; x++) {
            for (int y = 0; y < TRACK_RESOLUTION_Y; y++) {
                gridTrack[x][y] = BoundTrackStatus.FREE;
            }
        }
    }

    public void cleanBoundsTrack() {
        initializeStatusArray();
    }

    public void removeFromBoundsTrack(Bounds bounds) {
        changeBoundsTrack(bounds, BoundTrackStatus.FREE);
    }

    public void addOnBoundsTrack(Bounds bounds) {
        changeBoundsTrack(bounds, BoundTrackStatus.OCCUPIED);
    }

    private void changeBoundsTrack(Bounds bounds, BoundTrackStatus status) {
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
        return gridTrack[x][y] == BoundTrackStatus.FREE;
    }

    public boolean isCoordinateFree(double xCoordinate, double yCoordinate) {
        int x = getHorizontalBinNumberForXCoorinate(xCoordinate);
        int y = getVerticalBinNumberForYCoorinate(yCoordinate);
        return gridTrack[x][y] == BoundTrackStatus.FREE;
    }

    public boolean isBoundAreaFree(Bounds bounds) {
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
        return (trackedWidth / TRACK_RESOLUTION_X);
    }

    public double getVerticalBinSize() {
        return (trackedHeight / TRACK_RESOLUTION_Y);
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
                if  (gridTrack[x][y] == BoundTrackStatus.FREE) {
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
