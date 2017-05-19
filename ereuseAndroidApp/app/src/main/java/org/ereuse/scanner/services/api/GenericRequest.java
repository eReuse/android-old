package org.ereuse.scanner.services.api;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class GenericRequest extends ActionRequest implements ApiRequest {


    @SerializedName("@type")
    private String type;

    public GenericRequest(User user, List<String> devicesList, String comment, String actionType) {
        this.setDevicesList(devicesList);
        this.setComment(comment);
        this.type = actionType;
    }


}
