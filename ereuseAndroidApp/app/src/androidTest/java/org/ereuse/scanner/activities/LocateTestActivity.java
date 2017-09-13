package org.ereuse.scanner.activities;

import android.location.Location;
import android.view.Menu;
import android.view.View;
import org.ereuse.scanner.GlobalTestParams;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.api.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

public class LocateTestActivity extends FormActivity {

    @Override
    protected void setToolbar() {
    }

    @Override
    public String getServer() {
        return GlobalTestParams.DEVICEHUB_SERVER;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public User getUser() {
        User testUser = new User();
        testUser.email = GlobalTestParams.DEMO_USER_EMAIL;
        testUser.password = GlobalTestParams.DEMO_USER_PASSWORD;
        testUser.token = GlobalTestParams.DEMO_USER_TOKEN;
        testUser.databases = GlobalTestParams.DATABASES;
        testUser.role = User.ADMIN;
        return testUser;
    }

    @Override
    public void onSuccess(ApiResponse response) {
        if (response.getClass().equals(ManufacturersResponse.class)) {
            super.onSuccess(response);
        } else {
            onHandleResponseCalled(response);
            super.onSuccess(response);
        }
    }

    @Override
    public void onError(ApiException exception) {
        onHandleResponseCalled(null);
        super.onError(exception);
    }

    public void onHandleResponseCalled(ApiResponse apiResponse) {
        assertNotNull(apiResponse);

        ActionResponse actionResponse = (ActionResponse) apiResponse;
        ActionResponse.ActionType actionType = actionResponse.getActionType();
        assertEquals(ActionResponse.ActionType.LOCATE, actionType);
    }

    @Override
    public void sendForm(View view) {
        this.deviceIds = GlobalTestParams.DEVICES_LIST;
        ApiServicesImpl.setDb(GlobalTestParams.DEFAULT_DB);
        Location testLocation = new Location("");
        testLocation.setLatitude(41.39);
        testLocation.setLongitude(2.15);
        this.location = testLocation;
        super.sendForm(view);
    }
}
