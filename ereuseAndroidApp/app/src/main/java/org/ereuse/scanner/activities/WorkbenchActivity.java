package org.ereuse.scanner.activities;

import android.app.Activity;
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
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.utils.ScanUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Jamgo SCCL.
 */
public class WorkbenchActivity extends ScanActivity {

    private SubMenu selectDb;
    ArrayList<String> databases;
    WebView scanWebView;
    private String workbenchServerAddress;
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
        setToolbar();

        this.workbenchServerAddress = getWorkbenchServer();

        this.scanWebView = (WebView)this.findViewById(R.id.workbench_webview);

        // Uncomment this to disable caching
        // this.scanWebView.getSettings().setAppCacheEnabled(false);
        // this.scanWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // this.scanWebView.clearCache(true);

        this.scanWebView.getSettings().setJavaScriptEnabled(true);
        this.scanWebView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "app");
        this.scanWebView.getSettings().setJavaScriptEnabled(true);
        this.scanWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WorkbenchActivity.this, "ERROR retrieving workbench information" , Toast.LENGTH_LONG).show();
                showDefaultWebView();
            }
            @Override
            public void onReceivedHttpError(
                WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Toast.makeText(WorkbenchActivity.this, "ERROR retrieving workbench information" , Toast.LENGTH_LONG).show();
                showDefaultWebView();
            }
        });
        reloadWebView();
    }



    private void showDefaultWebView() {
        String dynamicHtml = getDefaultWorkbenchWebViewContent();
        this.scanWebView.loadData(dynamicHtml,"text/html","UTF-8");
    }

    private void reloadWebView() {
        if(this.workbenchServerAddress.equals("")) {
          showDefaultWebView();
        } else {
            final Activity activity = this;

            this.scanWebView.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
                }
            });

            try { //check valid url
                final URLConnection connection = new URL(this.workbenchServerAddress).openConnection();
                this.scanWebView.loadUrl(this.workbenchServerAddress);
            } catch (Exception e) {
                showDefaultWebView();
            }

        }
    }

    private String getDefaultWorkbenchWebViewContent() {
        String result = null;
        InputStream defaultWorkbecnhContentInputStream = getResources().openRawResource(R.raw.workbench_default_webview);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(defaultWorkbecnhContentInputStream));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            result = out.toString();
            reader.close();
        } catch (IOException e) {
        }
            return result;

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private String getWorkbenchServer() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        return sharedPreferences.getString("workbenchServerAddress", getString(R.string.workbench_default_server_address_value));
    }

    private void setWorkbenchServer(String server) {
        this.workbenchServerAddress = server;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        sharedPreferences.edit().putString("workbenchServerAddress", server).commit();
        this.reloadWebView();
    }

    public void showSettingsDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final EditText workbenchServerAddressEditText = new EditText(this);
        workbenchServerAddressEditText.setText(workbenchServerAddress);

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
                .setNeutralButton(R.string.dialog_reset_default, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setWorkbenchServer(getString(R.string.workbench_default_server_address_value));
                        dialog.dismiss();
                    }
                })
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
        inflater.inflate(R.menu.workbench_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettingsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showLogoutDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.logout);
        dialog.setTitle(getString(R.string.back_to_login));
        dialog.setMessage(getString(R.string.back_to_login_message));
        dialog.setPositiveButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                doBackToLogin();
            }
        });
        dialog.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void doBackToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
