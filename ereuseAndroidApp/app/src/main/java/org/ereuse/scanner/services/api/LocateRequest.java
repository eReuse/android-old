package org.ereuse.scanner.services.api;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class LocateRequest extends ActionRequest implements ApiRequest {


    @SerializedName("@type")
    private String type = "Locate";

    public LocateRequest(User user, String label, List<String> devicesList, String comment, Location location) {
        super(user, label, devicesList, comment, location);
    }


}
