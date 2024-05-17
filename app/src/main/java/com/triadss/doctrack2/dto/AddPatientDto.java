package com.triadss.doctrack2.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class AddPatientDto implements Parcelable {
    public AddPatientDto(){
        //Required empty constructor
    }

    private String idNumber;

    private String email;

    private String fullName;

    private String address;

    private String phone;

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getYear() {
        return Year;
    }

    public void setYear(Integer year) {
        Year = year;
    }

    private Integer Year;
    private String course;
    private Timestamp dateOfBirth;
    private String gender;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public Timestamp getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Timestamp dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public AddPatientDto(Parcel in) {
        // Read fields from Parcel
        idNumber = in.readString();
        fullName = in.readString();
        email = in.readString();
        address = in.readString();
        phone = in.readString();
        course = in.readString();
    }

    public static final Creator<AddPatientDto> CREATOR = new Creator<AddPatientDto>() {
        @Override
        public AddPatientDto createFromParcel(Parcel in) {
            return new AddPatientDto(in);
        }

        @Override
        public AddPatientDto[] newArray(int size) {
            return new AddPatientDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write fields to Parcel
        dest.writeString(idNumber);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(course);
    }
}
