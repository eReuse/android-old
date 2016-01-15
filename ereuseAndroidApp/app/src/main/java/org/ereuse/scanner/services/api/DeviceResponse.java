package org.ereuse.scanner.services.api;

/**
 * Created by Jamgo SCCL.
 */
public class DeviceResponse implements ApiResponse {

    private String _id;
    private String _etag;
    private String _created;
    private String _updated;
    private String hid;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String icon;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
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

    public String getModel() { return model; }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIcon() { return icon; }

    public void setIcon(String icon) { this.icon = icon; }
}
