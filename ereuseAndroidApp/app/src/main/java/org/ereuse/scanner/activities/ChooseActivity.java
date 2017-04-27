package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.api.ApiServicesImpl;

import java.util.ArrayList;

/**
 * Created by Jamgo SCCL.
 */
public class ChooseActivity extends BaseActivity {

    private SubMenu selectDb;
    ArrayList<String> databases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        databases = getScannerApplication().getUser().getDatabases();
        setToolbar();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkLogin();
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

    public void doSnapshot(View view) {
        Intent formIntent = new Intent(this, SnapshotChooseActivity.class);
        startActivity(formIntent);
    }

    private void startScanActivity(String selectedAction) {
        System.out.println(selectedAction);

        Intent formIntent = new Intent(this, FormActivity.class);
        formIntent.putExtra(FormActivity.EXTRA_MODE, selectedAction);
        startActivity(formIntent);
    }

    public void showEventsList(View view) {
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }

    public void doRemoveComponent(View view) {
        Intent formIntent = new Intent(this, SnapshotRemoveComponentActivity.class);
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
