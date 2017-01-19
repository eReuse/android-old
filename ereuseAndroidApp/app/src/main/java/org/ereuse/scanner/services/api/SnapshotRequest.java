package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Device;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotRequest implements ApiRequest {

<<<<<<< HEAD
    private Device device;

    @SerializedName("@type")
    private String type = "devices:Snapshot";

    private String comment;

    public SnapshotRequest(String user, String deviceType, String deviceSubType, String serialNumber, String model, String manufacturer, String licenseKey, String comment) {

        this.device = new Device();
        device.setDeviceType(deviceType);
        device.setDeviceSubType(deviceSubType);
=======
    @SerializedName("from.email")
    private String user;

    private Device device;


    private String licenseKey;

    private String comments;

    @SerializedName("@type")
    private String type = "Snapshot";

    public SnapshotRequest(String user, String serialNumber, String model, String manufacturer, String licenseKey, String comments) {

        this.user = user;
        this.licenseKey = licenseKey;

        this.device = new Device();
>>>>>>> 796d3447d82576983fc3284e0a2aa33b701553d7
        device.setSerialNumber(serialNumber);
        device.setModel(model);
        device.setManufacturer(manufacturer);

<<<<<<< HEAD
       this.comment = comment;
=======
        this.comments = comments;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
>>>>>>> 796d3447d82576983fc3284e0a2aa33b701553d7
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

<<<<<<< HEAD
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

=======
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
>>>>>>> 796d3447d82576983fc3284e0a2aa33b701553d7
}
