package eu.ase.ro.dam.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import eu.ase.ro.dam.database.dao.AccountDao;
import eu.ase.ro.dam.database.dao.TransactionDao;
import eu.ase.ro.dam.database.model.Account;
import eu.ase.ro.dam.database.model.Transaction;

@Database(entities = {Account.class, Transaction.class},
          exportSchema = false, version = 1)
public abstract class BankingDatabase extends RoomDatabase {
    public abstract AccountDao getAccountDao();
    public abstract TransactionDao getTransactionDao();
}
