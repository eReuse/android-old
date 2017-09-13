package org.ereuse.scanner.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import org.ereuse.scanner.R;
import org.ereuse.scanner.services.api.ApiServicesImpl;

import java.util.Map;

public abstract class ActivityWithDbMenu extends BaseActivity {
    Map<String, String> databases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        databases = getScannerApplication().getUser().getDatabases();
        setToolbar();
    }

    abstract protected int getLayoutId();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_db_menu, menu);
        MenuItem item = menu.findItem(R.id.action_select_db);
        SubMenu selectDb = item.getSubMenu();
        SetDatabase setDatabase = new SetDatabase();
        for (Map.Entry<String, String> database : databases.entrySet()) {
            // Access or admin
            if (database.getValue().equals("ac") || database.getValue().equals("ad")) {
                selectDb.add(database.getKey()).setOnMenuItemClickListener(setDatabase);
            }
        }
        return true;
    }

    private class SetDatabase implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ApiServicesImpl.setDb(item.getTitleCondensed().toString());
            return true;
        }
    }
}
