package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class Device implements Serializable {

    private String _id;
    @SerializedName("@type")
    private String type;
    private String serialNumber;
    private String hid;
    private double speed;
    private String _etag;
    private String _created;
    private String _updated;
    private String manufacturer;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String get_etag() {
        return _etag;
    }

    public void set_etag(String _etag) {
        this._etag = _etag;
    }

    public String get_created() {
        return _created;
    }

    public void set_created(String _created) {
        this._created = _created;
    }

    public String get_updated() {
        return _updated;
    }

    public void set_updated(String _updated) {
        this._updated = _updated;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Device)) return false;
        Device other = (Device) o;
        return this._id == other._id;
    }

    @Override
    public int hashCode() {
        return this._id.hashCode();
    }

}
