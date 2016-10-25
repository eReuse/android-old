package org.ereuse.scanner.services.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class ApiServicesImpl implements ApiServices {

    private static final List<MediaType> MEDIA_TYPE_LIST = Collections.singletonList(new MediaType("application", "json"));

    private static final HttpMethod HTTP_METHOD_LOGIN = HttpMethod.POST;
    private static final HttpMethod HTTP_METHOD_DEVICES = HttpMethod.GET;
    private static final HttpMethod HTTP_METHOD_LOCATION = HttpMethod.POST;
    private static final HttpMethod HTTP_METHOD_RECYCLE = HttpMethod.POST;
    private static final HttpMethod HTTP_METHOD_RECEIVE = HttpMethod.POST;
    private static final HttpMethod HTTP_METHOD_EVENTS = HttpMethod.GET;
    private static final HttpMethod HTTP_METHOD_PLACE = HttpMethod.POST;
    // TODO Add HTTP_METHOD for each method

    private static String db;
    private static final String PATH_LOGIN = "login";
    private static final String PATH_DEVICES = "devices";
    private static final String PATH_LOCATE = "events/devices/locate";
    private static final String PATH_RECEIVE = "events/devices/receive";
    private static final String PATH_EVENTS = "events";
    private static final String PATH_PLACE = "place s";
    // TODO Add PATH for each method

    private static final String EVENTS_QUERY = "?embedded={\"devices\":1}";

    private String server;
    private String token;

    private RestTemplate restTemplate;

    public ApiServicesImpl(String server, String token) {
        this.server = server;
        this.token = token;
        this.restTemplate = this.createRestTemplate();
    }

    @Override
    public ApiResponse execute(String method, ApiRequest request) throws ApiException {
        ApiResponse response = null;

        // TODO add other API methods, use switch instead of if statement
        if (method.equals(METHOD_LOGIN)) {
            response = this.login(request);
        } else if (method.equals(METHOD_DEVICE)) {
            response = this.device(request);
        } else if (method.equals(METHOD_LOCATE)) {
            response = this.locate(request);
        } else if (method.equals(METHOD_RECEIVE)) {
            response = this.receive(request);
        } else if (method.equals(METHOD_RECYCLE)) {
            response = this.recycle(request);
        } else if (method.equals(METHOD_EVENTS)) {
            response = this.events(request);
        }  else if (method.equals(METHOD_PLACE)) {
        response = this.place(request);
        } else {
            throw new ApiException("Not implemented method: " + method);
        }

        return response;
    }

    private LoginResponse login(ApiRequest request) throws ApiException {
        String url = this.server + PATH_LOGIN;

        HttpEntity<?> requestEntity = new HttpEntity<Object>(request, this.getRequestHeaders(false));
        ResponseEntity<LoginResponse> response = this.restTemplate.exchange(url, HTTP_METHOD_LOGIN, requestEntity, LoginResponse.class);
        LoginResponse body = response.getBody();
        setDb(body.getDefaultDatabase());
        return body;
    }

    private DeviceResponse device(final ApiRequest request) throws ApiException {
        DeviceRequest deviceRequest = (DeviceRequest) request;
        String url = this.server + db + PATH_DEVICES;
        if (deviceRequest.getDeviceId() != null) {
            url += "/" + deviceRequest.getDeviceId();
        }

        HttpEntity<?> requestEntity = new HttpEntity<Object>(this.getRequestHeaders(true));
        ResponseEntity<DeviceResponse> response = this.restTemplate.exchange(url, HTTP_METHOD_DEVICES, requestEntity, DeviceResponse.class);
        return response.getBody();
    }

    private ActionResponse locate(final ApiRequest request) throws ApiException {
        String url = this.server + db + PATH_LOCATE;

        HttpEntity<?> requestEntity = new HttpEntity<Object>(request, this.getRequestHeaders(true));
        ResponseEntity<ActionResponse> response = this.restTemplate.exchange(url, HTTP_METHOD_LOCATION, requestEntity, ActionResponse.class);
        ActionResponse actionResponse = response.getBody();
        actionResponse.setActionType(ActionResponse.ActionType.LOCATE);
        return response.getBody();
    }

    private ActionResponse receive(final ApiRequest request) throws ApiException {
        String url = this.server + db + PATH_RECEIVE;

        HttpEntity<?> requestEntity = new HttpEntity<Object>(request, this.getRequestHeaders(true));
        ResponseEntity<ActionResponse> response = this.restTemplate.exchange(url, HTTP_METHOD_RECEIVE, requestEntity, ActionResponse.class);
        ActionResponse actionResponse = response.getBody();
        actionResponse.setActionType(ActionResponse.ActionType.RECEIVE);
        return response.getBody();
    }

    private ActionResponse recycle(final ApiRequest request) throws ApiException {
        String url = this.server + db + PATH_RECEIVE;

        HttpEntity<?> requestEntity = new HttpEntity<Object>(request, this.getRequestHeaders(true));
        ResponseEntity<ActionResponse> response = this.restTemplate.exchange(url, HTTP_METHOD_RECYCLE, requestEntity, ActionResponse.class);
        ActionResponse actionResponse = response.getBody();
        actionResponse.setActionType(ActionResponse.ActionType.RECYCLE);
        return response.getBody();
    }

    private EventsResponse events(final ApiRequest request) throws ApiException {
        String url = this.server + db + PATH_EVENTS;

        HttpEntity<?> requestEntity = new HttpEntity<Object>(this.getRequestHeaders(true));
        ResponseEntity<EventsResponse> response = this.restTemplate.exchange(url, HTTP_METHOD_EVENTS, requestEntity, EventsResponse.class);
        return response.getBody();
    }

    private ActionResponse place(final ApiRequest request) throws ApiException {
        String url = this.server + db + PATH_PLACE;

        HttpEntity<?> requestEntity = new HttpEntity<Object>(request, this.getRequestHeaders(true));
        ResponseEntity<ActionResponse> response = this.restTemplate.exchange(url, HTTP_METHOD_PLACE, requestEntity, ActionResponse.class);
        ActionResponse actionResponse = response.getBody();
        actionResponse.setActionType(ActionResponse.ActionType.PLACE);
        return response.getBody();
    }

    private HttpHeaders getRequestHeaders(boolean authorization) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(MEDIA_TYPE_LIST);
        requestHeaders.set("Connection", "Close");
        if (authorization) {
            requestHeaders.setAuthorization(new EreuseHttpAuthenticationHeader(this.token));
        }
        return requestHeaders;
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EreuseResponseErrorHandler());
        return restTemplate;
    }

    private static class EreuseHttpAuthenticationHeader extends HttpAuthentication {

        private String token;

        public EreuseHttpAuthenticationHeader(String token) {
            this.token = token;
        }

        @Override
        public String getHeaderValue() {
            return "Basic " + this.token;
        }

    }

    /*
        Error: code = 401, text = UNAUTHORIZED
        {
            "_error": {
                "@type": "WrongCredentials",
                "code": 401,
                "message": "There is not an user with the matching username/password"
            },
            "_status": "ERR"
        }
     */
    private static class EreuseResponseErrorHandler extends DefaultResponseErrorHandler {
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()));
            EreuseException exception = new Gson().fromJson(reader, EreuseException.class);

            if (exception.status.equals("ERR")) {
                throw new ApiException(response.getStatusCode(), response.getStatusText(), exception.error);
            }
        }
    }

    private static class EreuseException {
        @SerializedName("_error")
        private EreuseError error;
        @SerializedName("_status")
        private String status;
    }

    public static class EreuseError {
        @SerializedName("@type")
        private String type;
        private int code;
        private String message;

        public String getType() {
            return type;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public static void setDb(String db) {
        ApiServicesImpl.db = db + '/';
    }
}
