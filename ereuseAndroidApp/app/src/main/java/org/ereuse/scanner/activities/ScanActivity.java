package org.ereuse.scanner.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

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
public abstract class ScanActivity extends AsyncActivity {

    private Integer scanType = null;

    public void setScanType(Integer scanType) {
        this.getScannerApplication().setScanType(scanType);
    }

    public Integer getScanType() {
        return this.getScannerApplication().getScanType();
    }

    protected void launchScanAction(int permissionCode) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        this.setScanType(permissionCode);
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_QR_CAMERA_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Start QR scanner
                    this.launchScanAction(requestCode);

                } else {
                    this.setScanType(null);
                    showPermissionDeniedDialog();
                }
                return;
            }
        }
    }

        /* CAMERA USAGE FOR QR and Barcode Scan */

    protected void checkCameraPermission(int permissionCode) {

        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionCode);
                return;
            }
        }

        this.launchScanAction(permissionCode);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setScanType(null);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
