package org.ereuse.scanner.utils;

/**
 * Created by martin on 18/05/17.
 */

public class ScanUtils {

    // A scanned SystemId is a full DeviceHub URL, we must extract the device Id
    public static String getSystemIdFromUrl(String scannedCode) {
        String[] splittedUrl = scannedCode.split("/");
        if (splittedUrl.length > 1) {
            return splittedUrl[splittedUrl.length -1 ];
        }
        return scannedCode;
    }

}
