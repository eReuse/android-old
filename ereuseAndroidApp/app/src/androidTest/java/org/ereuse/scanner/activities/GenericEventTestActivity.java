package org.ereuse.scanner.activities;

import android.view.View;

import org.ereuse.scanner.GlobalTestParams;
import org.ereuse.scanner.services.api.ActionResponse;
import org.ereuse.scanner.services.api.ApiException;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.junit.Ignore;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

public class GenericEventTestActivity extends FormGenericActivity {

    @Override
    protected void setToolbar() {
    }

    @Override
    public String getServer() {
        return GlobalTestParams.DEVICEHUB_SERVER;
    }

    @Override
    public void sendForm(View view) {
        this.deviceIds = GlobalTestParams.DEVICES_LIST;
        ApiServicesImpl.setDb(GlobalTestParams.DEFAULT_DB);

        super.sendForm(view);
    }

    @Override
    public void onSuccess(ApiResponse response) {
        onHandleResponseCalled(response);
        super.onSuccess(response);
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

        assertEquals(ActionResponse.ActionType.GENERIC.toString(), actionType.name());
    }
}
