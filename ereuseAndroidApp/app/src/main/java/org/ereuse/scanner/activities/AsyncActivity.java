package org.ereuse.scanner.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.api.ApiException;
import org.ereuse.scanner.services.api.ApiResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public abstract class AsyncActivity extends BaseActivity {

    final protected int REQUEST_CODE_ACCESS_FINE_PERMISSIONS = 123;

    protected static final int REQUEST_CODE_SERIALNUMBER_CAMERA_PERMISSIONS = 91;
    protected static final int REQUEST_CODE_MODEL_CAMERA_PERMISSIONS = 92;
    protected static final int REQUEST_CODE_MANUFACTURER_CAMERA_PERMISSIONS = 93;
    protected static final int REQUEST_CODE_LICENSEKEY_CAMERA_PERMISSIONS = 94;
    protected static final int REQUEST_CODE_GIVER_CAMERA_PERMISSIONS = 95;
    protected static final int REQUEST_CODE_REFURBISHER_CAMERA_PERMISSIONS = 96;
    protected static final int REQUEST_CODE_SYSTEM_CAMERA_PERMISSIONS = 97;
    protected static final int REQUEST_CODE_JS_CAMERA_PERMISSIONS = 98;

    protected static final int REQUEST_CODE_QR_CAMERA_PERMISSIONS = 99;

    protected static final List<Integer> REQUEST_CODEBAR_PERMISSIONS = new ArrayList<Integer>() {{
        add(REQUEST_CODE_SERIALNUMBER_CAMERA_PERMISSIONS);
        add(REQUEST_CODE_MODEL_CAMERA_PERMISSIONS);
        add(REQUEST_CODE_MANUFACTURER_CAMERA_PERMISSIONS);
        add(REQUEST_CODE_LICENSEKEY_CAMERA_PERMISSIONS);
        add(REQUEST_CODE_GIVER_CAMERA_PERMISSIONS);
        add(REQUEST_CODE_REFURBISHER_CAMERA_PERMISSIONS);
        add(REQUEST_CODE_SYSTEM_CAMERA_PERMISSIONS);
        add(REQUEST_CODE_JS_CAMERA_PERMISSIONS);
    }};

    public String getServer() {
        return this.getScannerApplication().getServer();
    }

    public User getUser() {
        return this.getScannerApplication().getUser();
    }

    public String getToken() {
        return this.getScannerApplication().getUser().getToken();
    }

    public List<Integer> getMainLayoutFields() {
        return Collections.emptyList();
    }

    public void enableLayoutFields() {
        this.enableDisableMainLayoutFields(true);
    }

    public void disableLayoutFields() {
        this.enableDisableMainLayoutFields(false);
    }

    private void enableDisableMainLayoutFields(boolean enabled) {
        for (int fieldId : this.getMainLayoutFields()) {
            findViewById(fieldId).setEnabled(enabled);
        }
    }

    public void startProgressBar() {
        RelativeLayout waitingLayout = (RelativeLayout) findViewById(R.id.waitingLayout);
        waitingLayout.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar() {
        RelativeLayout waitingLayout = (RelativeLayout) findViewById(R.id.waitingLayout);
        waitingLayout.setVisibility(View.GONE);
    }

    public void onStartAsync() {
        this.disableLayoutFields();
        this.startProgressBar();
    }

    public void onSuccess(ApiResponse response) {
        this.enableLayoutFields();
        this.stopProgressBar();
    }

    public void onError(ApiException exception) {
        this.enableLayoutFields();
        this.stopProgressBar();

        if(AsyncService.ERROR_NO_SUCH_PLACE.equals(exception.getResponseBody().getType())) {
            AlertDialog.Builder noSuchPlaceDialog = new AlertDialog.Builder(this);
            noSuchPlaceDialog.setTitle(exception.getResponseBody().getMessage());
            noSuchPlaceDialog.setMessage("Please, create a new Place for your current location");
            noSuchPlaceDialog.setPositiveButton(R.string.dialog_ack, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    noSuchPlaceDialogOK();
                }
            });
            noSuchPlaceDialog.show();
        } else {
            System.out.println("Error: code = " + exception.getStatusCode().toString() + ", text = " + exception.getStatusText() + "\n " + exception.getResponseBody());
            Toast toast = Toast.makeText(this, "Error: " + exception.getResponseBody().getMessage(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void noSuchPlaceDialogOK() {
        Intent intent = new Intent(this, PlaceMapActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("commingFromNoSuchPlaceError", true);
        intent.putExtras(b);
        startActivity(intent);
    }
}
