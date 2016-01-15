package org.ereuse.scanner.services.api;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class EmployeeRequest extends ActionRequest implements ApiRequest {


    protected class UnregisteredReceiver {
        protected String email;
    }

    private UnregisteredReceiver unregisteredReceiver = new UnregisteredReceiver();
    private boolean acceptedConditions;
    private String type;

    @SerializedName("@type")
    protected String arrobatype = "Receive";

    public boolean getAcceptedConditions() {
        return this.acceptedConditions;
    }

    public EmployeeRequest(User user, String unregisteredReceiver, List<String> devicesList, String comment, Location location, boolean acceptedConditions, String type) {
        super(user, devicesList, comment, location);
        this.acceptedConditions = acceptedConditions;
        this.unregisteredReceiver.email = unregisteredReceiver;
        this.type = type;

    }

    public String getUnregisteredReceiver() {
        return this.unregisteredReceiver.email;
    }

    public String getType() {
        return this.type;
    }
}
