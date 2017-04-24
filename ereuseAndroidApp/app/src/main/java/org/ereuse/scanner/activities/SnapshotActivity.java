package org.ereuse.scanner.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.Device;
import org.ereuse.scanner.data.Grade;
import org.ereuse.scanner.data.GradeOption;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.services.api.SnapshotResponse;
import org.w3c.dom.Text;

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
    Boolean deviceSubTypeSpinnerReloadUpdate = false;

    TextView gradeAppearanceTextView;
    TextView gradeFunctionalityTextView;
    TextView gradeBiosTextView;
    CheckBox gradeLabelsCheckBox;

    private AlertDialog gradeSelectorDialog = null;

    Grade gradeConditions = new Grade();

    static enum GRADE_OPTION {
        APPEARANCE,
        FUNCTIONALITY,
        BIOS
    }

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

        this.gradeAppearanceTextView = (TextView) this.findViewById(R.id.gradeAppearanceValueLabel);
        this.gradeFunctionalityTextView = (TextView) this.findViewById(R.id.gradeFunctionalityValueLabel);
        this.gradeBiosTextView = (TextView) this.findViewById(R.id.gradeBiosValueLabel);
        this.gradeLabelsCheckBox = (CheckBox) this.findViewById(R.id.gradeLabelsCheckBox);

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
                if (deviceSubTypeSpinnerReloadUpdate) {
                    deviceSubTypeSpinnerReloadUpdate = false;
                } else {
                    deviceSubTypeSpinner.setAdapter(deviceSubTypeAdapter);
                    deviceSubTypeSpinner.setVisibility(View.VISIBLE);
                }
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

        this.reloadLatestSuccessfulSnapshotData();

        this.deviceSubTypeSpinner.setVisibility(View.VISIBLE);
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
            asyncService.doSnapshot(this.getServer(), this.getUser(), deviceType, deviceSubType, serialNumber, model, manufacturer, licenseKey, giverId, refurbisherId, systemId, comment, gradeConditions);
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

            this.resetUniqueFields();
            this.saveSuccessfulSnapshotData();

            launchActionMessageDialog(getString(R.string.snapshot_success), true);
        }

    }

    private void resetUniqueFields() {
        this.serialNumberEditText.setText("");
        this.licenseKeyEditText.setText("");
        this.giverEditText.setText("");
        this.refurbishedEditText.setText("");
        this.systemEditText.setText("");
    }

    private void saveSuccessfulSnapshotData() {
        Device successSnapshot = new Device();
        successSnapshot.setModel(this.modelEditText.getText().toString());
        successSnapshot.setManufacturer(this.manufacturerEditText.getText().toString());
        successSnapshot.setDeviceType(this.deviceType);
        successSnapshot.setDeviceSubType(this.deviceSubType);
        this.getScannerApplication().setLatestSuccessfulSnapshot(successSnapshot);
    }

    private void reloadLatestSuccessfulSnapshotData() {
        Device successSnapshot = this.getScannerApplication().getLatestSuccessfulSnapshot();

        if (successSnapshot != null) {
            this.modelEditText.setText(successSnapshot.getModel());
            this.manufacturerEditText.setText(successSnapshot.getManufacturer());

            String[] spinnerDeviceSubTypeValues = DEVICETYPES.get(successSnapshot.getDeviceType()).toArray(new String[DEVICETYPES.get(successSnapshot.getDeviceType()).size()]);
            int deviceTypePosition = 0;
            for (String eachDeviceType : DEVICETYPES.keySet()) {
                if (eachDeviceType.equals(successSnapshot.getDeviceType())) {
                    break;
                }
                deviceTypePosition++;
            }
            this.deviceTypeSpinner.setSelection(deviceTypePosition);
            ArrayAdapter<String> deviceSubTypeAdapter = new ArrayAdapter<String>(SnapshotActivity.this, R.layout.spinner_item, spinnerDeviceSubTypeValues);

            this.deviceSubTypeSpinner.setAdapter(deviceSubTypeAdapter);


            this.deviceType = successSnapshot.getDeviceType();
            this.deviceSubType = successSnapshot.getDeviceSubType();

            this.deviceSubTypeSpinner.setVisibility(View.VISIBLE);
            this.deviceSubTypeSpinnerReloadUpdate = true;

            refreshDeviceSubTypeSpinner(spinnerDeviceSubTypeValues);


        }
    }

    public void refreshDeviceSubTypeSpinner( String[] spinnerDeviceSubTypeValues) {
        int deviceSubTypePosition = 0;
        for (String eachDeviceSubType : spinnerDeviceSubTypeValues) {
            if (eachDeviceSubType.equals(this.deviceSubType)) {
                break;
            }
            deviceSubTypePosition++;
        }
        deviceSubTypeSpinner.setSelection(deviceSubTypePosition, true);
    }

    public void showHelp(){
        RelativeLayout snapshotHelpLayout = (RelativeLayout) findViewById(R.id.SnapshotHelpLayout);
        TextView helpCreate = (TextView) findViewById(R.id.snapshot_help_text);

        String dialogText = getString(R.string.snapshot_dialog_help_multiple_snapshots_in_row);
        helpCreate.setText(dialogText);
        snapshotHelpLayout.setVisibility(View.VISIBLE);
    }

    public void dissmissHelp(View view) {
        RelativeLayout mapHelpLayout = (RelativeLayout) findViewById(R.id.SnapshotHelpLayout);
        mapHelpLayout.setVisibility(View.GONE);
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
                this.systemEditText.setText(getSystemIdFromUrl(scannedCode));
                break;
            default:
                break;
        }
    }

    // A scanned SystemId is a full DeviceHub URL, we must extract the device Id
    private String getSystemIdFromUrl(String scannedCode) {
        String[] splittedUrl = scannedCode.split("/");
        if (splittedUrl.length > 1) {
            return splittedUrl[splittedUrl.length -1 ];
        }
        return scannedCode;
    }

    @Override
    protected void dialogCallback() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("snapshotHelpShown", false)) {
                sharedPreferences.edit().putBoolean("snapshotHelpShown", true).commit();
                showHelp();
            }
    }

    public void showAppearanceSelector(View view) {
        String title = getString(R.string.snapshot_grade_appearance_help);
        int gradeAttributeArray =  R.array.snapshot_grade_appearance_array;
        this.showGradeSelector(GRADE_OPTION.APPEARANCE.toString(), title,gradeAttributeArray,gradeAppearanceTextView, GRADE_OPTION.APPEARANCE);
    }

    public void showFunctionalitySelector(View view) {
        String title = getString(R.string.snapshot_grade_functionality_help);
        int gradeAttributeArray =  R.array.snapshot_grade_functionality_array;
        this.showGradeSelector(GRADE_OPTION.FUNCTIONALITY.toString(),title,gradeAttributeArray,gradeFunctionalityTextView, GRADE_OPTION.FUNCTIONALITY);
    }

    public void showBiosSelector(View view) {
        String title = getString(R.string.snapshot_grade_bios_help);
        int gradeAttributeArray =  R.array.snapshot_grade_bios_array;
        this.showGradeSelector(GRADE_OPTION.BIOS.toString(),title,gradeAttributeArray,gradeBiosTextView, GRADE_OPTION.BIOS);
    }

    private void showGradeSelector(String title, String helpText, final int gradeAttributeArray, final TextView gradeTextView, final GRADE_OPTION gradeOption) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gradeDialogView = inflater.inflate(R.layout.single_choice_dialog,
                null, false);

        TextView gradeHelpTextView = (TextView) gradeDialogView.findViewById(R.id.gradeHelpTextView);
        gradeHelpTextView.setText(helpText);
        RadioGroup gradeRadioGroup = (RadioGroup) gradeDialogView.findViewById(R.id.gradeRadioGroup);
        gradeRadioGroup.removeAllViews();
            gradeRadioGroup.clearDisappearingChildren();

        TextView gradeTitleTextView = (TextView) gradeDialogView.findViewById(R.id.gradeTitleTextView);
        gradeTitleTextView.setText(title);

        String[] gradePossibleValues = getResources().getStringArray(gradeAttributeArray);
        for (int i = 0; i< gradePossibleValues.length; i++) {
            String eachGradePossibleValue = gradePossibleValues[i];
            RadioButton gradePossibleValueButton = new RadioButton(this);
            gradePossibleValueButton.setText(eachGradePossibleValue);
            gradePossibleValueButton.setTag(i);
            gradeRadioGroup.addView(gradePossibleValueButton, i);
        }

        dialogBuilder.setView(gradeDialogView);
        dialogBuilder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SnapshotActivity.this, "aaaaaa", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.gradeSelectorDialog = dialogBuilder.create();

        gradeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int realCheckedId = getRealCheckedId(group, checkedId);
                gradeSelectorCallback(realCheckedId, gradeTextView, gradeAttributeArray, gradeOption);
                SnapshotActivity.this.gradeSelectorDialog.dismiss();// dismiss the alertbox after chose option
            }
        });

        this.gradeSelectorDialog.show();

/*        AlertDialog.Builder gradeDialog = new AlertDialog.Builder(this);

        TextView dialogTitle = new TextView(this);
        dialogTitle.setText(title);
        dialogTitle.setTextSize(18);

        LinearLayout.LayoutParams customDialogTitleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customDialogTitleParams.setMargins(10,10,10,10);
        dialogTitle.setLayoutParams(customDialogTitleParams);
        gradeDialog.setCustomTitle(dialogTitle);

        gradeDialog.setSingleChoiceItems(gradeAttributeArray, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                gradeSelectorCallback(item, gradeTextView, gradeAttributeArray, gradeOption);
                dialog.dismiss();// dismiss the alertbox after chose option

            }
        });
        gradeDialog.create().show();
        */

    }

    private int getRealCheckedId(RadioGroup group, int checkedId) {
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        return (int) selectedRadioButton.getTag();
    }

    private void gradeSelectorCallback(int selectedItemId, TextView gradeTextView, int gradeAttributeArray, GRADE_OPTION gradeOption) {
//        Toast.makeText(getApplicationContext(),
//                "Appearance = "+getResources().getStringArray(R.array.snapshot_grade_appearance_array_values)[item], Toast.LENGTH_SHORT).show();
        gradeTextView.setText(getResources().getStringArray(gradeAttributeArray)[selectedItemId]);
        String gradeOptionValue = null;
        switch(gradeOption) {
            case APPEARANCE:
                GradeOption appearance = new GradeOption(getResources().getStringArray(R.array.snapshot_grade_appearance_array_values)[selectedItemId]);
                gradeConditions.setAppearance(appearance);
                break;
            case FUNCTIONALITY:
                GradeOption functionality = new GradeOption(getResources().getStringArray(R.array.snapshot_grade_functionality_array_values)[selectedItemId]);
                gradeConditions.setFunctionality(functionality);
                break;
            case BIOS:
                GradeOption bios = new GradeOption(getResources().getStringArray(R.array.snapshot_grade_bios_array_values)[selectedItemId]);
                gradeConditions.setBios(bios);
                break;
        }
    }
}
