package org.ereuse.scanner;

import android.app.Application;
import android.location.Location;

import org.ereuse.scanner.activities.LocationListenerActivity;
import org.ereuse.scanner.activities.LoginActivity;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.BackgroundStatusService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jamgo SCCL.
 */
public class ScannerApplication extends Application {

    private boolean debug = true;
    private String server;
    private User user;
    private boolean gpsDialogShown = false;
    private Timer timer;
    BackgroundStatusService backgroundStatusService = BackgroundStatusService.getInstance();
    private LoginActivity loginActivity;
    private LocationListenerActivity currentLocationActivity;

    private Integer scanType;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public Integer getScanType() {
        return this.scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void incrementCurrentActivities() {
        setForeground();
    }
    public void decrementCurrentActivities() {
            setBackground();
    }

    private void setForeground() {

        //Save status with background/foreground flags
        if (backgroundStatusService.wasInBackground()) {
            backgroundStatusService.setForeground();
            logDebug(getString(R.string.log_background_control), getString(R.string.log_set_to_foreground));
            //Location stuff
            loginActivity.startLocationUpdates();
        } else if (backgroundStatusService.isBackgroundTimerStarted()) {
            //Not yet accounted as background, cancel timer to not disable location.

                timer.cancel();
                timer.purge();
        }

        if (backgroundStatusService.isBackgroundTimerStarted()) {
            backgroundStatusService.stopBackgroundTimer();
        }
    }

    private void setBackground() {
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
                loginActivity.stopLocationUpdates();
                currentLocationActivity = null;
            }
        }, delay);
    }

    public void updateLocationUI() {
        Location location = this.getLoginActivity().getLocation();
        logDebug("ScannerApplication","lat: " + location.getLatitude()
                        + ", long: " + location.getLongitude()
                        + ", alt: " + location.getAltitude()
                        + ", acc: " + location.getAccuracy());
        if (this.currentLocationActivity != null) {
            this.currentLocationActivity.updateLocationUI(location);
        } else {
            logDebug("ScannerApplication","empty currentLocationActivity");
        }
    }

    public void logDebug(String tag,String message) {
        if (this.debug) {
            System.out.println("["+tag+"] "+message);
        }
    }
}
