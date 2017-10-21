package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.DeviceComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class DeviceComponentRemoveRequest implements ApiRequest {

    @SerializedName("@type")
    private String type = "devices:Remove";

    @SerializedName("device")
    private String parentSystemId;
    @SerializedName("components")
    private List<String> componentSystemIds;
    private String comment;


    public DeviceComponentRemoveRequest(String parentSystemId, String componentSystemId, String comment) {

        this.parentSystemId = parentSystemId;
        this.componentSystemIds = Arrays.asList(componentSystemId);
        this.comment = comment;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comments) {
        this.comment = comment;
    }

    public String getParentSystemId() {
        return parentSystemId;
    }

    public void setParentSystemId(String parentSystemId) {
        this.parentSystemId = parentSystemId;
    }

    public List<String> getComponentSystemIds() { return componentSystemIds; }

    public void setComponentSystemIds(List<String> componentSystemIds) { this.componentSystemIds = componentSystemIds; }
}
