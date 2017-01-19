package org.ereuse.scanner.services.api;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class LocateRequest extends ActionRequest implements ApiRequest {

    private String place;

    @SerializedName("@type")
    private String type = "Locate";

    public LocateRequest(User user, List<String> devicesList, String comment, Location location, String place) {
        super(user, devicesList, comment, location);
        this.place = place;
    }
 
    public String getPlace() {
        return place;
    }
}
