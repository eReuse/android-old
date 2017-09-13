package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import org.ereuse.scanner.R;

/**
 * Created by Jamgo SCCL.
 */
public class ChooseActivity extends ActivityWithDbMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    public void showDeviceEventTypes(View v) {
        Intent intent = new Intent(this, EventsChooseActivity.class);
        startActivity(intent);
    }

    public void doLocatePlace(View view) {
        Intent formIntent = new Intent(this, PlaceMapActivity.class);
        startActivity(formIntent);
    }

    public void showEventsList(View view) {
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }

    public void showSnapshotTypes(View view) {
        Intent intent = new Intent(this, SnapshotChooseActivity.class);
        startActivity(intent);
    }

}
