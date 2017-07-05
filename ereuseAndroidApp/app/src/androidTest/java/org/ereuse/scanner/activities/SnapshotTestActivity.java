package org.ereuse.scanner.activities;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.ereuse.scanner.GlobalTestParams;
import org.ereuse.scanner.R;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.api.ActionResponse;
import org.ereuse.scanner.services.api.ApiException;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.services.api.ManufacturersResponse;
import org.ereuse.scanner.services.api.SnapshotResponse;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

public class SnapshotTestActivity extends SnapshotActivity {

    @Override
    protected void setToolbar() {
    }

    @Override
    public String getServer() {
        return GlobalTestParams.DEVICEHUB_SERVER;
    }

    @Override
    public String getSelectedManufacturer() {
        return "bq";
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
        testUser.databases = Arrays.asList("db1");
        return testUser;
    }
    @Override
    public void sendSnapshot(View view) {
        this.deviceType = "Device";
        this.deviceSubType = "Smartphone";
        super.sendSnapshot(view);
    }

    @Override
    public void onSuccess(ApiResponse response) {
        if (response.getClass().equals(ManufacturersResponse.class)) {
            super.onSuccess(response);
        } else {
            onHandleResponseCalled(null);
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

        SnapshotResponse snapshotResponse = (SnapshotResponse) apiResponse;
        assertEquals(getString(R.string.server_response_status_ok),snapshotResponse.getStatus());
    }
}
