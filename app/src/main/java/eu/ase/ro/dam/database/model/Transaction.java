package eu.ase.ro.dam.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "transactions",
        indices = {@Index("owner_iban")},
        foreignKeys = @ForeignKey(entity = Account.class,
                                  parentColumns = "iban",
                                  childColumns = "owner_iban",
                                  onDelete = CASCADE))
public class Transaction implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "owner_iban")
    private String ownerIban;
    @ColumnInfo(name = "transaction_type")
    private String transactionType;
    @ColumnInfo(name = "beneficiary_iban")
    private String beneficiaryIban;
    @ColumnInfo(name = "named_beneficiary")
    private String namedBeneficiary;
    @ColumnInfo(name = "amount")
    private Double amount;

    public Transaction(long id, String ownerIban, String transactionType,
                       String beneficiaryIban, String namedBeneficiary, Double amount) {
        this.id = id;
        this.ownerIban = ownerIban;
        this.transactionType = transactionType;
        this.beneficiaryIban = beneficiaryIban;
        this.namedBeneficiary = namedBeneficiary;
        this.amount = amount;
    }

    @Ignore
    public Transaction(String ownerIban, String transactionType, String beneficiaryIban,
                       String namedBeneficiary, Double amount) {
        this.ownerIban = ownerIban;
        this.transactionType = transactionType;
        this.beneficiaryIban = beneficiaryIban;
        this.namedBeneficiary = namedBeneficiary;
        this.amount = amount;
    }

    private Transaction(Parcel source) {
        this.ownerIban = source.readString();
        this.transactionType = source.readString();
        this.beneficiaryIban = source.readString();
        this.namedBeneficiary = source.readString();
        this.amount = source.readDouble();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwnerIban() {
        return ownerIban;
    }

    public void setOwnerIban(String ownerIban) {
        this.ownerIban = ownerIban;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getBeneficiaryIban() {
        return beneficiaryIban;
    }

    public void setBeneficiaryIban(String beneficiaryIban) {
        this.beneficiaryIban = beneficiaryIban;
    }

    public String getNamedBeneficiary() {
        return namedBeneficiary;
    }

    public void setNamedBeneficiary(String namedBeneficiary) {
        this.namedBeneficiary = namedBeneficiary;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "ownerIban='" + ownerIban + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", beneficiaryIban='" + beneficiaryIban + '\'' +
                ", namedBeneficiary='" + namedBeneficiary + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerIban);
        dest.writeString(transactionType);
        dest.writeString(beneficiaryIban);
        dest.writeString(namedBeneficiary);
        dest.writeDouble(amount);
    }

    public static Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[0];
        }
    };
}
