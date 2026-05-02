package com.motel.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class LoginResponse {
    private boolean success;
    private String username;
    private String fullName;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String username, String fullName, String message) {
        this.success = success;
        this.username = username;
        this.fullName = fullName;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
