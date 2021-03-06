package util;

import android.app.Application;

public class UsersApi extends Application {
    private  String userName,classId,className;
    private String tokenId;
    private boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    private  String userId;
    private  String email;
    private static UsersApi instance;
    public  static UsersApi getInstance(){
        if(instance==null)
            instance = new UsersApi();
        return instance;

    }

    public UsersApi(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
