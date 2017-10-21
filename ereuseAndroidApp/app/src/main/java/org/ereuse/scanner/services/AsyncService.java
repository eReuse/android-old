package org.ereuse.scanner.services;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.common.StringUtils;

import org.ereuse.scanner.activities.AsyncActivity;
import org.ereuse.scanner.data.Grade;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.api.ActionRequest;
import org.ereuse.scanner.services.api.ApiException;
import org.ereuse.scanner.services.api.ApiRequest;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.ApiServices;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.services.api.DeviceComponentRemoveRequest;
import org.ereuse.scanner.services.api.DeviceComponentSnapshotRequest;
import org.ereuse.scanner.services.api.DeviceRequest;
import org.ereuse.scanner.services.api.EmployeeRequest;
import org.ereuse.scanner.services.api.EventUndoRequest;
import org.ereuse.scanner.services.api.GenericRequest;
import org.ereuse.scanner.services.api.LocateRequest;
import org.ereuse.scanner.services.api.LoginRequest;
import org.ereuse.scanner.services.api.NonEmployeeRequest;
import org.ereuse.scanner.services.api.PlaceRequest;
import org.ereuse.scanner.services.api.PlainRequest;
import org.ereuse.scanner.services.api.SnapshotRequest;

import java.util.List;


/**
 * Created by Jamgo SCCL.
 */
public class AsyncService {

    private AsyncActivity activity;
    private ApiResponse response;
    private ApiException exception;

    public static final String ERROR_NO_SUCH_PLACE = "NoPlaceForGivenCoordinates";

    public void setResponse(ApiResponse response) {
        this.response = response;
    }

    public void setException(ApiException exception) {
        this.exception = exception;
    }

    public AsyncService(AsyncActivity activity) {
        this.activity = activity;
    }

    public void doLogin(String email, String password, String server) {
        this.activity.onStartAsync();
        try {
            LoginRequest request = new LoginRequest(email, password);
            new HttpRequestTask(this, server, request).execute(ApiServices.METHOD_LOGIN);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void getDevices(String server, String token) {
        this.activity.onStartAsync();

        DeviceRequest request = new DeviceRequest(null);
        new HttpRequestTask(this, server, request).execute(ApiServices.METHOD_DEVICE);
    }

    public void getDevice(String server, String token, String deviceId) {
        this.activity.onStartAsync();

        DeviceRequest request = new DeviceRequest(deviceId);
        new HttpRequestTask(this, server, token, request).execute(ApiServices.METHOD_DEVICE);
    }

    public void doLocate(String server, User user, String eventLabel, List<String> devicesList, String comment, Location location) {
        this.activity.onStartAsync();

        LocateRequest request = new LocateRequest(user, eventLabel, devicesList, comment, location);
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_LOCATE);
    }

    public void doGeneric(String server, User user, String eventLabel, List<String> devicesList, String comment, String actionType) {
        this.activity.onStartAsync();

        GenericRequest request = new GenericRequest(user, eventLabel, devicesList, comment, actionType);
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_GENERIC_EVENT, actionType);
    }

    public void doReceive(String server, User user, String unregisteredReceiver, Location location, String eventLabel, List<String> devicesList, String comment, boolean acceptedConditions) {
        this.activity.onStartAsync();

        ApiRequest request = null;
        if (user.isEqualOrGreaterThanEmployee() && unregisteredReceiver != null && !unregisteredReceiver.isEmpty()) {
            request = new EmployeeRequest(user, unregisteredReceiver, eventLabel, devicesList, comment, location, acceptedConditions, ActionRequest.RECEIVE_REQUEST_TYPE);
        } else {
            request = new NonEmployeeRequest(user, eventLabel, devicesList, comment, location, acceptedConditions, ActionRequest.RECEIVE_REQUEST_TYPE);
        }
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_RECEIVE);
    }

    public void doRecycle(String server, User user, String unregisteredReceiver, Location location, String eventLabel, List<String> devicesList, String comment, boolean acceptedConditions) {
        this.activity.onStartAsync();

        ApiRequest request = null;
        if (user.isEqualOrGreaterThanEmployee() && unregisteredReceiver != null && !unregisteredReceiver.isEmpty()) {
            request = new EmployeeRequest(user, unregisteredReceiver, eventLabel, devicesList, comment, location, acceptedConditions, ActionRequest.RECYCLE_REQUEST_TYPE);
        } else {
            request = new NonEmployeeRequest(user, eventLabel, devicesList, comment, location, acceptedConditions, ActionRequest.RECYCLE_REQUEST_TYPE);
        }
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_RECYCLE);
    }

    public void doPlace(String server, User user, List<LatLng> locations, String label) {
        this.activity.onStartAsync();

        PlaceRequest request = new PlaceRequest(user, label, locations);
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_PLACE);
    }

    public void doEvents(String server, User user) {
        this.activity.onStartAsync();

        new HttpRequestTask(this, server, user.getToken(), null).execute(ApiServices.METHOD_EVENTS);
    }

    public void doSnapshot(String server, User user, String deviceType, String deviceSubType,
                           String serialNumber, String model, String manufacturer,
                           String licenseKey, String giverId, String refurbisherId, String systemId, String comment, Grade gradeConditions) {
        this.activity.onStartAsync();

        SnapshotRequest request = new SnapshotRequest(user.getEmail(), deviceType, deviceSubType, serialNumber, model, manufacturer, licenseKey, giverId, refurbisherId, systemId, comment, gradeConditions);
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_SNAPSHOT);
    }

    public void doLinkComponentToParentSnapshot(String server, User user, String componentType, String parentSystemId,
                                          String serialNumber, String model, String manufacturer, String comment) {

        DeviceComponentSnapshotRequest request = new DeviceComponentSnapshotRequest(componentType, parentSystemId, serialNumber, model, manufacturer, comment);
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_SNAPSHOT);


    }

    public void undoLinkComponentToParentRemoveComponentFromParent(String server, User user, String eventToUndo) {
        EventUndoRequest request = new EventUndoRequest(eventToUndo);
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_EVENT_UNDO);

    }


    public void doRemoveComponentFromParent(String server, User user, String parentSystemId, String componentSystemId, String comment) {

        DeviceComponentRemoveRequest request = new DeviceComponentRemoveRequest(parentSystemId, componentSystemId, comment);
        new HttpRequestTask(this, server, user.getToken(), request).execute(ApiServices.METHOD_DEVICE_COMPONENT_REMOVE);


    }

    public void getManufacturers(String server, User user) {
        getManufacturers(server, user, null);
    }

    public void getManufacturers(String server, User user, String href) {

        String[] servicePath;
        if (href == null) {
            servicePath = new String[]{ApiServices.METHOD_MANUFACTURERS};
        } else {
            servicePath = href.split("\\?");
        }
/*        String servicePath = href;
        if (servicePath == null) {
            servicePath = ApiServices.METHOD_MANUFACTURERS;
        }*/
        this.activity.onStartAsync();

        PlainRequest request = new PlainRequest();
        new HttpRequestTask(this, server, user.getToken(), request).execute(servicePath);
    }

    public void finished() {
        if (this.response != null) {
            this.activity.onSuccess(this.response);
        } else {
            this.activity.onError(this.exception);
        }
    }

    private static class HttpRequestTask extends AsyncTask<String, Void, ApiResponse> {
        private AsyncService service;
        private String server;
        private String token;
        private ApiRequest request;

        public HttpRequestTask(AsyncService service, String server, ApiRequest request) {
            this.service = service;
            this.server = server;
            this.request = request;
        }

        public HttpRequestTask(AsyncService service, String server, String token, ApiRequest request) {
            this.service = service;
            this.server = server;
            this.request = request;
            this.token = token;
        }

        @Override
        protected ApiResponse doInBackground(String... methods) {
            ApiResponse response = null;
            try {
                ApiServices apiServices = new ApiServicesImpl(this.server, this.token);
                response = apiServices.execute(this.request, methods);

                this.service.setResponse(response);
                this.service.setException(null);
            } catch (ApiException e) {
                this.service.setResponse(null);
                this.service.setException(e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(ApiResponse response) {
            this.service.finished();
        }

    }

}
