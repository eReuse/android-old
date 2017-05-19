package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.api.ApiServicesImpl;
import org.ereuse.scanner.services.api.GenericEventType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class EventsChooseActivity extends BaseActivity {

    private SubMenu selectDb;
    ArrayList<String> databases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_choose);
        databases = getScannerApplication().getUser().getDatabases();
        addGenericEvents();
        setToolbar();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkLogin();
    }

    private void addGenericEvents() {

//        List<String> genericEvents = Arrays.asList("Ready");

        LinearLayout genericEventButtonslayout = (LinearLayout) findViewById(R.id.genericEventButtons);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(0, 0 ,0, getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));

        for (final String eachGenericEvent : GenericEventType.GENERIC_EVENT_LABEL_MAP.keySet()) {
            String buttonText = GenericEventType.GENERIC_EVENT_LABEL_MAP.get(eachGenericEvent);
            Button genericEventButton = new Button(this);
            genericEventButton.setText(buttonText);
            genericEventButton.setLayoutParams(buttonLayoutParams);
            genericEventButton.setBackgroundColor(getResources().getColor(R.color.emphasis_2));
            genericEventButton.setTextColor(getResources().getColor(R.color.white));
            genericEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGeneric(eachGenericEvent);
                }
            });
            genericEventButtonslayout.addView(genericEventButton);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_db_menu, menu);
        MenuItem item = menu.findItem(R.id.action_select_db);
        selectDb = item.getSubMenu();
        SetDatabase setDatabase = new SetDatabase();
        for(String database : databases)
            selectDb.add(database).setOnMenuItemClickListener(setDatabase);
        return true;
    }

    public void doGeneric(String genericEventType) { this.startGenericScanActivity(genericEventType); }

    public void doReceive(View view) {
        this.startScanActivity(FormActivity.MODE_RECEIVE);
    }

    public void doRecycle(View view) {
        this.startScanActivity(FormActivity.MODE_RECYCLE);
    }

    public void doLocate(View view) {
        this.startScanActivity(FormActivity.MODE_LOCATE);
    }


    public void doSnapshot(View view) {
        Intent formIntent = new Intent(this, SnapshotChooseActivity.class);
        startActivity(formIntent);
    }

    private void startGenericScanActivity(String selectedAction) {
        System.out.println(selectedAction);

        Intent formIntent = new Intent(this, FormGenericActivity.class);
        formIntent.putExtra(FormActivity.EXTRA_MODE, selectedAction);
        startActivity(formIntent);
    }

    private void startScanActivity(String selectedAction) {
        System.out.println(selectedAction);

        Intent formIntent = new Intent(this, FormActivity.class);
        formIntent.putExtra(FormActivity.EXTRA_MODE, selectedAction);
        startActivity(formIntent);
    }


    class SetDatabase implements MenuItem.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ApiServicesImpl.setDb(item.getTitleCondensed().toString());
            return true;
        }
    }

}
