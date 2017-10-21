package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import org.ereuse.scanner.R;

/**
 * Created by Jamgo SCCL.
 */
public class SnapshotChooseActivity extends ActivityWithDbMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_snapshot_choose;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
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

    public void doRemoveComponent(View view) {
        Intent formIntent = new Intent(this, SnapshotRemoveComponentActivity.class);
        startActivity(formIntent);
    }

}
