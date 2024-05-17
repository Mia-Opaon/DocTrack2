package com.triadss.doctrack2.dto;

public class HealthProfDto {
    private String fullName;
    private String position;
    private String userName;
    private String password;
    private String healthProfId;
    private String gender;
    private String email;

    public HealthProfDto() {

    }

    public HealthProfDto(String fullName, String position, String userName, String email, String password,
            String gender) {
        this.fullName = fullName;
        this.position = position;
        this.userName = userName;
        this.password = password;
        this.gender = gender;
        this.email = email;
    }

    public void setHealthProfid(String healthProfId) {
        this.healthProfId = healthProfId;
    }
    public void setFullName(String fullName) { this.fullName = fullName;}
    public void setPosition(String position) {  this.position = position;}
    public void setUserName(String userName) { this.userName = userName;}
    public void setPassword(String password) { this.password = password;}
    public void setGender(String gender) { this.gender = gender;}
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getHealthProfid() {
        return healthProfId;
    }

    public String getPosition() {
        return position;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }



}
