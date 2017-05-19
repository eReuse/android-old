package org.ereuse.scanner.services.api;

/**
 * Created by Jamgo SCCL.
 */
public class ActionResponse implements ApiResponse {
    public String body;
    public ActionType type;

    public enum ActionType {
        LOCATE,
        RECEIVE,
        RECYCLE,
        PLACE,
        SNAPSHOT,
        GENERIC
    }

    public void setActionType(ActionType type) {
        this.type = type;
    }

    public ActionType getActionType() {
        return this.type;
    }

    public String getBody() {
        return body;
    }


}

