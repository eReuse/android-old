package org.ereuse.scanner.data;

import java.util.Map;

/**
 * Created by Jamgo SCCL.
 */
public class User {

    public static final String EMPLOYEE = "employee";
    public static final String SUPERUSER = "superuser";
    public static final String ADMIN = "admin";

    public String _id;
    public String email;
    public String token;
    public String role;
    public String password;
    public Map<String, String> databases;
    public String defaultDatabase;

    public Map<String, String> getDatabases() {
        return databases;
    }

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public void update(String email, String token, String role, String id, Map<String, String> databases, String defaultDatabase) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public boolean isEqualOrGreaterThanEmployee() {
        return true; // todo redo all employee idea
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
