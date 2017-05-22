package org.ereuse.scanner.services.api;

import android.location.Location;

import org.ereuse.scanner.data.Point;
import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class ActionRequest implements ApiRequest {

    private String label;
    private List<String> devices;
    private String comment;
    private Point geo;

    public static final String RECEIVE_REQUEST_TYPE = "FinalUser";
    public static final String RECYCLE_REQUEST_TYPE = "CollectionPoint";

    public ActionRequest() {}

    public ActionRequest(User user, String label, List<String> devicesList, String comment, Location location) {
        this.label = label;
        this.devices = devicesList;
        this.comment = comment;
        this.geo = new Point(location);
    }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }
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
