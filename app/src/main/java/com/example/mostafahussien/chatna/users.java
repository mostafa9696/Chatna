package com.example.mostafahussien.chatna;

import android.os.Parcel;
import android.os.Parcelable;


public class users implements Parcelable {
    private String name;
    private String status;
    private String image;
    private String gender;
    private String thumb_image;
    public users(String name, String status, String image, String gender) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.gender = gender;
    }

    protected users(Parcel in) {
        name = in.readString();
        status = in.readString();
        image = in.readString();
        gender = in.readString();
        thumb_image = in.readString();
    }

    public static final Creator<users> CREATOR = new Creator<users>() {
        @Override
        public users createFromParcel(Parcel in) {
            return new users(in);
        }

        @Override
        public users[] newArray(int size) {
            return new users[size];
        }
    };

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(status);
        parcel.writeString(image);
        parcel.writeString(gender);
        parcel.writeString(thumb_image);
    }
}
