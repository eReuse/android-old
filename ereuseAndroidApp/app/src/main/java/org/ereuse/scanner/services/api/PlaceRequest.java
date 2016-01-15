package org.ereuse.scanner.services.api;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Polygon;
import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class PlaceRequest implements ApiRequest {

    private String label;
    private Polygon geo;

    @SerializedName("@type")
    private String type = "Place";

    public PlaceRequest(User user, String label, List<LatLng> locations) {
        this.label = label;
        this.geo = new Polygon(locations);
    }

    public String getLabel() {
        return this.label;
    }

    public Polygon getGeo() {
        return this.geo;
    }
}