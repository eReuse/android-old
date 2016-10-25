package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class Event implements Serializable {
    @SerializedName("_id")
    private String id;
    private String byUser;
    private List<String> devices;
    @SerializedName("_created")
    private String created;
    @SerializedName("@type")
    private String type;
    @SerializedName("_updated")
    private String updated;
    @SerializedName("_links")
    private Links links;
    private Point geo;
    private boolean incidence;
    private boolean secured;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getByUser() {
        return byUser;
    }

    public void setByUser(String byUser) {
        this.byUser = byUser;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Point getGeo() {
        return geo;
    }

    public void setGeo(Point geo) {
        this.geo = geo;
    }

    public boolean isIncidence() {
        return incidence;
    }

    public void setIncidence(boolean incidence) {
        this.incidence = incidence;
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public int getDevicesSize() {
        int size = 0;
        if (!CollectionUtils.isEmpty(this.getDevices())) {
            size = this.getDevices().size();
        }
        return size;
    }
}
