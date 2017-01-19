package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Link;
import org.ereuse.scanner.data.Links;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotResponse implements ApiResponse {

    @SerializedName("_id")
    private String id;

    @SerializedName("_created")
    private String created;

    @SerializedName("_updated")
    private String updated;

    @SerializedName("_status")
    private String status;

    @SerializedName("_links")
    private Link link;

    @SerializedName("@type")
    private String type;

    public String get_id() {
        return id;
    }

    public void set_id(String id) {
        this.id = id;
    }

    public String get_created() {
        return created;
    }

    public void set_created(String _created) {
        this.created = _created;
    }

    public String get_updated() {
        return updated;
    }

    public void set_updated(String _updated) {
        this.updated = _updated;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }

    public Link getLink() { return link; }

    public void setLink(Link link)
    {
        this.link = new Link();
        this.link.setHref(link.getHref());
        this.link.setTitle(link.getTitle());
    }

    public String getType() { return type; }

    public void setType(String type) {
        this.type = type;
    }
}
