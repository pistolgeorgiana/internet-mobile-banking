package eu.ase.ro.dam.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts",
        indices = {@Index(value = "iban", unique = true)})
public class Account implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "iban")
    private String iban;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "currency")
    private String currency;
    @ColumnInfo(name = "amount")
    private Double amount;

    public Account(long id, String iban, String name, String currency, Double amount) {
        this.id = id;
        this.iban = iban;
        this.name = name;
        this.currency = currency;
        this.amount = amount;
    }

    @Ignore
    public Account(String iban, String name, String currency, double amount) {
        this.iban = iban;
        this.name = name;
        this.currency = currency;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "iban='" + iban + '\'' +
                ", name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iban);
        dest.writeString(this.name);
        dest.writeString(this.currency);
        dest.writeDouble(this.amount);
    }

    private Account(Parcel in) {
        this.iban = in.readString();
        this.name = in.readString();
        this.currency = in.readString();
        this.amount = in.readDouble();
    }

    public static Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[0];
        }
    };
}
