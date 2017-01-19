package org.ereuse.scanner.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.ValidationService;
import org.ereuse.scanner.services.api.ApiResponse;

import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class PlaceMapActivity extends AsyncActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback, LocationListenerActivity {

    private GoogleMap placeMap;
    private PolygonOptions polygonOptions;
    private Polygon polygon;
    private Location location;
    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_map);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.placeMap)).getMapAsync(this);
        setToolbar();

    }

    @Override
    protected void onResume(){
        super.onResume();
        ValidationService.checkInternetConnection(this);
        checkLogin();
        this.getScannerApplication().setCurrentLocationActivity(this);
        this.updateLocationUI(this.getScannerApplication().getLoginActivity().getLocation());

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("mapHelpShown", false)) {
            sharedPreferences.edit().putBoolean("mapHelpShown", true).commit();
            showHelp();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.placeMap = googleMap;
        this.placeMap.setOnMapClickListener(this);
        this.placeMap.setOnMapLongClickListener(this);
        this.placeMap.setOnMarkerClickListener(this);
        this.placeMap.getUiSettings().setZoomControlsEnabled(true);
        updateLocationUI(this.location);
    }

    public void updateLocationUI(Location location) {
        if(location == null) {
            logDebug("PlaceMapActivity", "null location");
        } else {
            this.location = location;
            String locationMessage = "lat: " + this.location.getLatitude()
                    + ", long: " + this.location.getLongitude()
                    + ", alt: " + this.location.getAltitude()
                    + ", acc: " + this.location.getAccuracy();
            logDebug("PlaceMapActivity", locationMessage);
            updateLocationMap();
        }
    }

    private void updateLocationMap(){

        LatLng position = new LatLng(this.location.getLatitude(), this.location.getLongitude());

        MarkerOptions mp = new MarkerOptions();
        mp.position(position);
        mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        mp.title("my position");

        if (this.placeMap != null) {
            if (this.polygonOptions != null) {
                logDebug("updateLocationMap", this.polygonOptions.getPoints().toString());
            }

            if (this.locationMarker != null) {
               this.locationMarker.remove();
            }
            this.locationMarker = this.placeMap.addMarker(mp);
            this.placeMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        }

    }

    @Override
    public void onMapClick(LatLng point) {
        placeMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        MarkerOptions markerOptions = new MarkerOptions().position(point).title(point.toString());
        placeMap.addMarker(markerOptions);

        addToPolygon(point);
    }


    public void addToPolygon(LatLng point) {
        if (polygon != null) {
            polygon.remove();
        }

        if (polygonOptions == null) {
            polygonOptions = new PolygonOptions().add(point);
        } else {
            polygonOptions.add(point);
            polygonOptions.strokeWidth(5);
            polygonOptions.strokeColor(R.color.emphasis_2);
            polygonOptions.fillColor(R.color.emphasis_12);
            polygon = placeMap.addPolygon(polygonOptions);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng markerPoint = marker.getPosition();

        if (this.locationMarker != null) {
            LatLng locationPosition = this.locationMarker.getPosition();
            if (locationPosition.latitude == markerPoint.latitude && locationPosition.longitude == markerPoint.longitude) {
                return true;
            }
        }

        if (polygonOptions != null) {
            List<LatLng> polygonPoints = polygonOptions.getPoints();
            for (LatLng point : polygonPoints) {
                if (point.latitude == markerPoint.latitude && point.longitude == markerPoint.longitude) {
                    polygonPoints.remove(point);
                    marker.remove();
                    if (polygon != null) {
                        polygon.remove();
                    }
                    if (polygonPoints.size() > 0) {
                        polygon = placeMap.addPolygon(polygonOptions);
                        break;
                    }
                }
            }
        }
        return true;
    }

    public void doCreatePlace(View view) {

        String placeName = ((TextView) findViewById(R.id.placeMapText)).getText().toString();
        if(doValidate()) {
            List<LatLng> polygonPoints = this.polygon.getPoints();
            new AsyncService(this).doPlace(this.getServer(), this.getUser(), polygonPoints, placeName);
        }
    }


    @Override
    public void onSuccess(ApiResponse response) {
        super.onSuccess(response);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.place_success));

        Bundle b = getIntent().getExtras();
        if (b != null && b.getBoolean("commingFromNoSuchPlaceError")) {
            dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    FormActivity.checkRelaunchActionFromNewPlace.setChecked(true);
                }
            });


        } else {
            dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        dialog.show();

    }


    public void doHelpPlace(View view){
       showHelp();
    }

    private boolean doValidate() {
        if (!ValidationService.checkInternetConnection(this)) {
            return false;
        }
        String placeName = ((TextView) findViewById(R.id.placeMapText)).getText().toString();
        if (placeName == null || placeName.isEmpty()) {
            launchActionMessageDialog(getString(R.string.dialog_validation_error_title), getString(R.string.place_map_validation_error_placename));
            return false;
        }
        //A polygon is defined as a graph circuit (closed path).
        // So it must contain 4 locations (origin, second location, third location, origin again)
        if (polygon == null || polygon.getPoints().size() < 4) {
            launchActionMessageDialog(getString(R.string.dialog_validation_error_title), getString(R.string.place_map_validation_error_placedefinition));
            return false;
        }
        return true;
    }

    public void showHelp(){
        RelativeLayout mapHelpLayout = (RelativeLayout) findViewById(R.id.MapHelpLayout);
        TextView helpCreate = (TextView) findViewById(R.id.help_map_text_create);
        TextView helpRemove = (TextView) findViewById(R.id.help_map_text_remove);
        helpCreate.setText(this.getText(R.string.place_map_dialog_help_create_body));
        helpRemove.setText(R.string.place_map_dialog_help_remove_body);
        mapHelpLayout.setVisibility(View.VISIBLE);
    }

    public void dissmissHelp(View view) {
        RelativeLayout mapHelpLayout = (RelativeLayout) findViewById(R.id.MapHelpLayout);
        mapHelpLayout.setVisibility(View.GONE);
    }
}
