package application;

public class User {
    private Integer userID;
    private String name;
    private String address;
    private String mobile;
    private String email;
    private String userLastName;
    private String password; // Assuming you have a password field
    private String role;

    public User(Integer userID, String name, String address, String mobile, String email, String userLastName, String password, String role) {
        this.userID = userID;
        this.name = name;
        this.address = address;
        this.mobile = mobile;
        this.email = email;
        this.userLastName = userLastName;
        this.password = password; // Store a hashed password, not the plain text!
        this.role = role;
    }

    // Getters
    public Integer getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String userLastName) {
        this.userLastName = userLastName;
    }

    public void setPassword(String password) {
        this.password = password; // Remember to hash the password before setting it
    }

    public void setRole(String role) {
        this.role = role;
    }
}
