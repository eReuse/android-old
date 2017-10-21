package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class Manufacturer implements Serializable {

    private String label;

    @SerializedName("_links")
    private Links links;
    @SerializedName("_updated")
    private String updated;
    private String logo;
    private String url;
    @SerializedName("_created")
    private String created;
    @SerializedName("_id")
    private String id;


    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
