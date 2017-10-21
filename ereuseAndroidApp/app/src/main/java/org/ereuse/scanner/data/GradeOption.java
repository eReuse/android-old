package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class GradeOption implements Serializable {

    private String general;

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public GradeOption(String general) {
        this.general = general;
    }

}
