package org.ereuse.scanner.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.services.api.SnapshotResponse;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotActivity extends ScanActivity {

    private SubMenu selectDb;
    ArrayList<String> databases;

    public static final String EXTRA_MODE = "mode";
    public static final String MODE_SELF = "self";
    public static final String MODE_EXTERNAL_DEVICE = "External";
    private String mode;

    //    TextView titleTextView;
    EditText serialNumberEditText;
    EditText modelEditText;
    EditText manufacturerEditText;
    EditText licenseKeyEditText;
    EditText giverEditText;
    EditText refurbishedEditText;
    EditText systemEditText;
    EditText commentsEditText;
    Spinner deviceTypeSpinner;
    Spinner deviceSubTypeSpinner;
    String deviceType;
    String deviceSubType;

    //As per eReuse request, until they can provide this information dinamically this will be a static list
    private static final Map<String, List<String>> DEVICETYPES;

    static {
        Map<String, List<String>> deviceTypesMap = new HashMap<String, List<String>>();
        deviceTypesMap.put("Computer", new ArrayList<String>(Arrays.asList("Netbook", "Laptop", "Microtower", "Server")));
        deviceTypesMap.put("ComputerMonitor", new ArrayList<String>(Arrays.asList("TFT", "LCD", "OLED", "LED")));
        deviceTypesMap.put("Peripheral", new ArrayList<String>(Arrays.asList("Scanner", "MultifunctionPrinter", "SAI", "Switch", "HUB", "Router", "Mouse", "Keyboard", "Printer")));
        DEVICETYPES = Collections.unmodifiableMap(deviceTypesMap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot);
        databases = getScannerApplication().getUser().getDatabases();
        setToolbar();

        this.mode = this.getIntent().getStringExtra(EXTRA_MODE);
        this.initLayout();
    }

    private void initLayout() {
//        this.titleTextView = (TextView) this.findViewById(R.id.snapshotHeadLabel);
        this.manufacturerEditText = (EditText) this.findViewById(R.id.snapshotManufacturerEditText);
        this.modelEditText = (EditText) this.findViewById(R.id.snapshotModelEditText);
        this.serialNumberEditText = (EditText) this.findViewById(R.id.snapshotSerialNumberEditText);
        this.licenseKeyEditText = (EditText) this.findViewById(R.id.snapshotLicenseEditText);

        this.giverEditText = (EditText) this.findViewById(R.id.snapshotGiverEditText);
        this.refurbishedEditText = (EditText) this.findViewById(R.id.snapshotRefurbisherEditText);
        this.systemEditText = (EditText) this.findViewById(R.id.snapshotSystemEditText);

        this.commentsEditText = (EditText) this.findViewById(R.id.snapshotCommentsEditText);
        this.deviceTypeSpinner = (Spinner) this.findViewById(R.id.snapshotDeviceType);
        this.deviceSubTypeSpinner = (Spinner) this.findViewById(R.id.snapshotDeviceSubType);

        if (this.mode.equals(MODE_SELF)) {
            initializeSelfSnapshotLayout();
        } else {
            initializeExternalDeviceSnapshotLayout();
        }
    }

    private void initializeSelfSnapshotLayout() {
        this.deviceType = "Device";
        this.deviceSubType = "Smartphone";

        TextView deviceTypeLabel = (TextView) this.findViewById(R.id.snapshotDeviceTypeLabel);
        deviceTypeLabel.setVisibility(View.GONE);
        this.deviceTypeSpinner.setVisibility(View.GONE);
        this.deviceSubTypeSpinner.setVisibility(View.GONE);

        this.manufacturerEditText.setText(Build.MANUFACTURER);
        this.manufacturerEditText.setFocusable(false);
        this.modelEditText.setText(Build.MODEL);
        this.modelEditText.setFocusable(false);
        this.serialNumberEditText.setText(Build.SERIAL);
        this.serialNumberEditText.setFocusable(false);

        this.hideScanButtons();

        TextView commentsLabel = (TextView) this.findViewById(R.id.snapshotCommentsLabel);
        commentsLabel.setVisibility(View.GONE);
        this.commentsEditText.setVisibility(View.GONE);
    }

    private void initializeExternalDeviceSnapshotLayout() {
        String[] spinnerDeviceTypeValues = DEVICETYPES.keySet().toArray(new String[DEVICETYPES.keySet().size()]);
        ArrayAdapter<String> deviceTypeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerDeviceTypeValues);
        deviceTypeAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.deviceTypeSpinner.setAdapter(deviceTypeAdapter);

        this.deviceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedDeviceType = (String) adapterView.getItemAtPosition(i);
                deviceType = selectedDeviceType;
                String[] spinnerDeviceSubTypeValues = DEVICETYPES.get(selectedDeviceType).toArray(new String[DEVICETYPES.get(selectedDeviceType).size()]);
                ArrayAdapter<String> deviceSubTypeAdapter = new ArrayAdapter<String>(SnapshotActivity.this, R.layout.spinner_item, spinnerDeviceSubTypeValues);
                deviceSubTypeSpinner.setAdapter(deviceSubTypeAdapter);
                deviceSubTypeSpinner.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        this.deviceSubTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedDeviceSubType = (String) adapterView.getItemAtPosition(i);
                deviceSubType = selectedDeviceSubType;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        this.deviceSubTypeSpinner.setVisibility(View.GONE);
    }

    private void hideScanButtons() {
        ImageButton serialNumberScanButton = (ImageButton) this.findViewById(R.id.snapshotSerialNumberScanButton);
        ImageButton modelScanButton = (ImageButton) this.findViewById(R.id.snapshotModelScanButton);
        ImageButton manufacturerScanButton = (ImageButton) this.findViewById(R.id.snapshotManufacturerScanButton);
        ImageButton licenseScanButton = (ImageButton) this.findViewById(R.id.snapshotLicenseScanButton);

        serialNumberScanButton.setVisibility(View.GONE);
        modelScanButton.setVisibility(View.GONE);
        manufacturerScanButton.setVisibility(View.GONE);
        licenseScanButton.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_db_menu, menu);
        MenuItem item = menu.findItem(R.id.action_select_db);
        selectDb = item.getSubMenu();
        SetDatabase setDatabase = new SetDatabase();
        for (String database : databases)
            selectDb.add(database).setOnMenuItemClickListener(setDatabase);
        return true;
    }


    class SetDatabase implements MenuItem.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ApiServicesImpl.setDb(item.getTitleCondensed().toString());
            return true;
        }
    }

    public void sendSnapshot(View view) {
        if (doValidate()) {
            String serialNumber = this.serialNumberEditText.getText().toString();
            String model = this.modelEditText.getText().toString();
            String manufacturer = this.manufacturerEditText.getText().toString();
            String licenseKey = this.licenseKeyEditText.getText().toString();

            String giverId = this.giverEditText.getText().toString();
            String refurbisherId = this.refurbishedEditText.getText().toString();
            String systemId = this.systemEditText.getText().toString();

            String comment = this.commentsEditText.getText().toString();

            AsyncService asyncService = new AsyncService(this);
            asyncService.doSnapshot(this.getServer(), this.getUser(), deviceType, deviceSubType, serialNumber, model, manufacturer, licenseKey, giverId, refurbisherId, systemId, comment);
        }

    }

    private boolean doValidate() {
        List<String> mandatoryEmptyFields = new ArrayList<String>();

        if (this.serialNumberEditText.getText().toString().isEmpty()) {
            mandatoryEmptyFields.add(getString(R.string.snapshot_serial_number_label));
        }
        if (this.modelEditText.getText().toString().isEmpty()) {
            mandatoryEmptyFields.add(getString(R.string.snapshot_model_label));

        }
        if (this.manufacturerEditText.getText().toString().isEmpty()) {
            mandatoryEmptyFields.add(getString(R.string.snapshot_manufacturer_label));
        }

        if (!mandatoryEmptyFields.isEmpty()) {
            String errorMessage = getString(R.string.empty_fields_error_message) + "\n";
            for (String emptyField : mandatoryEmptyFields) {
                errorMessage += "\n - " + emptyField;
            }
            launchActionMessageDialog(getString(R.string.dialog_validation_error_title), errorMessage);
            return false;
        }

        return true;
    }

    @Override
    public void onSuccess(ApiResponse response) {
        super.onSuccess(response);

        SnapshotResponse snapshotResponse = (SnapshotResponse) response;

        if (snapshotResponse.getStatus().equals(getString(R.string.server_response_status_ok))) {
            launchActionMessageDialog(getString(R.string.snapshot_success));
        }

    }


    /* barcode scan actions */
    public void scanSerialNumber(View view) {
        checkCameraPermission(REQUEST_CODE_SERIALNUMBER_CAMERA_PERMISSIONS);
    }

    public void scanModel(View view) {
        checkCameraPermission(REQUEST_CODE_MODEL_CAMERA_PERMISSIONS);
    }

    public void scanManufacturer(View view) {
        checkCameraPermission(REQUEST_CODE_MANUFACTURER_CAMERA_PERMISSIONS);
    }

    public void scanLicenseKey(View view) {
        checkCameraPermission(REQUEST_CODE_LICENSEKEY_CAMERA_PERMISSIONS);
    }

    public void scanGiverId(View view) {
        checkCameraPermission(REQUEST_CODE_GIVER_CAMERA_PERMISSIONS);
    }

    public void scanRefurbisherId(View view) {
        checkCameraPermission(REQUEST_CODE_REFURBISHER_CAMERA_PERMISSIONS);
    }

    public void scanSystemId(View view) {
        checkCameraPermission(REQUEST_CODE_SYSTEM_CAMERA_PERMISSIONS);
    }

    @Override
    protected void launchScanAction(int permissionCode) {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus,true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash,false);

        startActivityForResult(intent, permissionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_CODEBAR_PERMISSIONS.contains(requestCode)) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Toast.makeText(this, getString(R.string.toast_barcode_scanned) + " " + barcode.displayValue, Toast.LENGTH_LONG).show();
                    this.fillScannedCode(barcode.displayValue, requestCode);
                } else {
                    Toast.makeText(this, getString(R.string.toast_barcode_cancel), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void fillScannedCode(String scannedCode, int requestCode) {

        switch (requestCode) {
            case REQUEST_CODE_SERIALNUMBER_CAMERA_PERMISSIONS:
                this.serialNumberEditText.setText(scannedCode);
                break;
            case REQUEST_CODE_MODEL_CAMERA_PERMISSIONS:
                this.modelEditText.setText(scannedCode);
                break;
            case REQUEST_CODE_MANUFACTURER_CAMERA_PERMISSIONS:
                this.manufacturerEditText.setText(scannedCode);
                break;
            case REQUEST_CODE_LICENSEKEY_CAMERA_PERMISSIONS:
                this.licenseKeyEditText.setText(scannedCode);
                break;
            case REQUEST_CODE_GIVER_CAMERA_PERMISSIONS:
                this.giverEditText.setText(scannedCode);
                break;
            case REQUEST_CODE_REFURBISHER_CAMERA_PERMISSIONS:
                this.refurbishedEditText.setText(scannedCode);
                break;
            case REQUEST_CODE_SYSTEM_CAMERA_PERMISSIONS:
                this.systemEditText.setText(scannedCode);
                break;
            default:
                break;
        }
    }

}
