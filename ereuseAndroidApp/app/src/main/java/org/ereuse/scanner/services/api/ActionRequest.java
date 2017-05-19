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

    public ActionRequest() {}

    public ActionRequest(User user, List<String> devicesList, String comment, Location location) {
        this.devices = devicesList;
        this.comment = comment;
        this.geo = new Point(location);
    }

    public List<String> getDevicesList() {
        return this.devices;
    }
    public void setDevicesList(List<String> devicesList) {
        this.devices = devicesList;
    }
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) { this.comment = comment; }

    public Point getLocation() {
        return this.geo;
    }
}
