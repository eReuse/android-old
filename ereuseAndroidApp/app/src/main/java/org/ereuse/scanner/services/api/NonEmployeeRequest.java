package org.ereuse.scanner.services.api;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class NonEmployeeRequest extends ActionRequest implements ApiRequest {


    private String receiver;
    private boolean acceptedConditions;
    private String type;

    @SerializedName("@type")
    protected String arrobatype = "Receive";

    public NonEmployeeRequest(User user, List<String> devicesList, String message, Location location, boolean acceptedConditions, String type) {
        super(user, devicesList, message, location);
        this.acceptedConditions = acceptedConditions;
        this.receiver = user.get_id();
        this.type = type;
    }

    public boolean getAcceptedConditions() {
        return this.acceptedConditions;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public String getType() {
        return this.type;
    }
}
