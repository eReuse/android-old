package org.ereuse.scanner.activities;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.ValidationService;
import org.ereuse.scanner.services.api.ApiException;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.LoginResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamgo SCCL.
 */
public class LoginActivity extends AsyncActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Spinner spinner = (Spinner) findViewById(R.id.serversSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.login_servers, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);

        //If login succeeded and different user from the stored one, then update the user information
        String userEmailPreference = sharedPreferences.getString("user","");
        String userPasswordPreference = sharedPreferences.getString("password","");

        EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        emailEditText.setText(userEmailPreference);
        passwordEditText.setText(userPasswordPreference);

        //checkAllPermissions();
        setToolbar();
        this.getScannerApplication().setLoginActivity(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        checkAllPermissions();
        ValidationService.checkInternetConnection(this);

/*        if (Build.VERSION.SDK_INT < 23) {
            if (this.getScannerApplication().getGoogleApiClient().isConnected() && !this.getScannerApplication().isRequestingLocationUpdates()) {
                this.startLocationUpdates();
            }
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    public void doLogin(View view) {
        if (ValidationService.checkInternetConnection(this)) {
            EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
            EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

            String server = getServer();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            //Save login information into context
            User user = new User();
            user.setPassword(password);
            user.setEmail(email);
            getScannerApplication().setUser(user);
            logDebug("LoginActivity","server = " + server + ", email = " + email + ", password = " + password);
            // TODO Validate mandatory fields

            // do login
            checkAllPermissions();
            AsyncService asyncService = new AsyncService(this);
            asyncService.doLogin(email, password, server);
        }
    }

    public void doNoLoginWorkbench(View view) {
        Intent workbenchIntent = new Intent(this, WorkbenchActivity.class);
        startActivity(workbenchIntent);
    }


    @Override
    public String getServer() {
        Spinner serverSpinner = (Spinner) findViewById(R.id.serversSpinner);
        String server = serverSpinner.getSelectedItem().toString();
        return server;
    }

    @Override
    public List<Integer> getMainLayoutFields() {
        List<Integer> fieldIds = new ArrayList<Integer>();
        fieldIds.add(R.id.serversSpinner);
        fieldIds.add(R.id.emailEditText);
        fieldIds.add(R.id.passwordEditText);
        fieldIds.add(R.id.loginButton);
        return fieldIds;
    }

    @Override
    public void onSuccess(ApiResponse response) {
        super.onSuccess(response);

        LoginResponse loginResponse = (LoginResponse) response;
        User user = getScannerApplication().getUser();
        user.update(loginResponse.getEmail(), loginResponse.getToken(), loginResponse.getRole(), loginResponse.get_id(), loginResponse.getDatabases(), loginResponse.getDefaultDatabase());
        this.getScannerApplication().setServer(this.getServer());
        this.getScannerApplication().setUser(user);

        // Go to next activity
        Intent chooseIntent = new Intent(this, ChooseActivity.class);
        startActivity(chooseIntent);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);

        //If login succeeded and different user from the stored one, then update the user information
        String userEmailPreference = sharedPreferences.getString("user", "");
        if(!user.getEmail().equals(userEmailPreference)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user",user.getEmail());
            editor.putString("password", user.getPassword());
            editor.commit();
        }
    }

    @Override
    public void onError(ApiException exception) {
        super.onError(exception);

        this.getScannerApplication().setServer(null);
        this.getScannerApplication().setUser(null);
    }
}
