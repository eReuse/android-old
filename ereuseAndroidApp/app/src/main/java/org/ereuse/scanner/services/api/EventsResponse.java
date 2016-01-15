package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Event;
import org.ereuse.scanner.data.Links;
import org.ereuse.scanner.data.Meta;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class EventsResponse implements ApiResponse {
    @SerializedName("_meta")
    private Meta meta;

    @SerializedName("_items")
    private List<Event> events;

    @SerializedName("_links")
    private Links links;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

}
