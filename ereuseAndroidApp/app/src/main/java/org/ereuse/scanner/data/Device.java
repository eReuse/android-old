package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class Device implements Serializable {

    @SerializedName("@type")
    private String deviceType = "Device";
    @SerializedName("type")
    private String deviceSubType = "Smartphone";
   private String serialNumber;
    private String manufacturer;
    private String model;
    @SerializedName("pid")
    private String giverId;
    @SerializedName("rid")
    private String refurbisherId;
    @SerializedName("_id")
    private String systemId;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceSubType() {
        return deviceSubType;
    }

    public void setDeviceSubType(String deviceSubType) {
        this.deviceSubType = deviceSubType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGiverId() { return giverId; }

    public void setGiverId(String giverId) { this.giverId = giverId; }

    public String getSystemId() { return systemId; }

    public void setSystemId(String systemId) { this.systemId = systemId; }

    public String getRefurbisherId() { return refurbisherId; }

    public void setRefurbisherId(String refurbisherId) { this.refurbisherId = refurbisherId; }
}
