package org.ereuse.scanner.services.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

/**
 * Created by Jamgo SCCL.
 */
public class ApiException extends RestClientException {

    private HttpStatus statusCode;
    private String statusText;
    private ApiServicesImpl.EreuseError responseBody;

    public ApiException(String message) {
        super(message);
        this.statusText = message;
    }

    public ApiException(HttpStatus statusCode, String statusText, ApiServicesImpl.EreuseError responseBody) {
        super(statusText);
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseBody = responseBody;
    }

    public String getStatusText() {
        return statusText;
    }

    public ApiServicesImpl.EreuseError getResponseBody() {
        return responseBody;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

}
