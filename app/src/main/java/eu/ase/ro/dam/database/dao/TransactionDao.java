package eu.ase.ro.dam.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import eu.ase.ro.dam.database.model.Transaction;

@Dao
public interface TransactionDao {
    @Insert
    long insert(Transaction transaction);

    @Update
    int update(Transaction transaction);

    @Delete
    int delete(Transaction transaction);

    @Query("select * from transactions")
    List<Transaction> getTransactionList();

    @Query("select * from transactions where owner_iban like (:ownerIban)")
    List<Transaction> findOwnerTransactions(final String ownerIban);

    @Query("select sum(amount) from transactions " +
            "where transaction_type in (:transactionTypes)")
    Double getAmountFromTransactions(List<String> transactionTypes);
}
