package com.is.pics.model;

public class UserRole {

    private Integer userRoleId;
    private MyUser user;
    private String role;

    public UserRole() {
    }

    public UserRole(MyUser user, String role) {
        this.user = user;
        this.role = role;
    }


    public Integer getUserRoleId() {
        return this.userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public MyUser getUser() {
        return this.user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}