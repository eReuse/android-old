package org.ereuse.scanner.services.api;

import android.location.Location;

import org.ereuse.scanner.data.Point;
import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class ActionRequest implements ApiRequest {

    private List<String> devices;
    private String comment;
    private Point geo;

    public static final String RECEIVE_REQUEST_TYPE = "FinalUser";
    public static final String RECYCLE_REQUEST_TYPE = "CollectionPoint";

    public ActionRequest(User user, List<String> devicesList, String comment, Location location) {
        this.devices = devicesList;
        this.comment = comment;
        if (location != null)
            this.geo = new Point(location);
        else this.geo = null;
    }

    public List<String> getDevicesList() {
        return this.devices;
    }

    public String getComment() {
        return this.comment;
    }

    public Point getLocation() {
        return this.geo;
    }
}
