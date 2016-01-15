package org.ereuse.scanner.services;

/**
 * Created by Jamgo SCCL.
 */
public class BackgroundStatusService {

    private static BackgroundStatusService instance = new BackgroundStatusService();

    private boolean inBackground = true;
    private boolean backgroundTimerStarted = false;

    private BackgroundStatusService(){}

    //Get the only object available
    public static BackgroundStatusService getInstance(){
        return instance;
    }

    public void setBackground() {
        inBackground = true;
    }

    public void setForeground() {
        inBackground = false;
    }

    public boolean wasInBackground() {
        return this.inBackground;
    }

    public boolean isBackgroundTimerStarted() {
        return this.backgroundTimerStarted;
    }

    public void stopBackgroundTimer() {
        this.backgroundTimerStarted = false;
    }
    public void startBackgroundTimer() {
        this.backgroundTimerStarted = true;
    }


}
