package eu.ase.ro.dam.util;

public class Bank {
    private String id;
    private String BIC;
    private String name;
    private String country;
    private String type;

    public Bank() {
    }

    public Bank(String BIC, String name, String country, String type) {
        this.BIC = BIC;
        this.name = name;
        this.country = country;
        this.type = type;
    }

    public Bank(String id, String BIC, String name, String country, String type) {
        this.id = id;
        this.BIC = BIC;
        this.name = name;
        this.country = country;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBIC() {
        return BIC;
    }

    public void setBIC(String BIC) {
        this.BIC = BIC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return BIC + ": " +
                name + ", " +
                country + " - " +
                type;
    }
}
