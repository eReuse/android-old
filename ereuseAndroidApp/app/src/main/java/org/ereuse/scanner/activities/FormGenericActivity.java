package org.ereuse.scanner.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.common.StringUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.ValidationService;
import org.ereuse.scanner.services.api.ActionResponse;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.DeviceResponse;
import org.ereuse.scanner.utils.ScanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class FormGenericActivity extends ScanActivity {
    public static final String EXTRA_MODE = "mode";

    private static final float ACCURACY_THRESHOLD = 20.0f;

    protected String mode;
    protected List<String> deviceIds;

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_form);

        this.tableLayout = (TableLayout) findViewById(R.id.devicesTableLayout);
        String mode = this.getIntent().getStringExtra(EXTRA_MODE);
        if (mode != null && mode.length() > 0) {
            this.mode = mode;
        }
        this.deviceIds = new ArrayList<String>();

        this.initLayout();
    }

    private void initLayout() {
        setToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        ValidationService.checkInternetConnection(this);
        checkLogin();
    }


    public void addDevice(View view) {
        checkCameraPermission(REQUEST_CODE_QR_CAMERA_PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, getString(R.string.toast_qr_cancel), Toast.LENGTH_LONG).show();
            } else if (!result.getContents().startsWith(this.getServer())) {
                launchActionMessageDialog(getString(R.string.dialog_qr_error_title),getString(R.string.dialog_qr_error_message));
            } else {
                Toast.makeText(this, getString(R.string.toast_qr_scanned) + " " + result.getContents(), Toast.LENGTH_LONG).show();

                // On result, use eReuse API to get device info
                new AsyncService(this).getDevice(this.getServer(), this.getToken(), ScanUtils.getSystemIdFromUrl(result.getContents()));
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void sendForm(View view) {

        if(ValidationService.checkInternetConnection(this)) {

            if (doValidate()) {
                AsyncService asyncService = new AsyncService(this);
                String message = ((TextView) findViewById(R.id.commentsEditText)).getText().toString();
                String eventLabel = ((TextView) findViewById(R.id.titleEditText)).getText().toString();
                asyncService.doGeneric(this.getServer(), this.getUser(), eventLabel, this.deviceIds, message, this.mode);
            }
        }
    }

    // AsyncActivity methods

    @Override
    public void onSuccess(ApiResponse response) {
        super.onSuccess(response);

        if (response instanceof DeviceResponse) {

            DeviceResponse deviceResponse = (DeviceResponse) response;
            this.addDeviceToTableLayout(deviceResponse);

        } else if (response instanceof ActionResponse) {

            ActionResponse actionResponse = (ActionResponse) response;
            ActionResponse.ActionType actionType = actionResponse.getActionType();


            launchActionMessageDialog("\"" + this.mode + "\"" + " action performed successfully");


        }
    }

    private void addDeviceToTableLayout(DeviceResponse device) {
        if(deviceIds.contains(device.get_id())) {
            launchActionMessageDialog(getString(R.string.form_message_device_already_scanned));
            return;
        }

        this.deviceIds.add(device.get_id());

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        View rowView = this.getLayoutInflater().inflate(R.layout.device_list_item, row, false);

        TextView tv;
        tv = (TextView) rowView.findViewById(R.id.deviceFirstLine);
        tv.setText(device.getHid());
        tv = (TextView) rowView.findViewById(R.id.deviceSecondLine);
        tv.setText(device.getManufacturer());
        tv = (TextView) rowView.findViewById(R.id.deviceThirdLine);
        tv.setText(device.get_id());

        Button removeDeviceButton = (Button) rowView.findViewById(R.id.removeDeviceButton);
        removeDeviceButton.setTag(device.get_id());
        removeDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId = (String) view.getTag();
                FormGenericActivity.this.deviceIds.remove(deviceId);

                View row = (View) view.getParent();
                ViewGroup container = (ViewGroup) row.getParent();
                container.removeView(row);
                container.invalidate();
            }
        });

        row.addView(rowView);
        this.tableLayout.addView(row);

    }

    private boolean doValidate() {

        if(deviceIds.isEmpty()) {
            launchActionMessageDialog(getString(R.string.dialog_validation_error_title),getString(R.string.form_validation_error_emptydevices));
            return false;
        }

        return true;
    }
}

