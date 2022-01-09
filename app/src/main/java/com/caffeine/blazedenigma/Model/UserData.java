package com.caffeine.blazedenigma.Model;

public class UserData {

    String serial, uid, discord, email, password, status;

    public UserData() {}

    public UserData(String serial, String uid, String discord, String email, String password, String status) {
        this.serial = serial;
        this.uid = uid;
        this.discord = discord;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public String getSerial() {
        return serial;
    }

    public String getUid() {
        return uid;
    }

    public String getDiscord() {
        return discord;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }
}
