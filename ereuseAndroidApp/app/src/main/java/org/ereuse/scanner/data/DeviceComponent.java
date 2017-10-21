package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class DeviceComponent implements Serializable {

    @SerializedName("@type")
    private String componentType = "HardDrive";
   private String serialNumber;
    private String manufacturer;
    private String model;

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
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

}
