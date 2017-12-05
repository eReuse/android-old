package org.ereuse.scanner.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import org.ereuse.scanner.R;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.utils.ScanUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jamgo SCCL.
 */
public class WorkbenchActivity extends ScanActivity {

    WebView scanWebView;
    private String htmlFieldId;
    private boolean urlField;

    public void setHtmlFieldId(String htmlFieldId) {
        this.htmlFieldId = htmlFieldId;
    }

    public String getHtmlFieldId() {
        return this.htmlFieldId;
    }

    public boolean isUrlField() {
        return urlField;
    }

    public void setUrlField(boolean urlField) {
        this.urlField = urlField;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_workbench);
        setToolbar();

        this.scanWebView = (WebView) this.findViewById(R.id.workbench_webview);

        // Uncomment this to disable caching
        // this.scanWebView.getSettings().setAppCacheEnabled(false);
        // this.scanWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // this.scanWebView.clearCache(true);

        // Uncomment this to enable debugging
        // See https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews
        // this.scanWebView.setWebContentsDebuggingEnabled(true);


        this.scanWebView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "AndroidApp");
        this.scanWebView.getSettings().setJavaScriptEnabled(true);
        this.scanWebView.getSettings().setDomStorageEnabled(true);  // Website can access localStorage
        this.scanWebView.getSettings().setDatabaseEnabled(true);  // Website can access sessionStorage
        this.scanWebView.setWebViewClient(new WebViewClient() {
            /**
             * Shows a message in any not handled error.
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showDefaultWebView();
            }

            /**
             * Only allows certificates from DeviceTag.io / localhost.
             */
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // from https://stackoverflow.com/a/35618839
                if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    SslCertificate certificate = error.getCertificate();
                    SslCertificate.DName issuedBy = certificate.getIssuedBy();
                    if (issuedBy.getOName().equals("DeviceTag.io") && issuedBy.getCName().equals("localhost")) {
                        handler.proceed();
                    } else {
                        handler.cancel();
                    }
                } else {
                    handler.cancel();
                }
            }
        });
        String url = this.getClientServer() + "/workbench/" + ApiServicesImpl.getDb();
        this.scanWebView.loadUrl(url);
    }

    private void showDefaultWebView() {
        String dynamicHtml = getDefaultWorkbenchWebViewContent();
        this.scanWebView.loadData(dynamicHtml, "text/html", "UTF-8");
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
    protected void onResume() {
        super.onResume();
    }

    private String getWorkbenchServer() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return sharedPreferences.getString("workbenchServerAddress", getString(R.string.workbench_default_server_address_value));
    }

    private void setWorkbenchServer(String server) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putString("workbenchServerAddress", server).commit();
    }

    class SetDatabase implements MenuItem.OnMenuItemClickListener {

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
        if (this.isUrlField()) {
            scanResult = ScanUtils.getSystemIdFromUrl(scannedCode);
        }

        this.setHtmlFieldId(null);
        this.setUrlField(false);
        String js = "javascript:(function(){" +
                "var input = $('#" + htmlFieldId + "');" +
                "input.val('"+scanResult+"');" +
                "input.trigger('change');" +
                "})()";
        scanWebView.loadUrl(js);
    }

    @Override
    protected void launchScanAction(int permissionCode) {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

        startActivityForResult(intent, permissionCode);
    }

    protected void checkCameraPermission(int permissionCode, String htmlFieldId, boolean isUrlField) {
        this.setHtmlFieldId(htmlFieldId);
        this.setUrlField(isUrlField);
        this.checkCameraPermission(permissionCode);
    }

    public class WebViewJavaScriptInterface {

        private Context context;

        /*
         * Need a reference to the context in order to sent a post message
         */
        public WebViewJavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public String account() {
            User user = getScannerApplication().getUser();
            JSONObject ob = new JSONObject();
            try {
                ob.accumulate("_id", user.get_id());
                ob.accumulate("email", user.getEmail());
                ob.accumulate("token", user.getToken());
                ob.accumulate("role", user.getRole());
                ob.accumulate("databases", new JSONObject(user.getDatabases()));
                ob.accumulate("defaultDatabase", user.getDefaultDatabase());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ob.toString();
        }

        @JavascriptInterface
        public String workbenchServerAddress() {
            return getWorkbenchServer();
        }

        @JavascriptInterface
        public void setWorkbenchServerAddress(String value) {
            setWorkbenchServer(value);
        }

        /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
        @JavascriptInterface
        public void startJSScan(String htmlFieldId, boolean isUrlField) {
            checkCameraPermission(REQUEST_CODE_JS_CAMERA_PERMISSIONS, htmlFieldId, isUrlField);
            //return "OK";
        }
    }
}
