package org.ereuse.scanner;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.ereuse.scanner.activities.LocationListenerActivity;
import org.ereuse.scanner.activities.LoginActivity;
import org.ereuse.scanner.data.Device;
import org.ereuse.scanner.data.Manufacturer;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.BackgroundStatusService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jamgo SCCL.
 */
public class ScannerApplication extends Application {

    private boolean debug = true;
    private String server;
    private String clientServer;
    private User user;
    private boolean gpsDialogShown = false;
    private Timer timer;
    BackgroundStatusService backgroundStatusService = BackgroundStatusService.getInstance();
    private LoginActivity loginActivity;
    private LocationListenerActivity currentLocationActivity;

    private List<Manufacturer> manufacturers;

    private Device latestSuccessfulSnapshot;

    private Integer scanType;

    private GoogleApiClient googleApiClient;
    private Location location;
    private boolean requestingLocationUpdates;
    private LocationRequest locationRequest;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
        // todo workaround, please take it from devicehub directly
        switch (server) {
            case "https://api.devicetag.io/":
                this.clientServer = "https://www.devicetag.io/app";
                break;
            case "https://api-us.devicetag.io/":
                this.clientServer = "https://us.devicetag.io";
                break;
            case "https://api-beta.devicetag.io/":
                this.clientServer = "https://beta.devicetag.io/app";
                break;
            case "http://devicehub.ereuse.net/":
                this.clientServer = "http://devicehub-client.ereuse.net";
                break;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLatestSuccessfulSnapshot(Device latestSuccessfulSnapshot) {
        this.latestSuccessfulSnapshot = latestSuccessfulSnapshot;
    }

    public Device getLatestSuccessfulSnapshot() {
        return latestSuccessfulSnapshot;
    }

    public Integer getScanType() {
        return this.scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setGPSDialogShown(boolean gpsDialogShown) {
        this.gpsDialogShown = gpsDialogShown;
    }

    public boolean getGPSDialogShown() {
        return this.gpsDialogShown;
    }

    public LoginActivity getLoginActivity() {
        return this.loginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public LocationListenerActivity getCurrentLocationActivity() {
        return this.currentLocationActivity;
    }

    public void setCurrentLocationActivity(LocationListenerActivity currentLocationActivity) {
        this.currentLocationActivity = currentLocationActivity;
    }

    public List<Manufacturer> getManufacturers() {
        return manufacturers;
    }

    public void setManufacturers(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public GoogleApiClient getGoogleApiClient() {
        return this.googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public boolean isRequestingLocationUpdates() {
        return this.requestingLocationUpdates;
    }

    public void setRequestingLocationUpdates(boolean requestingLocationUpdates) {
        this.requestingLocationUpdates = requestingLocationUpdates;
    }

    public LocationRequest getLocationRequest() {
        return this.locationRequest;
    }

    public void triggerLocationStarter() {

        //Save status with background/foreground flags
        if (backgroundStatusService.wasInBackground()) {
            backgroundStatusService.setForeground();
            logDebug(getString(R.string.log_background_control), getString(R.string.log_set_to_foreground));
            //Location stuff
            this.startLocationUpdates();
        } else if (backgroundStatusService.isBackgroundTimerStarted()) {
            //Not yet accounted as background, cancel timer to not disable location.
            timer.cancel();
            timer.purge();
        }

        if (backgroundStatusService.isBackgroundTimerStarted()) {
            backgroundStatusService.stopBackgroundTimer();
        }
    }

    public void triggerLocationStopper() {
        BackgroundStatusService backgroundStatusService = BackgroundStatusService.getInstance();
        backgroundStatusService.startBackgroundTimer();
        timer = new Timer();
        Long delay = 10000L;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BackgroundStatusService backgroundStatusService = BackgroundStatusService.getInstance();
                backgroundStatusService.setBackground();
                backgroundStatusService.stopBackgroundTimer();
                logDebug(getString(R.string.log_background_control), getString(R.string.log_set_to_background));
                //Location stuff
                ScannerApplication.this.stopLocationUpdates();
            }
        }, delay);
    }

    public void updateLocationUI() {
        //Location location = this.getLoginActivity().getLocation();
        logDebug("ScannerApplication", "lat: " + location.getLatitude()
                + ", long: " + location.getLongitude()
                + ", alt: " + location.getAltitude()
                + ", acc: " + location.getAccuracy());
        if (this.currentLocationActivity != null) {
            this.currentLocationActivity.updateLocationUI(location);
        } else {
            logDebug("ScannerApplication", "empty currentLocationActivity");
        }
    }

    private void stopLocationUpdates() {
        if (this.googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this.loginActivity);
        }
        this.requestingLocationUpdates = false;
        currentLocationActivity = null;
        logDebug("ScannerApplication", "Stopping Location Updates");
    }

    private void startLocationUpdates() {
        GoogleApiClient googleApiClient = this.getGoogleApiClient();
        LocationRequest locationRequest = this.getLocationRequest();
        if (googleApiClient != null) {
            if (googleApiClient.isConnected() && !this.isRequestingLocationUpdates()) {
                if (locationRequest == null) {
                    locationRequest = this.createLocationRequest();
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this.loginActivity);
                this.setRequestingLocationUpdates(true);
                logDebug("ScannerApplication", "Starting Location Updates");
            }
        }
    }

    public LocationRequest createLocationRequest() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(5000);
        this.locationRequest.setFastestInterval(5000);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return this.locationRequest;
    }

    public void logDebug(String tag, String message) {
        if (this.debug) {
            System.out.println("[eReuseApp] [" + tag + "] " + message);
        }
    }

    public String getClientServer() {
        return clientServer;
    }
}
