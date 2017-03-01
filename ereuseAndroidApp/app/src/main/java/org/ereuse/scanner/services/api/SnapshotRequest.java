package org.ereuse.scanner.services.api;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Device;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotRequest implements ApiRequest {

//    @SerializedName("from.email")
//    private String user;

    private Device device;

    @SerializedName("@type")
    private String type = "devices:Snapshot";

    // private String licenseKey;

    private String comment;

    private String snapshotSoftware = "Scan";

    public SnapshotRequest(String user, String deviceType, String deviceSubType, String serialNumber, String model, String manufacturer, String licenseKey, String giverId, String refurbisherId, String systemId, String comment) {

        this.device = new Device();
        device.setDeviceType(deviceType);
        device.setDeviceSubType(deviceSubType);
        if (this.isNotBlank(serialNumber)) device.setSerialNumber(serialNumber);
        if (this.isNotBlank(model)) device.setModel(model);
        if (this.isNotBlank(manufacturer)) device.setManufacturer(manufacturer);

        if (this.isNotBlank(giverId)) this.device.setGiverId(giverId);
        if (this.isNotBlank(refurbisherId)) this.device.setRefurbisherId(refurbisherId);
        if (this.isNotBlank(systemId)) this.device.setSystemId(systemId);

//        this.user = user;
//        this.licenseKey = licenseKey;

        this.comment = comment;
    }

    private boolean isNotBlank(String string) {
        return !TextUtils.isEmpty(string) && TextUtils.getTrimmedLength(string) > 0;
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
