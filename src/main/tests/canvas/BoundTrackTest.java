package canvas;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoundTrackTest {
    private BoundTrack track;
    private Bounds bounds;

    @Before
    public void setUp() {
        double trackedWidth = 600;
        double trackedHeight = 600;
        double xCoordinate = 10;
        double yCoordinate = 10;
        double boundsWidth = 30;
        double boundsHeight = 20;

        track = new BoundTrack(trackedWidth, trackedHeight);
        bounds = new BoundingBox(xCoordinate, yCoordinate, boundsWidth, boundsHeight);

    }

    @Test
    public void everyCellIsFreeUponConstruction() {
        boolean isFree = true;
        for (int x = 0; x < BoundTrack.TRACK_RESOLUTION_X; x++) {
            for (int y = 0; y < BoundTrack.TRACK_RESOLUTION_Y; y++) {
                isFree = track.isBinFree(x,y) ? isFree : false;
            }
        }

        assertTrue(isFree);
    }


    @Test
    public void freeAreaIsReportedAsFree() {
        assertTrue(track.isBoundAreaFree(bounds));
    }

    @Test
    public void occupiedAreaIsReportedAsOccupied() {
        track.cleanBoundsTrack();
        track.addOnBoundsTrack(bounds);
        assertFalse(track.isBoundAreaFree(bounds));
    }

    @Test
    public void removingBoundsFromTrackIsReportedAsFree() {
        track.cleanBoundsTrack();
        track.addOnBoundsTrack(bounds);
        track.removeFromBoundsTrack(bounds);

        assertTrue(track.isBoundAreaFree(bounds));
    }

    @Test
    public void convertingXCoordinateToCorrectBinXCoordinate() {
        double binSize = track.getHorizontalBinSize();
        double xCoord = 20;
        int expectedXBin = (int)(xCoord/binSize);
        assertEquals(expectedXBin, track.getHorizontalBinNumberForXCoordinate(xCoord));
    }

    @Test
    public void convertingYCoordinateToCorrectBinYCoordinate() {
        double binSize = track.getVerticalBinSize();
        double yCoord = 20;
        int expectedYBin = (int)(yCoord/binSize);
        assertEquals(expectedYBin, track.getVerticalBinNumberForYCoordinate(yCoord));
    }
}
