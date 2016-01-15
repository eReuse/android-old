package org.ereuse.scanner.data;

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

    public void update(String email, String token, String role, String id) {
        this._id = id;
        this.email = email;
        this.token = token;
        this.role = role;
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