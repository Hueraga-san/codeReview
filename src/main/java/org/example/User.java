package org.example;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String password;
    public User(String userName, String password){
        this.password = password;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
