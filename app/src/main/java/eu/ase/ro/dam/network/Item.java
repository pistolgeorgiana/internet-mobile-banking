package eu.ase.ro.dam.network;


import eu.ase.ro.dam.database.model.Account;

public class Item {
    private String companyAccount;
    private String city;
    private Account currentAccounts;

    public Item(String company, String city, Account currentAccounts) {
        this.companyAccount = company;
        this.city = city;
        this.currentAccounts = currentAccounts;
    }

    public String getCompanyAccount() {
        return companyAccount;
    }

    public void setCompanyAccount(String company) {
        this.companyAccount = company;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Account getCurrentAccounts() {
        return currentAccounts;
    }

    public void setCurrentAccounts(Account currentAccounts) {
        this.currentAccounts = currentAccounts;
    }

    @Override
    public String toString() {
        return "Item{" +
                "company='" + companyAccount + '\'' +
                ", currentAccounts=" + currentAccounts +
                '}';
    }
}
