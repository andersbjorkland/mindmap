package canvas;

import javafx.geometry.Bounds;

public class BoundTrack {
    public static final int TRACK_RESOLUTION_X = 30;
    public static final int TRACK_RESOLUTION_Y = 20;
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
        int minX = getHorizontalBinNumberForXCoordinate(bounds.getMinX());
        int maxX = getHorizontalBinNumberForXCoordinate(bounds.getMaxX());
        int minY = getVerticalBinNumberForYCoordinate(bounds.getMinY());
        int maxY = getVerticalBinNumberForYCoordinate(bounds.getMaxY());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                gridTrack[x][y] = status;
            }
        }
    }

    public boolean isBinFree(int x, int y) {
        return gridTrack[x][y] == BoundTrackStatus.FREE;
    }

    public boolean isBoundAreaFree(Bounds bounds) {
        return isCoordinateAreaFree(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
    }

    public boolean isCoordinateAreaFree(double coordinateMinX, double coordinateMinY, double coordinateMaxX, double coordinateMaxY) {
        int minX = getHorizontalBinNumberForXCoordinate(coordinateMinX);
        int maxX = getHorizontalBinNumberForXCoordinate(coordinateMaxX);
        int minY = getVerticalBinNumberForYCoordinate(coordinateMinY);
        int maxY = getVerticalBinNumberForYCoordinate(coordinateMaxY);

        return isBinAreaFree(minX, minY, maxX, maxY);
    }

    private boolean isBinAreaFree(int minX, int minY, int maxX, int maxY) {
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

    public int getHorizontalBinNumberForXCoordinate(double xCoordinate) {
        return (int) (xCoordinate / getHorizontalBinSize());
    }

    public int getVerticalBinNumberForYCoordinate(double yCoordinate) {
        return (int) (yCoordinate / getVerticalBinSize());
    }


}
