package me.ryanpetschek.gatekeeper;

import java.util.ArrayList;

/**
 * Created by crsch on 4/2/2017.
 */

public class Building {

    private String name, slug, description, imageUrl, address, latitude, longitude;
    public static ArrayList<Building> buildings = new ArrayList<>();

    public Building(String name, String slug, String description, String imageUrl, String address, String latitude, String longitude) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        buildings.add(this);
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
