package org.ereuse.scanner.activities;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

import android.support.test.runner.AndroidJUnit4;

import org.ereuse.scanner.GlobalTestParams;
import org.ereuse.scanner.services.api.ApiException;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.LoginResponse;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoginTestActivity extends LoginActivity {

    @Override
    protected void setToolbar() {
    }

    @Override
    public String getServer() {
        return GlobalTestParams.DEVICEHUB_SERVER;
    }

    @Override
    public void onSuccess(ApiResponse response) {
        LoginResponse loginResponse = (LoginResponse) response;
      //  user.update(loginResponse.getEmail(), loginResponse.getToken(), loginResponse.getRole(), loginResponse.get_id(), loginResponse.getDatabases(), loginResponse.getDefaultDatabase());
        onHandleResponseCalled(loginResponse);
        super.onSuccess(response);
    }

    @Override
    public void onError(ApiException exception) {
        onHandleResponseCalled(null);
        super.onError(exception);
    }

    public void onHandleResponseCalled(ApiResponse apiResponse) {
        assertNotNull(apiResponse);
        LoginResponse loginResponse = (LoginResponse) apiResponse;
        assertEquals("a@a.a",loginResponse.getEmail());

    }

}
