package org.ereuse.scanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

public class GlobalTestParams {

    public static final String DEVICEHUB_SERVER = "http://devicehub.ereuse.net/";
    public static final List<String> DEVICES_LIST = Arrays.asList("472");

    public static final String DEMO_USER_EMAIL = "a@a.a";
    public static final String DEMO_USER_PASSWORD = "1234";
    public static final String DEMO_USER_TOKEN = "Tk9GQVRETk5VQjo=";
    public static final String DEFAULT_DB = "db1";
    public static final Map<String, String> DATABASES = new HashMap<String, String>() {{
        put("db1", "ac");
    }};

    public static final String GENERIC_EVENT_TEST_TYPE = "Ready";
}
