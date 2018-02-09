package org.ereuse.scanner.services.api;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.User;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class EmployeeRequest extends ActionRequest implements ApiRequest {


    protected class Receiver {
        protected String email;
    }

    private Receiver receiver = new Receiver();
    private boolean acceptedConditions;
    private String type;

    @SerializedName("@type")
    protected String arrobatype = "Receive";

    public boolean getAcceptedConditions() {
        return this.acceptedConditions;
    }

    public EmployeeRequest(User user, String receiver, String label, List<String> devicesList, String comment, Location location, boolean acceptedConditions, String type) {
        super(user, label, devicesList, comment, location);
        this.acceptedConditions = acceptedConditions;
        this.receiver.email = receiver;
        this.type = type;

    }

    public String getReceiver() {
        return this.receiver.email;
    }

    public String getType() {
        return this.type;
    }
}
