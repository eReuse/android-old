package org.ereuse.scanner.data;

import java.util.ArrayList;

/**
 * Created by Jamgo SCCL.
 */
public class User {

    private static final String EMPLOYEE = "employee";

    public String _id;
    public String email;
    public String token;
    public String role;
    public String password;

    public ArrayList<String> getDatabases() {
        return databases;
    }

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public ArrayList<String> databases;
    public String defaultDatabase;

    public void update(String email, String token, String role, String id, ArrayList<String> databases, String defaultDatabase) {
        this._id = id;
        this.email = email;
        this.token = token;
        this.role = role;
        this.databases = databases;
        this.defaultDatabase = defaultDatabase;
    }

    public String get_id() {
        return _id;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public boolean isEmployee() {
        return this.role.equals(EMPLOYEE);
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return this.password;
    }

}