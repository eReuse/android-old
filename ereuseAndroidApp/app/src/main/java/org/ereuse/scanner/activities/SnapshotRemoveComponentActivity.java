package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import org.ereuse.scanner.R;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotRemoveComponentActivity extends ScanActivity {

    private static final List<String> REMOVABLEDEVICETYPES;

    static {
        List<String> removableDeviceTypesMap = Arrays.asList("HardDrive", "GraphicCard", "MotherBoard", "NetworkAdapter", "OpticalDrive", "Processor", "RamModule", "SosundCard");
        REMOVABLEDEVICETYPES = removableDeviceTypesMap;
    }

    EditText parentSystemEditText;
    Spinner componentTypeSpinner;
    String componentType;
    EditText serialNumberEditText;
    EditText modelEditText;
    EditText manufacturerEditText;
    EditText commentsEditText;
    private String currentLinkComponentToParentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_remove_component);
        this.initLayout();
        setToolbar();
    }

    private void initLayout() {
        this.parentSystemEditText = (EditText) this.findViewById(R.id.snapshotParentSystemEditText);

        this.manufacturerEditText = (EditText) this.findViewById(R.id.snapshotManufacturerEditText);
        this.modelEditText = (EditText) this.findViewById(R.id.snapshotModelEditText);
        this.serialNumberEditText = (EditText) this.findViewById(R.id.snapshotSerialNumberEditText);

        this.componentTypeSpinner = (Spinner) this.findViewById(R.id.snapshotComponentType);

        this.commentsEditText = (EditText) this.findViewById(R.id.snapshotCommentsEditText);

        //Initialize componentsTypeSpinner
        String[] spinnerComponentsTypeValues = REMOVABLEDEVICETYPES.toArray(new String[REMOVABLEDEVICETYPES.size()]);
        ArrayAdapter<String> componentsTypeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerComponentsTypeValues);
        componentsTypeAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.componentTypeSpinner.setAdapter(componentsTypeAdapter);

        this.componentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedComponentType = (String) adapterView.getItemAtPosition(i);
                componentType = selectedComponentType;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    public void sendRemoveComponentSnapshot(View view) {
        if (doValidate()) {
            String serialNumber = this.serialNumberEditText.getText().toString();
            String model = this.modelEditText.getText().toString();
            String manufacturer = this.manufacturerEditText.getText().toString();

            String parentSystemId = this.parentSystemEditText.getText().toString();

            String comment = this.commentsEditText.getText().toString();

            AsyncService asyncService = new AsyncService(this);
            asyncService.doLinkComponentToParentSnapshot(this.getServer(), this.getUser(), this.componentType, parentSystemId, serialNumber, model, manufacturer, comment);
        }

    }

    public void undoComponentSnapshot() {
        String eventToUndo = this.currentLinkComponentToParentEvent;
        this.currentLinkComponentToParentEvent = null;

        AsyncService asyncService = new AsyncService(this);
        asyncService.undoLinkComponentToParentRemoveComponentFromParent(this.getServer(), this.getUser(), eventToUndo);
    }

    public void removeComponentFromDevice(String componentIdToRemove) {
        String parentSystemId = this.parentSystemEditText.getText().toString();
        String comment = this.commentsEditText.getText().toString();

        AsyncService asyncService = new AsyncService(this);
        asyncService.doRemoveComponentFromParent(this.getServer(), this.getUser(), parentSystemId, componentIdToRemove, comment);
    }

    private boolean doValidate() {
        List<String> mandatoryEmptyFields = new ArrayList<String>();

        if (this.parentSystemEditText.getText().toString().isEmpty()) {
            mandatoryEmptyFields.add(getString(R.string.snapshot_parent_system_label));
        }
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

        if (response instanceof SnapshotResponse) {
            SnapshotResponse snapshotResponse = (SnapshotResponse) response;
            if (snapshotResponse.getStatus().equals(getString(R.string.server_response_status_ok))) {
                currentLinkComponentToParentEvent = snapshotResponse.get_id();
                this.removeComponentFromDevice(snapshotResponse.getdeviceId());
            }
        } else if (!(response instanceof EventUndoResponse)) {
            this.resetFields();
            launchActionMessageDialog(getString(R.string.snapshot_remove_component_success), true);
        }

    }

    @Override
    public void onError(ApiException exception) {
        System.out.println(exception.getResponseBody());
        if (this.currentLinkComponentToParentEvent != null) {
            undoComponentSnapshot();
            launchActionMessageDialog(getString(R.string.snapshot_remove_component_error), true);
        }
    }

    private void resetFields() {
        this.parentSystemEditText.setText("");
        this.serialNumberEditText.setText("");
        this.manufacturerEditText.setText("");
        this.modelEditText.setText("");

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

    public void scanParentSystemId(View view) {
        checkCameraPermission(REQUEST_CODE_SYSTEM_CAMERA_PERMISSIONS);
    }

    @Override
    protected void launchScanAction(int permissionCode) {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

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
            case REQUEST_CODE_SYSTEM_CAMERA_PERMISSIONS:
                this.parentSystemEditText.setText(getSystemIdFromUrl(scannedCode));
                break;
            default:
                break;
        }
    }

    // A scanned SystemId is a full DeviceHub URL, we must extract the device Id
    private String getSystemIdFromUrl(String scannedCode) {
        String[] splittedUrl = scannedCode.split("/");
        if (splittedUrl.length > 1) {
            return splittedUrl[splittedUrl.length - 1];
        }
        return scannedCode;
    }

    class SetDatabase implements MenuItem.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ApiServicesImpl.setDb(item.getTitleCondensed().toString());
            return true;
        }
    }
}
