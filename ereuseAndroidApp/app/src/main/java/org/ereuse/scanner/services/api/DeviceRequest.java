package org.ereuse.scanner.services.api;

/**
 * Created by Jamgo SCCL.
 */
public class DeviceRequest implements ApiRequest {
    private String deviceId;

    public DeviceRequest(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

}
