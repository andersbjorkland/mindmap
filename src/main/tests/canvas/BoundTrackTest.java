package canvas;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoundTrackTest {
    private BoundTrack track;
    private Bounds bounds;

    @Before
    public void setUp() {
        track = new BoundTrack(600, 600);
        bounds = new BoundingBox(10, 10, 30, 20);

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
        track.addOnBoundsTrack(bounds);
        assertFalse(track.isBoundAreaFree(bounds));
    }

    @Test
    public void removingBoundsFromTrackIsReportedAsFree() {
        track.removeFromBoundsTrack(bounds);
        assertTrue(track.isBoundAreaFree(bounds));
    }
}
