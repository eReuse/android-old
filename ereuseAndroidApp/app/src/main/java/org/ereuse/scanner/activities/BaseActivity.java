package org.ereuse.scanner.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.ereuse.scanner.R;
import org.ereuse.scanner.ScannerApplication;
import org.ereuse.scanner.data.User;

/**
 * Created by Jamgo SCCL.
 */
public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String SHARED_PREFERENCES_NAME = "ereuse.properties";
    final private int REQUEST_CODE_FINE_LOCATION_PERMISSIONS = 0;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES";
    private static final String LOCATION_KEY = "LOCATION";

    public ScannerApplication getScannerApplication() {
        return (ScannerApplication) this.getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.updateValuesFromBundle(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                return true;
            case R.id.action_logout:
                showLogoutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void setToolbar() {
        Toolbar ereuseToolbar = (Toolbar) findViewById(R.id.ereuse_toolbar);
        //ereuseToolbar.setLogo(R.drawable.header);
        ereuseToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(ereuseToolbar);
    }

    public void logDebug(String tag, String message) {
        this.getScannerApplication().logDebug(tag, message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FINE_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    this.initLocation();

                    if (this.getScannerApplication().getGoogleApiClient().isConnected() && !this.getScannerApplication().isRequestingLocationUpdates()) {
                        this.getScannerApplication().triggerLocationStarter();
                    }
                } else {
                    showPermissionDeniedDialog();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // GoogleApiClient.ConnectionCallbacks Methods
    @Override
    public void onConnected(Bundle connectionHint) {
        logDebug("GoogleApiClient", "Connected!");
        Location location = LocationServices.FusedLocationApi.getLastLocation(this.getScannerApplication().getGoogleApiClient());
        this.getScannerApplication().setLocation(location);
        if (location != null) {
            this.getScannerApplication().updateLocationUI();
        }

        this.getScannerApplication().triggerLocationStarter();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO
    }

    // GoogleApiClient.OnConnectionFailedListener Method
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO
    }

    // LocationListener Method
    @Override
    public void onLocationChanged(Location location) {
        //5 minutes in miliseconds
        long fiveMinutes = 300000;
        long fiveMinutesAgo = System.currentTimeMillis() - fiveMinutes;
        Location lastLocation = this.getScannerApplication().getLocation();
        if (location.getAccuracy() != 0.0f
                && (lastLocation == null || location.getAccuracy() < lastLocation.getAccuracy()) || lastLocation.getTime() < fiveMinutesAgo) {
            lastLocation = location;
            String locationMessage = "lat: " + lastLocation.getLatitude()
                    + ", long: " + lastLocation.getLongitude()
                    + ", alt: " + lastLocation.getAltitude()
                    + ", acc: " + lastLocation.getAccuracy();
            logDebug("LocationChanged",locationMessage);

            this.getScannerApplication().setLocation(location);
            this.getScannerApplication().updateLocationUI();
        }
    }

    //Location stuff
    public void initLocation() {
        this.buildGoogleApiClient();
        this.getScannerApplication().getGoogleApiClient().connect();
    }

    private synchronized void buildGoogleApiClient() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.getScannerApplication().setGoogleApiClient(googleApiClient);
    }

    protected void checkAllPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int locationFinePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if(locationFinePermissionCheck != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
            }
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                this.getScannerApplication().setRequestingLocationUpdates(savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY));
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                Location location = savedInstanceState.getParcelable(LOCATION_KEY);
                this.getScannerApplication().setLocation(location);
                this.getScannerApplication().updateLocationUI();
            }
        }
    }

    public void showAboutDialog(){

        final TextView message = new TextView(this);
        final SpannableString spannableString = new SpannableString(getString(R.string.dialog_about_body));
        Linkify.addLinks(spannableString, Linkify.ALL);
        message.setText(spannableString);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.about);
        dialog.setTitle(getString(R.string.dialog_about_title));
        dialog.setMessage(spannableString);
        //dialog.setView(message);
        dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void showLogoutDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.logout);
        dialog.setTitle(getString(R.string.logout));
        dialog.setMessage(getString(R.string.logout_message));
        dialog.setPositiveButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                doLogout();
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

    public void showPermissionDeniedDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.logout);
        dialog.setTitle("eReuse permission");
        dialog.setMessage("This permission is necessary for a correct application behaviour, please enable it via device settings before try to use it");
        dialog.setPositiveButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();
    }

    private void doLogout(){
        User user = this.getScannerApplication().getUser();
        if (user != null) {
            user.update(user.getEmail(), null, user.getRole(), user.get_id(), user.getDatabases(), user.getDefaultDatabase());
        }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
    }

    protected void checkLogin()
    {
        User user = this.getScannerApplication().getUser();
        if (user == null || user.getToken() == null ) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    protected void launchActionMessageDialog(String title, String message) {
        launchActionMessageDialog(title, message, false);
    }

    protected void launchActionMessageDialog(String title, String message, boolean useDialogCallback) {
        //Show action result
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (title != null) {
            dialog.setTitle(title);
        }
        if(useDialogCallback) {
            dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback();
                }
            });
        } else {
            dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog.setMessage(message);
        dialog.show();
    }

    protected void launchActionMessageDialog(String message) {
        launchActionMessageDialog(null, message, false);
    }

    protected void launchActionMessageDialog(String message, boolean useDialogCallback) {
        launchActionMessageDialog(null, message, true);
    }

    protected void dialogCallback() {

    }
}
