package org.ereuse.scanner.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import org.ereuse.scanner.R;
import org.ereuse.scanner.ScannerApplication;
import org.ereuse.scanner.activities.AsyncActivity;

/**
 * Created by Jamgo SCCL.
 */
public class ValidationService {

    public static boolean checkInternetConnection(AsyncActivity sourceActivity) {

        boolean isConnected = false;

        ScannerApplication scanApp = sourceActivity.getScannerApplication();

        ConnectivityManager cm = (ConnectivityManager) sourceActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnectedOrConnecting();
        }

        if(!isConnected) {
            scanApp.setGPSDialogShown(false);
            launchActionMessageDialog(sourceActivity,
                    scanApp.getString(R.string.dialog_validation_internet_disabled_title),
                    scanApp.getString(R.string.dialog_validation_internet_disabled_message),
                    Settings.ACTION_SETTINGS);
            return false;
        }

        return true;
}

    public static boolean checkLocationConnection(AsyncActivity sourceActivity) {

        boolean GPSLocationEnabled = false;
        boolean NetworkLocationEnabled = false;

        ScannerApplication scanApp = sourceActivity.getScannerApplication();
        LocationManager lm = (LocationManager)sourceActivity.getSystemService(Context.LOCATION_SERVICE);

        try {
            GPSLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            NetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {
            //If could not check Location configuration, a dialog to open location settigns will be displayed by default.
        }

        if (!NetworkLocationEnabled && !GPSLocationEnabled){
            scanApp.setGPSDialogShown(false);
            scanApp.setLocation(null);
            launchActionMessageDialog(sourceActivity,
                    scanApp.getString(R.string.dialog_validation_location_disabled_title),
                    scanApp.getString(R.string.dialog_validation_location_disabled_message),
                    Settings.ACTION_SETTINGS);
            return false;
        } else if (!GPSLocationEnabled && !scanApp.getGPSDialogShown()) {
            scanApp.setGPSDialogShown(true);
            launchActionMessageDialog(sourceActivity,
                    scanApp.getString(R.string.dialog_validation_gps_disabled_title),
                    scanApp.getString(R.string.dialog_validation_gps_disabled_message),
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }

        return true;
    }

    private static void launchActionMessageDialog(final Context context, String title, String message,final String settingsMenu) {
        //Show action result
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(context.getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(settingsMenu);
                context.startActivity(intent);
            }
        });
        dialog.show();
    }
}
