package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class Grade implements Serializable {

    private GradeOption appearance;
    private GradeOption functionality;
    private GradeOption bios;
    private boolean labelling;

    public GradeOption getAppearance() {
        return appearance;
    }
    public void setAppearance(GradeOption appearance) {
        this.appearance = appearance;
    }
    public GradeOption getFunctionality() {
        return functionality;
    }
    public void setFunctionality(GradeOption functionality) {
        this.functionality = functionality;
    }
    public GradeOption getBios() {
        return bios;
    }
    public void setBios(GradeOption bios) {
        this.bios = bios;
    }
    public boolean isLabelling() {
        return labelling;
    }
    public void setLabelling(boolean labelling) {
        this.labelling = labelling;
    }

}
