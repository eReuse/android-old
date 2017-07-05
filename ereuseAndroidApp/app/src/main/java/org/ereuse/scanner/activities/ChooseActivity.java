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
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class ChooseActivity extends BaseActivity {

    private SubMenu selectDb;
    List<String> databases;

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

    class SetDatabase implements MenuItem.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ApiServicesImpl.setDb(item.getTitleCondensed().toString());
            return true;
        }
    }

}
