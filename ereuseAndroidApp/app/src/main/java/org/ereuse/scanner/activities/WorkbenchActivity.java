package org.ereuse.scanner.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.common.StringUtils;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.utils.ScanUtils;

import java.util.ArrayList;

/**
 * Created by Jamgo SCCL.
 */
public class WorkbenchActivity extends ScanActivity {

    private SubMenu selectDb;
    ArrayList<String> databases;
    WebView scanWebView;
    private EditText workbenchServerAddressEditText;
    private String htmlFieldId;
    private boolean urlField;

    public void setHtmlFieldId(String htmlFieldId) { this.htmlFieldId = htmlFieldId; }
    public String getHtmlFieldId() { return this.htmlFieldId; }
    public boolean isUrlField() { return urlField; }
    public void setUrlField(boolean urlField) { this.urlField = urlField; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_workbench);
        databases = getScannerApplication().getUser().getDatabases();
        setToolbar();

        this.workbenchServerAddressEditText = new EditText(this);
        this.workbenchServerAddressEditText.setText(getWorkbenchServer());

        this.scanWebView = (WebView)this.findViewById(R.id.workbench_webview);

        this.scanWebView.getSettings().setJavaScriptEnabled(true);
        this.scanWebView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "app");

        String dynamicHtml = "<html>" +
                "<div>" +
                "<p>Clica per escanejar:</p>" +
                "</div>" +
                "<form>" +
                "<p>common field:</p>"+
                "<input type='button' value='scan' onclick='app.startJSScan(\"scanResultField\", false)' />" +
                "<input type='text' id='scanResultField' />" +
                "<p>System id:</p>"+
                "<input type='button' value='scan' onclick='app.startJSScan(\"systemIdField\", true)' />" +
                "<input type='text' id='systemIdField' />" +
                "</form>" +
                "</html>";

        this.scanWebView.getSettings().setJavaScriptEnabled(true);
        this.scanWebView.loadData(dynamicHtml,"text/html","UTF-8");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkLogin();
    }

    private String getWorkbenchServer() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        return sharedPreferences.getString("workbenchServerAddress", getString(R.string.default_workbench_server_address));
    }

    private void setWorkbenchServer(String server) {
        this.workbenchServerAddressEditText.setText(server);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        sharedPreferences.edit().putString("workbenchServerAddress", server).commit();
    }

    public void showWorkbenchSettings(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(getString(R.string.workbench_server_address))
                .setView(workbenchServerAddressEditText)
                .setPositiveButton(R.string.dialog_ack, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setWorkbenchServer(workbenchServerAddressEditText.getText().toString());
                        ((ViewGroup)workbenchServerAddressEditText.getParent()).removeAllViews();
                        dialog.dismiss();
                    }
                })
//                .setNeutralButton(R.string.dialog_reset_default, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        setWorkbenchServer(getString(R.string.default_workbench_server_address));
//                    }
//                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ViewGroup)workbenchServerAddressEditText.getParent()).removeAllViews();
                        dialog.dismiss();
                    }
                }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_db_menu, menu);
        MenuItem item = menu.findItem(R.id.action_select_db);
        selectDb = item.getSubMenu();
        SetDatabase setDatabase = new SetDatabase();
        for(String database : databases)
            selectDb.add(database).setOnMenuItemClickListener(setDatabase);
        return true;
    }


    class SetDatabase implements MenuItem.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ApiServicesImpl.setDb(item.getTitleCondensed().toString());
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_CODEBAR_PERMISSIONS.contains(requestCode)) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
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

        //Toast.makeText(this, getString(R.string.toast_barcode_scanned) + " " + scannedCode, Toast.LENGTH_LONG).show();
        //scanWebView.loadUrl("javascript:document.getElementById('scanresult').innerHTML = '"+scannedCode+"'");
        String htmlFieldId = this.getHtmlFieldId();
        String scanResult = scannedCode;
        if(this.isUrlField()) {
            scanResult = ScanUtils.getSystemIdFromUrl(scannedCode);
        }

        this.setHtmlFieldId(null);
        this.setUrlField(false);

        scanWebView.loadUrl("javascript:(function(){document.getElementById('" + htmlFieldId + "').value = '"+scanResult+"';})()");

    }

    @Override
    protected void launchScanAction(int permissionCode) {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus,true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash,false);

        startActivityForResult(intent, permissionCode);
    }

    protected void checkCameraPermission(int permissionCode, String htmlFieldId, boolean isUrlField) {
        this.setHtmlFieldId(htmlFieldId);
        this.setUrlField(isUrlField);
        this.checkCameraPermission(permissionCode);
    }

    public class WebViewJavaScriptInterface{

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context){
            this.context = context;
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void startJSScan(String htmlFieldId, boolean isUrlField){
            checkCameraPermission(REQUEST_CODE_JS_CAMERA_PERMISSIONS, htmlFieldId, isUrlField);
            //return "OK";
        }
    }
}
