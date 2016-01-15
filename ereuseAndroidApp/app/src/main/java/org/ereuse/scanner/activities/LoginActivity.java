package org.ereuse.scanner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.ValidationService;
import org.ereuse.scanner.services.api.ApiException;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.LoginResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class LoginActivity extends AsyncActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES";
    private static final String LOCATION_KEY = "LOCATION";
    private boolean requestingLocationUpdates;
    private Location location;

    public Location getLocation() {
        return this.location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Spinner spinner = (Spinner) findViewById(R.id.serversSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.login_servers, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);

        //If login succeeded and different user from the stored one, then update the user information
        String userEmailPreference = sharedPreferences.getString("user","");
        String userPasswordPreference = sharedPreferences.getString("password","");

        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        emailEditText.setText(userEmailPreference);
        passwordEditText.setText(userPasswordPreference);

        this.initLocation();
        this.updateValuesFromBundle(savedInstanceState);
        this.getScannerApplication().setLoginActivity(this);

        setToolbar();
    }

    @Override
    public void onResume(){
        super.onResume();
        ValidationService.checkInternetConnection(this);

        if (this.googleApiClient.isConnected() && !this.requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    public void doLogin(View view) {
        if (ValidationService.checkInternetConnection(this)) {
            Spinner serverSpinner = (Spinner) findViewById(R.id.serversSpinner);
            EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
            EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

            String server = serverSpinner.getSelectedItem().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            //Save login information into context
            User user = new User();
            user.setPassword(password);
            user.setEmail(email);
            getScannerApplication().setUser(user);
            logDebug("LoginActivity","server = " + server + ", email = " + email + ", password = " + password);
            // TODO Validate mandatory fields

            // do login
            AsyncService asyncService = new AsyncService(this);
            asyncService.doLogin(email, password, server);
        }
    }

    @Override
    public String getServer() {
        Spinner serverSpinner = (Spinner) findViewById(R.id.serversSpinner);
        String server = serverSpinner.getSelectedItem().toString();
        return server;
    }

    @Override
    public List<Integer> getMainLayoutFields() {
        List<Integer> fieldIds = new ArrayList<Integer>();
        fieldIds.add(R.id.serversSpinner);
        fieldIds.add(R.id.emailEditText);
        fieldIds.add(R.id.passwordEditText);
        fieldIds.add(R.id.loginButton);
        return fieldIds;
    }

    @Override
    public void onSuccess(ApiResponse response) {
        super.onSuccess(response);

        LoginResponse loginResponse = (LoginResponse) response;
        User user = getScannerApplication().getUser();
        user.update(loginResponse.getEmail(), loginResponse.getToken(), loginResponse.getRole(), loginResponse.get_id());
        this.getScannerApplication().setServer(this.getServer());
        this.getScannerApplication().setUser(user);

        // Go to next activity
        Intent chooseIntent = new Intent(this, ChooseActivity.class);
        startActivity(chooseIntent);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);

        //If login succeeded and different user from the stored one, then update the user information
        String userEmailPreference = sharedPreferences.getString("user", "");
        if(!user.getEmail().equals(userEmailPreference)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user",user.getEmail());
            editor.putString("password", user.getPassword());
            editor.commit();
        }
    }

    @Override
    public void onError(ApiException exception) {
        super.onError(exception);

        this.getScannerApplication().setServer(null);
        this.getScannerApplication().setUser(null);
    }


    //Location stuff
    private void initLocation() {
        this.buildGoogleApiClient();
        this.googleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient() {
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                this.requestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                this.location = savedInstanceState.getParcelable(LOCATION_KEY);
                this.getScannerApplication().updateLocationUI();
            }
        }
    }

    public void startLocationUpdates() {
        if (this.googleApiClient.isConnected() && !this.requestingLocationUpdates) {
            if (this.locationRequest == null) {
                this.createLocationRequest();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, this);
            this.requestingLocationUpdates = true;
            logDebug("LoginActivity","Starting Location Updates");
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this);
        this.requestingLocationUpdates = false;
        logDebug("LoginActivity","Stopping Location Updates");
    }

    private void createLocationRequest() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(5000);
        this.locationRequest.setFastestInterval(5000);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    // GoogleApiClient.ConnectionCallbacks Methods
    @Override
    public void onConnected(Bundle connectionHint) {
        this.location = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);
        if (this.location != null) {
            this.getScannerApplication().updateLocationUI();
        }

        this.startLocationUpdates();
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
        if (location.getAccuracy() != 0.0f
                && (this.location == null || location.getAccuracy() < this.location.getAccuracy())) {
            this.location = location;
            String locationMessage = "lat: " + this.location.getLatitude()
                    + ", long: " + this.location.getLongitude()
                    + ", alt: " + this.location.getAltitude()
                    + ", acc: " + this.location.getAccuracy();
            logDebug("LocationChanged",locationMessage);

            this.getScannerApplication().updateLocationUI();
        }
    }


}
