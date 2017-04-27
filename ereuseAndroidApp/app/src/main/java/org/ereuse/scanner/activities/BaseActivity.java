package org.ereuse.scanner.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.ereuse.scanner.R;
import org.ereuse.scanner.ScannerApplication;
import org.ereuse.scanner.data.User;

/**
 * Created by Jamgo SCCL.
 */
public class BaseActivity extends AppCompatActivity {

    protected static final String SHARED_PREFERENCES_NAME = "ereuse.properties";

    public ScannerApplication getScannerApplication() {
        return (ScannerApplication) this.getApplication();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        ScannerApplication application = this.getScannerApplication();
        application.incrementCurrentActivities();
    }


    @Override
    protected void onPause() {
        super.onPause();
//    @Override
//    public void onUserLeaveHint() {
        ScannerApplication application = this.getScannerApplication();
        application.decrementCurrentActivities();
        this.getScannerApplication().setCurrentLocationActivity(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                return true;
            case R.id.action_logout:
                showLogoutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void setToolbar() {
        Toolbar ereuseToolbar = (Toolbar) findViewById(R.id.ereuse_toolbar);
        //ereuseToolbar.setLogo(R.drawable.header);
        ereuseToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(ereuseToolbar);
    }

    public void logDebug(String tag, String message) {
        this.getScannerApplication().logDebug(tag, message);
    }

    public void showAboutDialog(){

        final TextView message = new TextView(this);
        final SpannableString spannableString = new SpannableString(getString(R.string.dialog_about_body));
        Linkify.addLinks(spannableString, Linkify.ALL);
        message.setText(spannableString);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.about);
        dialog.setTitle(getString(R.string.dialog_about_title));
        dialog.setMessage(spannableString);
        //dialog.setView(message);
        dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void showLogoutDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.logout);
        dialog.setTitle("Logout");
        dialog.setMessage("Are you shure you want to Logout?");
        dialog.setPositiveButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                doLogout();
            }
        });
        dialog.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showPermissionDeniedDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.logout);
        dialog.setTitle("eReuse permission");
        dialog.setMessage("This permission is necessary for a correct application behaviour, please enable it via device settings before try to use it");
        dialog.setPositiveButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.show();
    }

    private void doLogout(){
        User user = this.getScannerApplication().getUser();
        if (user != null) {
            user.update(user.getEmail(), null, user.getRole(), user.get_id(), user.getDatabases(), user.getDefaultDatabase());
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    protected void checkLogin()
    {
        User user = this.getScannerApplication().getUser();
        if (user == null || user.getToken() == null ) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    protected void launchActionMessageDialog(String title, String message) {
        launchActionMessageDialog(title, message, false);
    }

    protected void launchActionMessageDialog(String title, String message, boolean useDialogCallback) {
        //Show action result
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (title != null) {
            dialog.setTitle(title);
        }
        if(useDialogCallback) {
            dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCallback();
                }
            });
        } else {
            dialog.setNeutralButton(getString(R.string.dialog_ack), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog.setMessage(message);
        dialog.show();
    }

    protected void launchActionMessageDialog(String message) {
        launchActionMessageDialog(null, message, false);
    }

    protected void launchActionMessageDialog(String message, boolean useDialogCallback) {
        launchActionMessageDialog(null, message, true);
    }

    protected void dialogCallback() {

    }
}
