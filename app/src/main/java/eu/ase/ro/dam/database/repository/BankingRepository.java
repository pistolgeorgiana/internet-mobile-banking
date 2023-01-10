package eu.ase.ro.dam.database.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.dam.database.BankingDatabase;
import eu.ase.ro.dam.database.dao.AccountDao;
import eu.ase.ro.dam.database.dao.TransactionDao;
import eu.ase.ro.dam.database.model.Account;
import eu.ase.ro.dam.database.model.Transaction;

public class BankingRepository {

    private static AccountDao accountDao;
    private static TransactionDao transactionDao;
    private ArrayList<Transaction> transactionList = new ArrayList<>();

    private static final String DB_NAME = "internet_banking_database";
    private static BankingDatabase manager;

    public static BankingDatabase getManager(final Context context) {
        if (manager == null) {
            synchronized (BankingDatabase.class) {
                if (manager == null) {
                    manager = Room.databaseBuilder(
                            context,
                            BankingDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return manager;
    }

    public void insert(final Context context, final Account account) {
        new AsyncTask<Void, Void, Account>() {
            @Override
            protected Account doInBackground(Void... voids) {
                if(account != null) {
                    long id = getManager(context).getAccountDao()
                            .insertAccount(account);
                    if(id != -1) {
                        account.setId(id);
                    }
                }
                return account;
            }
        }.execute();
    }

    public void updateAccount(final Context context, final Account account) {
        new AsyncTask<Account, Void, Integer>() {
            @Override
            protected Integer doInBackground(Account... accounts) {
                if(accounts != null) {
                    return getManager(context).getAccountDao().updateAccount(accounts[0]);
                }
                return null;
            }
        }.execute(account);
    }

    public void deleteAccount(final Context context, final Account account) {
        new AsyncTask<Account, Void, Integer>(){
            @Override
            protected Integer doInBackground(Account... accounts) {
                if(accounts != null){
                    return getManager(context).getAccountDao().deleteAccount(accounts[0]);
                }
                return null;
            }
        }.execute(account);
    }

    public static class GetAccountList extends AsyncTask<Void, Void, List<Account>> {

        public GetAccountList(Context context) {
            accountDao = getManager(context).getAccountDao();
        }

        @Override
        protected List<Account> doInBackground(Void... voids) {
            return accountDao.getAccountList();
        }
    }

    public static class FetchAccountsByOwner extends AsyncTask<String, Void, List<Account>> {
        public FetchAccountsByOwner(Context context) {
            accountDao = getManager(context).getAccountDao();
        }

        @Override
        protected List<Account> doInBackground(String... strings) {
            if(strings != null && strings.length == 1) {
                return accountDao.fetchAccountsByOwner(strings[0]);
            }
            return null;
        }
    }

    public void insertTransaction(final Context context, final Transaction transaction) {
        new AsyncTask<Transaction, Void, Transaction>(){
            @Override
            protected Transaction doInBackground(Transaction... transactions) {
                if(transactions != null) {
                    long id = getManager(context).getTransactionDao().insert(transactions[0]);
                    if(id != -1) {
                        transactions[0].setId(id);
                        return transactions[0];
                    }
                }
                return null;
            }
        }.execute(transaction);
    }

    public void updateTransaction(final Context context, final Transaction transaction) {
        new AsyncTask<Transaction, Void, Integer>() {
            @Override
            protected Integer doInBackground(Transaction... transactions) {
                if(transactions != null) {
                    return getManager(context).getTransactionDao().update(transactions[0]);
                }
                return null;
            }
        }.execute(transaction);
    }

    public void deleteTransaction(final Context context, final Transaction transaction) {
        new AsyncTask<Transaction, Void, Integer>() {
            @Override
            protected Integer doInBackground(Transaction... transactions) {
                if(transactions != null) {
                    return getManager(context).getTransactionDao().delete(transactions[0]);
                }
                return null;
            }
        }.execute(transaction);
    }

    public static class GetTransactionList extends AsyncTask<Void, Void, List<Transaction>> {
        public GetTransactionList(Context context) {
            transactionDao = getManager(context).getTransactionDao();
        }

        @Override
        protected List<Transaction> doInBackground(Void... voids) {
            return transactionDao.getTransactionList();
        }
    }

    public List<Transaction> getTransactionsList(final Context context) {
        new AsyncTask<Void, Void, List<Transaction>>() {
            @Override
            protected List<Transaction> doInBackground(Void... voids) {
                return getManager(context).getTransactionDao().getTransactionList();
            }

            @Override
            protected void onPostExecute(List<Transaction> result) {
                if(result != null) {
                    transactionList.addAll(result);
                } else {
                    transactionList.clear();
                }
            }
        }.execute();
        return transactionList;
    }

    public static class GetTransactionByOwnerIban extends AsyncTask<String, Void, List<Transaction>> {
        public GetTransactionByOwnerIban(Context context) {
            transactionDao = getManager(context).getTransactionDao();
        }

        @Override
        protected List<Transaction> doInBackground(String... strings) {
            if(strings != null && strings.length == 1) {
                return transactionDao.findOwnerTransactions(strings[0]);
            }
            return null;
        }
    }

    public static class GetAmountByType extends AsyncTask<List<String>, Void, List<Double>> {
        public GetAmountByType(Context context) {
            transactionDao = getManager(context).getTransactionDao();
        }

        @Override
        protected List<Double> doInBackground(List<String>... lists) {
            List<Double> result = new ArrayList<>();
            if(lists != null && lists.length == 2) {
                for(int i=0; i<lists.length; i++) {
                    result.add(transactionDao.getAmountFromTransactions(lists[i]));
                }
                return result;
            }
            return null;
        }
    }
}

