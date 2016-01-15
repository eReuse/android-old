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

import java.util.Collections;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public abstract class AsyncActivity extends BaseActivity {

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
