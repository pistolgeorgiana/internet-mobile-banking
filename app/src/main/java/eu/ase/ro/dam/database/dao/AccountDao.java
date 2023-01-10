package eu.ase.ro.dam.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import eu.ase.ro.dam.database.model.Account;

@Dao
public interface AccountDao {
    @Query("select * from accounts")
    List<Account> getAccountList();

    @Insert
    long insertAccount(Account account);

    @Update
    int updateAccount(Account account);

    @Delete
    int deleteAccount(Account account);

    @Query("select * from accounts where name=:ownerName")
    List<Account> fetchAccountsByOwner(String ownerName);
}
