package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import org.ereuse.scanner.data.Link;

/**
 * Created by Jamgo SCCL.
 */
public class EventUndoResponse implements ApiResponse {


    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
