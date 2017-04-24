package org.ereuse.scanner.services.api;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class EventUndoRequest implements ApiRequest {

    private String id;


    public EventUndoRequest(String eventId) {

        this.id = eventId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
