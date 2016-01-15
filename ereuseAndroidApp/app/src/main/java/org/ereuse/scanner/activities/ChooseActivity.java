package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.ereuse.scanner.R;

/**
 * Created by Jamgo SCCL.
 */
public class ChooseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        setToolbar();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkLogin();
    }

    public void doReceive(View view) {
        this.startScanActivity(FormActivity.MODE_RECEIVE);
    }

    public void doRecycle(View view) {
        this.startScanActivity(FormActivity.MODE_RECYCLE);
    }

    public void doLocate(View view) {
        this.startScanActivity(FormActivity.MODE_LOCATE);
    }

    public void doLocatePlace(View view) {
        Intent formIntent = new Intent(this, PlaceMapActivity.class);
        startActivity(formIntent);
    }

    private void startScanActivity(String selectedAction) {
        System.out.println(selectedAction);

        Intent formIntent = new Intent(this, FormActivity.class);
        formIntent.putExtra(FormActivity.EXTRA_MODE, selectedAction);
        startActivity(formIntent);
    }

    public void showEventsList(View view) {
        System.out.println("Show events list");

        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }

}
