package org.ereuse.scanner.services.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jamgo SCCL on 19/05/17.
 */

public class GenericEventType {

    public static final Map<String,String> GENERIC_EVENT_PATH_MAP = new HashMap<String,String>();
    static {
        GENERIC_EVENT_PATH_MAP.put("Ready","ready");
        GENERIC_EVENT_PATH_MAP.put("Repair","repair");
        GENERIC_EVENT_PATH_MAP.put("ToPrepare","to-prepare");
        GENERIC_EVENT_PATH_MAP.put("ToRepair","to-repair");
        GENERIC_EVENT_PATH_MAP.put("ToDispose","to-dispose");
        GENERIC_EVENT_PATH_MAP.put("Dispose","dispose");
        GENERIC_EVENT_PATH_MAP.put("Free","free");
    }

    public static final Map<String,String> GENERIC_EVENT_LABEL_MAP = new HashMap<String,String>();
    static {
        GENERIC_EVENT_LABEL_MAP.put("Ready","Ready");
        GENERIC_EVENT_LABEL_MAP.put("Repair","Repair");
        GENERIC_EVENT_LABEL_MAP.put("ToPrepare","To Prepare");
        GENERIC_EVENT_LABEL_MAP.put("ToRepair","To Repair");
        GENERIC_EVENT_LABEL_MAP.put("ToDispose","To Dispose");
        GENERIC_EVENT_LABEL_MAP.put("Dispose","Dispose");
        GENERIC_EVENT_LABEL_MAP.put("Free","Free");
    }
}
