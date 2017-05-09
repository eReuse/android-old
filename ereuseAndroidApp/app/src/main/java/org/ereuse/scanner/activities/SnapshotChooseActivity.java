package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import org.ereuse.scanner.R;
import org.ereuse.scanner.services.api.ApiServicesImpl;

import java.util.ArrayList;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotChooseActivity extends BaseActivity {

    private SubMenu selectDb;
    ArrayList<String> databases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_choose);
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

    public void doAndroidSelfSnapshot(View view) {
        this.startSnapshotActivity(SnapshotActivity.MODE_SELF);
    }

    public void doExternalDeviceSnapshot(View view) {
        this.startSnapshotActivity(SnapshotActivity.MODE_EXTERNAL_DEVICE);

    }

    public void doWorkbenchSnapshot(View view) {
        Intent workbenchIntent = new Intent(this, WorkbenchActivity.class);
        startActivity(workbenchIntent);
    }

    private void startSnapshotActivity(String selectedAction) {
        System.out.println(selectedAction);

        Intent formIntent = new Intent(this, SnapshotActivity.class);
        formIntent.putExtra(SnapshotActivity.EXTRA_MODE, selectedAction);
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
