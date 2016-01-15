package org.ereuse.scanner.data;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class Polygon {
    private List<List<List<Double>>> coordinates;
    private final String type = "Polygon";

    public void setCoordinates(List<LatLng> locations){
        coordinates = new ArrayList<List<List<Double>>>();

        List<List<Double>> coordinatesList = new ArrayList<List<Double>>();
        for (LatLng location :locations) {
            List<Double> locationCoordinates = new ArrayList<Double>();
            locationCoordinates.add(location.longitude);
            locationCoordinates.add(location.latitude);
            coordinatesList.add(locationCoordinates);
        }
        coordinates.add(coordinatesList);
    }

    public List<List<List<Double>>> getCoordinates() {
         return coordinates;
    }

    public String getType() {
        return type;
    }

    public Polygon(List<LatLng> locations){
        this.setCoordinates(locations);
    }
}
