package com.teh.fakelocationimage.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Id")
    @Expose
    private int id;
    @SerializedName("Link image")
    @Expose
    private String imageLink;
    @SerializedName("Location")
    @Expose
    private String location;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Nation")
    @Expose
    private String nation;

    public String getCity() {
        return city;
    }

    public int getId() {
        return id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getNation() {
        return nation;
    }

    @Override
    public String toString() {
        return "Location{" +
                "city='" + city + '\'' +
                ", id=" + id +
                ", imageLink='" + imageLink + '\'' +
                ", location='" + location + '\'' +
                ", name='" + name + '\'' +
                ", nation='" + nation + '\'' +
                '}';
    }
}
