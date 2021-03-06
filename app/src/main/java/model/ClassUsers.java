package model;

public class ClassUsers {
    private String usersImage,usersName, usersId;
    private boolean isAdmin;

    public String getUsersImage() {
        return usersImage;
    }

    public void setUsersImage(String usersImage) {
        this.usersImage = usersImage;
    }

    public String getUsersName() {
        return usersName;
    }

    public void setUsersName(String usersName) {
        this.usersName = usersName;
    }

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public ClassUsers() {
    }
}
