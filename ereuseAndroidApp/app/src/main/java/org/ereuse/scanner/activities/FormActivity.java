package org.ereuse.scanner.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.ValidationService;
import org.ereuse.scanner.services.api.ActionResponse;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.DeviceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class FormActivity extends ScanActivity implements OnMapReadyCallback, LocationListenerActivity {
    public static final String EXTRA_MODE = "mode";

    public static final String MODE_RECYCLE = "recycle";
    public static final String MODE_RECEIVE = "receive";
    public static final String MODE_LOCATE = "locate";

    public static CheckBox checkRelaunchActionFromNewPlace;
    private static final float ACCURACY_THRESHOLD = 20.0f;

    private String mode;
    private List<String> deviceIds;

    private TableLayout tableLayout;
    private Location location;
    private GoogleMap map;
    private TextView tv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        checkRelaunchActionFromNewPlace = (CheckBox) findViewById(R.id.formRelaunchActivityFromNewPlace);
        checkRelaunchActionFromNewPlace.setVisibility(View.GONE);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        this.tableLayout = (TableLayout) findViewById(R.id.devicesTableLayout);
        this.mode = this.getIntent().getStringExtra(EXTRA_MODE);
        this.tv_location = (TextView) findViewById(R.id.tv_location);

        this.deviceIds = new ArrayList<String>();

        this.initLayout();
    }

    private void initLayout() {
        // Show checkbox field if mode = {RECEIVE, RECYCLE}
        CheckBox cb = (CheckBox) this.findViewById(R.id.formTermsAndConditionsCheckBox);
        EditText receiverEmailLabel = (EditText) this.findViewById(R.id.formReceiverEmailEditText);
        TextView receiverEmailText = (TextView) this.findViewById(R.id.formReceiverEmailLabel);
        TextView receiverNameText = (TextView) this.findViewById(R.id.formReceiverEmailLabel);
        if (this.mode.equals(MODE_LOCATE)) {
            cb.setVisibility(View.GONE);
            receiverEmailLabel.setVisibility(View.GONE);
            receiverEmailText.setVisibility(View.GONE);
            receiverNameText.setVisibility(View.GONE);

        } else {
            cb.setVisibility(View.VISIBLE);
            receiverEmailLabel.setVisibility(View.VISIBLE);
            receiverEmailText.setVisibility(View.VISIBLE);
            receiverNameText.setVisibility(View.VISIBLE);
            // TODO Change link to web depending on mode


            // Show receiver email and name field if user is employee and its not a LOCATE action
            TextView emailLabel = (TextView) findViewById(R.id.formReceiverEmailLabel);
            EditText emailText = (EditText) findViewById(R.id.formReceiverEmailEditText);

            if (this.getUser().isEqualOrGreaterThanEmployee()) {
                emailLabel.setVisibility(View.VISIBLE);
                emailText.setVisibility(View.VISIBLE);
            } else {
                emailLabel.setVisibility(View.GONE);
                emailText.setVisibility(View.GONE);
            }
        }
        // Disable send button until location is not set with accuracy
        Button sendButton = (Button) findViewById(R.id.formSendButton);
        sendButton.setBackgroundColor(this.getResources().getColor(R.color.disabled));
        sendButton.setText(this.getText(R.string.form_send_button_disabled));
        sendButton.setEnabled(false);
        sendButton.invalidate();

        setToolbar();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.getScannerApplication().triggerLocationStopper();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23) {
             int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(fineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_PERMISSIONS);
                return;
            }
        }

        ValidationService.checkInternetConnection(this);

        this.initLocation();
        ValidationService.checkLocationConnection(this);

        if (checkRelaunchActionFromNewPlace.isChecked()) {
            checkRelaunchActionFromNewPlace.setChecked(false);
            sendForm(findViewById(R.id.formSendButton));
        }

        this.getScannerApplication().setCurrentLocationActivity(this);
        this.updateLocationUI(this.getScannerApplication().getLocation());

        checkLogin();
    }

    public void updateLocationUI(Location location) {
        if(location == null) {
            logDebug("FormActivity", "null location");
        } else {
            this.location = location;
            String locationMessage = "lat: " + this.location.getLatitude()
                    + ", long: " + this.location.getLongitude()
                    + ", alt: " + this.location.getAltitude()
                    + ", acc: " + this.location.getAccuracy();
            logDebug("FormActivity",locationMessage);

            this.tv_location.setText(locationMessage);
            updateLocationMap();

            if (location.getAccuracy() <= ACCURACY_THRESHOLD) {
                // Enable send button
                Button sendButton = (Button) findViewById(R.id.formSendButton);
                sendButton.setText(this.getText(R.string.form_send_button));
                sendButton.setBackgroundColor(this.getResources().getColor(R.color.emphasis_2));
                sendButton.setEnabled(true);
                sendButton.invalidate();
            }
        }
    }

    private void updateLocationMap(){

        LatLng position = new LatLng(this.location.getLatitude(), this.location.getLongitude());

        MarkerOptions mp = new MarkerOptions();
        mp.position(position);
        mp.title("my position");
        mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        if (this.map != null) {
            this.map.clear();
            this.map.addMarker(mp);
            this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        }

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
                new AsyncService(this).getDevice(this.getServer(), this.getToken(), this.getDeviceId(result.getContents()));
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getDeviceId(String scannedUrl) {
        return scannedUrl.substring(scannedUrl.lastIndexOf('/') + 1);
    }


    public void sendForm(View view) {

        if(ValidationService.checkInternetConnection(this)) {

            if (doValidate()) {
                AsyncService asyncService = new AsyncService(this);

                String eventLabel = ((TextView) findViewById(R.id.titleEditText)).getText().toString();
                String message = ((TextView) findViewById(R.id.commentsEditText)).getText().toString();
                String unregisteredReceiver = ((TextView) findViewById(R.id.formReceiverEmailEditText)).getText().toString();
                boolean acceptedConditions = ((CheckBox) findViewById(R.id.formTermsAndConditionsCheckBox)).isChecked();

                switch (this.mode) {
                    case MODE_LOCATE:
                        asyncService.doLocate(this.getServer(), this.getUser(), eventLabel, this.deviceIds, message, location);
                        break;
                    case MODE_RECEIVE:
                        asyncService.doReceive(this.getServer(), this.getUser(), unregisteredReceiver, location, eventLabel, this.deviceIds, message, acceptedConditions);
                        break;
                    case MODE_RECYCLE:
                        asyncService.doRecycle(this.getServer(), this.getUser(), unregisteredReceiver, location, eventLabel, this.deviceIds, message, acceptedConditions);
                        break;
                }
            }
        }
    }

    // OnMapReadyCallback Methods

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        updateLocationUI(this.location);
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

            switch (actionType) {

                case LOCATE:
                    launchActionMessageDialog(getString(R.string.locate_success));
                    break;
                case RECEIVE:
                    launchActionMessageDialog(getString(R.string.receive_success));
                    break;
                case RECYCLE:
                    launchActionMessageDialog(getString(R.string.recycle_success));
                    break;
            }

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

        //ImageView imageView = (ImageView) rowView.findViewById(R.id.deviceIcon);
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
                FormActivity.this.deviceIds.remove(deviceId);

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

        if(this.mode.equals(MODE_RECEIVE) || this.mode.equals(MODE_RECYCLE)) {
            CheckBox acceptedConditions = (CheckBox) findViewById(R.id.formTermsAndConditionsCheckBox);
            if (!acceptedConditions.isChecked()) {
                launchActionMessageDialog(getString(R.string.dialog_validation_error_title), getString(R.string.form_validation_error_acceptconditions));
                return false;
            }
        }
        return true;
    }
}

