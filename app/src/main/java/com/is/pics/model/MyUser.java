package com.is.pics.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by stefano on 14/01/15.
 */
public class MyUser {

    public static final String ROLES[] = {"ROLE_ADMIN","ROLE_USER"};

    private String username;
    private String password;
    private boolean enabled;
    private Set<UserRole> userRole = new HashSet<UserRole>(0);
    private Set<Item> userItems = new HashSet<Item>(0);

    public MyUser() {
    }

    public MyUser(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public MyUser(String username, String password,
                boolean enabled, Set<UserRole> userRole) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.userRole = userRole;
    }

    public MyUser(String username, String password, boolean enabled,
                  Set<UserRole> userRole, Set<Item> userItems) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.userRole = userRole;
        this.userItems = userItems;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<UserRole> getUserRole() {
        return this.userRole;
    }

    public void setUserRole(Set<UserRole> userRole) {
        this.userRole = userRole;
    }

    public Set<Item> getUserItems() {
        return userItems;
    }

    public void setUserItems(Set<Item> userItems) {
        this.userItems = userItems;
    }
}
