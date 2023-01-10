package eu.ase.ro.dam.util;

import com.google.android.gms.maps.model.LatLng;

public class BankLocation {
    private LatLng coordinates;
    private String country;
    private String address;

    public BankLocation(LatLng coordinates, String country, String address) {
        this.coordinates = coordinates;
        this.country = country;
        this.address = address;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
