package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Device;
import org.ereuse.scanner.data.DeviceComponent;
import org.ereuse.scanner.data.Grade;

/**
 * Created by Jamgo SCCL.
 */
public class DeviceComponentSnapshotRequest implements ApiRequest {

    @SerializedName("device")
    private DeviceComponent component;


    @SerializedName("@type")
    private String type = "devices:Snapshot";

    private String comment;

    private String snapshotSoftware = "Scan";

    @SerializedName("parent")
    private String parentSystemId;

    public DeviceComponentSnapshotRequest(String componentType, String parentSystemId, String serialNumber, String model, String manufacturer, String comment) {

        this.component = new DeviceComponent();
        this.component.setComponentType(componentType);

        this.component.setSerialNumber(serialNumber);
        this.component.setModel(model);
        this.component.setManufacturer(manufacturer);

        this.parentSystemId = parentSystemId;

        this.comment = comment;
    }

    public DeviceComponent getComponent() {
        return component;
    }

    public void setComponent(DeviceComponent component) {
        this.component = component;
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
}
