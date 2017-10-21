package ereuse.org.scanner;

import org.ereuse.scanner.utils.ScanUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UtilsTest {
    @Test
    public void scannedDeviceUrlTest() throws Exception {
        assertEquals("2405", ScanUtils.getSystemIdFromUrl("http://devicehub.ereuse.net/devices/2405"));

    }
}