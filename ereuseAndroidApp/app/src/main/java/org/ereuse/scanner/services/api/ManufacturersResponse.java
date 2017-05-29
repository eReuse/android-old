package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Event;
import org.ereuse.scanner.data.Links;
import org.ereuse.scanner.data.Manufacturer;
import org.ereuse.scanner.data.Meta;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class ManufacturersResponse implements ApiResponse {

    @SerializedName("_meta")
    private Meta meta;

    @SerializedName("_links")
    private Links links;

    @SerializedName("_items")
    private List<Manufacturer> manufacturers;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public void setManufacturers(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public List<Manufacturer> getManufacturers() {
        return this.manufacturers;
    }

}
