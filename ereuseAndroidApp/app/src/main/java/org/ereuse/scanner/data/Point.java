package org.ereuse.scanner.data;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class Point implements Serializable {
    private final String type = "Point";
    private List<Double> coordinates = new ArrayList<Double>();

    public void setCoordinates(Location location) {
        this.coordinates.clear();
        this.coordinates.add(location.getLongitude());
        this.coordinates.add(location.getLatitude());
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }

    public Point(Location location) {
        this.setCoordinates(location);
    }
}
