package canvas;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PaneTrackTest {
    private PaneTrack track;

    @Before
    public void setUp() {
        track = new PaneTrack(600, 600);
    }

    @Test
    public void everyCellIsFreeUponConstruction() {
        boolean isFree = true;
        for (int x = 0; x < PaneTrack.TRACK_RESOLUTION_X; x++) {
            for (int y = 0; y < PaneTrack.TRACK_RESOLUTION_Y; y++) {
                isFree = track.isBinFree(x,y) ? isFree : false;
            }
        }

        assertTrue(isFree);
    }


    public void freeAreaIsReportedAsFree() {

    }
}
