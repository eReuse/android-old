package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Device;
import org.ereuse.scanner.data.Grade;
import org.ereuse.scanner.data.GradeOption;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotRequest implements ApiRequest {

//    @SerializedName("from.email")
//    private String user;

    private Device device;

    private Grade condition;

    @SerializedName("@type")
    private String type = "devices:Snapshot";

   // private String licenseKey;

    private String comment;

    private String snapshotSoftware = "Scan";

    public SnapshotRequest(String user, String deviceType, String deviceSubType, String serialNumber, String model, String manufacturer, String licenseKey, String giverId, String refurbisherId, String systemId, String comment, Grade gradeConditions) {

        this.device = new Device();
        device.setDeviceType(deviceType);
        device.setDeviceSubType(deviceSubType);

        device.setSerialNumber(serialNumber);
        device.setModel(model);
        device.setManufacturer(manufacturer);

        this.device.setGiverId(giverId);
        this.device.setRefurbisherId(refurbisherId);
        this.device.setSystemId(systemId);

        this.condition = gradeConditions;

//        this.user = user;
//        this.licenseKey = licenseKey;

        this.comment = comment;
    }
/*
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
*/
/*    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
*/
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comments) {
        this.comment = comment;
    }
}
