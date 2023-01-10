package eu.ase.ro.dam.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.ase.ro.dam.AddAccountActivity;
import eu.ase.ro.dam.AddTransactionActivity;
import eu.ase.ro.dam.MainActivity;
import eu.ase.ro.dam.R;
import eu.ase.ro.dam.database.repository.BankingRepository;
import eu.ase.ro.dam.network.HttpRequest;
import eu.ase.ro.dam.network.Item;
import eu.ase.ro.dam.database.model.Account;
import eu.ase.ro.dam.network.JsonParser;
import eu.ase.ro.dam.util.AccountAdapter;
import eu.ase.ro.dam.database.model.Transaction;
import eu.ase.ro.dam.util.TransactionsAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountsFragment extends Fragment {
    public final static String URL = "https://api.myjson.com/bins/18hkl2";
    public final static String ACCOUNTS_KEY = "accountsKey";
    public final static String TRANSACTION_TYPE_KEY = "transactionTypeKey";
    public final static String OWNER_IBAN_KEY = "ownerIbanKey";
    public final static String SEND_TRANSACTION_KEY = "sendTransactionKey";
    private static final String FILE_NAME = "mytransactions.csv";
    int selectedPostion;
    FloatingActionButton floatingActionButton;
    ListView lvAccounts;
    ListView lvTransactions;
    ArrayList<Account> accounts = new ArrayList<>();
    List<Transaction> transactions = new ArrayList<>();
    Map<String, List<Item>> bankClients = new HashMap<String, List<Item>>();

    public AccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        initComponents(view);
        loadAccountsFromDatabase();
//        new HttpRequest(){
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                bankClients = JsonParser.parseJson(s);
//                if(bankClients != null) {
//                    initBankAccounts(bankClients.values());
//                }
//            }
//        }.execute(URL);
        return view;
    }

    private void loadAccountsFromDatabase() {
        new BankingRepository.GetAccountList(getContext()){
            @Override
            protected void onPostExecute(List<Account> result) {
                if(result != null) {
                    accounts.clear();
                    accounts.addAll(result);
                    notifyAccount();
                }
            }
        }.execute();
    }

    private void initBankAccounts(Collection<List<Item>> existingAccounts) {
        if(existingAccounts != null) {
            for (List<Item> items : existingAccounts) {
                for (int i = 0; i < items.size(); i++) {
                    Account account = items.get(i).getCurrentAccounts();
                    if(!accounts.contains(account)) {
                        addAccountIntoDatabase(account);
                    }
                }
            }
        }
    }

    public void notifyAccount() {
        AccountAdapter adapter = (AccountAdapter) lvAccounts.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void initComponents(View view) {
        lvAccounts = view.findViewById(R.id.accounts_lv_current_account);
        registerForContextMenu(lvAccounts);
        if (getArguments() != null) {
            accounts = getArguments().getParcelableArrayList(ACCOUNTS_KEY);
        }
        if (getContext() != null) {
            AccountAdapter adapter = new AccountAdapter(getContext(),
                    R.layout.lv_account_view, accounts, getLayoutInflater());
            lvAccounts.setAdapter(adapter);
            lvAccounts.setOnItemClickListener(lvItemClicked());
        }

        floatingActionButton = view.findViewById(R.id.account_fab_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddAccountActivity.class);
                startActivityForResult(intent, AddAccountActivity.REQ_CODE_ADD_ACCOUNT);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.accounts_lv_current_account) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(accounts.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.contex_menu_items);
            for(int i=0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Account listItem = accounts.get(info.position);
        if (menuItemIndex == 0) {
            showTransactions(listItem);
            transactions.clear();
        } else if (menuItemIndex == 1) {
            new BankingRepository().deleteAccount(getContext(), listItem);
            accounts.remove(listItem);
            notifyAccount();
        }

        return true;
    }

    private void showTransactions(Account listItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.alert_dialog_export_to_csv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exportToCsv();
                    }
                });
        View alertDialogView = getLayoutInflater().inflate(
                R.layout.my_transactions_statistics, null);

        initializeAdapter(alertDialogView);

        builder.setView(alertDialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        findTransactions(listItem);
    }

    private void exportToCsv() {
        if (transactions != null) {
            try {
                FileOutputStream stream = getContext().openFileOutput(
                        FILE_NAME, Context.MODE_PRIVATE);
                StringBuilder fileText = new StringBuilder("OwnerIban,TransactionType," +
                        "BeneficiaryIban,NamedBeneficiary,Amount");
                for(Transaction t:transactions) {
                    fileText.append("\n" + t.getOwnerIban() + ','
                            + t.getTransactionType() + ','
                            + t.getBeneficiaryIban() + ','
                            + t.getNamedBeneficiary() + ','
                            + t.getAmount().toString());
                }
                stream.write(fileText.toString().getBytes());
                stream.close();
                Toast.makeText(getContext(), "Exported to ", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void findTransactions(Account listItem) {
        new BankingRepository.GetTransactionByOwnerIban(getContext()) {
            @Override
            protected void onPostExecute(List<Transaction> result) {
                if(result != null) {
                    for(Transaction t : result) {
                        transactions.add(t);
                        notifyTransaction();
                    }
                }
            }
        }.execute(listItem.getIban());
    }

    private void initializeAdapter(View alertDialogView) {
        lvTransactions = alertDialogView.findViewById
                (R.id.list_view_my_transactions);
        if(getContext() != null) {
            TransactionsAdapter adapter = new TransactionsAdapter(
                    getContext().getApplicationContext(),
                    R.layout.my_transactions_statistics,
                    transactions,
                    getLayoutInflater());
            lvTransactions.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void notifyTransaction() {
        TransactionsAdapter adapter = (TransactionsAdapter) lvTransactions.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            accounts = savedInstanceState.getParcelableArrayList(ACCOUNTS_KEY);
        }
    }

    private AdapterView.OnItemClickListener lvItemClicked() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String[] operations = getResources()
                        .getStringArray(R.array.pick_new_operation);
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.alert_dialog_operation)
                        .setSingleChoiceItems(operations,
                                -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getContext(),
                                                AddTransactionActivity.class);
                                        selectedPostion = position;
                                        intent.putExtra(TRANSACTION_TYPE_KEY, operations[which]);
                                        intent.putExtra(OWNER_IBAN_KEY, accounts.get(position));
                                        dialog.dismiss();
                                        startActivityForResult(intent,
                                                AddTransactionActivity.REQ_CODE_ADD_TRANSACTION);
                                    }
                                })
                        .setNeutralButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                        .create().show();
            }
        };
    }

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AddAccountActivity.REQ_CODE_ADD_ACCOUNT
                && resultCode == RESULT_OK && data != null) {
            Account account = data.getParcelableExtra(AddAccountActivity.ADD_ACCOUNT_KEY);
            if(account != null) {
                addAccountIntoDatabase(account);
            }
        } else if (requestCode == AddTransactionActivity.REQ_CODE_ADD_TRANSACTION
                && resultCode == RESULT_OK && data != null) {
            Account modified = data.getParcelableExtra(AddTransactionActivity.UPDATE_ACCOUNT_KEY);
            if(modified != null) {
                updateAccountInDatabase(modified);
            }
            Transaction transaction = data.getParcelableExtra
                    (AddTransactionActivity.ADD_TRANSACTION_KEY);
            Bundle bundle = new Bundle();
            bundle.putParcelable(SEND_TRANSACTION_KEY, transaction);
            MainActivity.fragment2.setArguments(bundle);
            MainActivity.fragment2.addTransaction();
        }
    }

    private void updateAccountInDatabase(Account account) {
        account.setId(accounts.get(selectedPostion).getId());
        new BankingRepository().updateAccount(getContext(), account);
        accounts.set(selectedPostion, account);
        notifyAccount();
    }

    private void addAccountIntoDatabase(Account account) {
        new BankingRepository().insert(getContext(), account);
        accounts.add(account);
        notifyAccount();
    }
}
