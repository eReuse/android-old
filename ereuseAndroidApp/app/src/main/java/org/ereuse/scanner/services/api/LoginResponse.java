package org.ereuse.scanner.services.api;

import java.util.ArrayList;

/**
 * Created by Jamgo SCCL.
 */
public class LoginResponse implements ApiResponse {
    public String _id;
    public String email;
    public String token;
    public String role;
    public String password;
    public String defaultDatabase;
    public ArrayList<String> databases;

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public void setDefaultDatabase(String defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }

    public ArrayList<String> getDatabases() {
        return databases;
    }

    public void setDatabases(ArrayList<String> databases) {
        this.databases = databases;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
