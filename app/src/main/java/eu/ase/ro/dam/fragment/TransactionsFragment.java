package eu.ase.ro.dam.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.dam.R;
import eu.ase.ro.dam.SearchAccountActivity;
import eu.ase.ro.dam.StatisticsActivity;
import eu.ase.ro.dam.database.model.Transaction;
import eu.ase.ro.dam.database.repository.BankingRepository;
import eu.ase.ro.dam.util.TransactionsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends Fragment {
    public final static String TRANSACTIONS_KEY = "transactionsKey";
    Button statistics;
    ListView lvTransactions;
    FloatingActionButton fabSearch;
    ArrayList<Transaction> transactions = new ArrayList<>();

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        initComponents(view);
        getDataFromDatabase();
        return view;
    }

    private void getDataFromDatabase() {
        new BankingRepository.GetTransactionList(getContext()){
            @Override
            protected void onPostExecute(List<Transaction> result) {
                if(result != null && transactions.isEmpty()) {
                    transactions.addAll(result);
                    notifyTransaction();
                }
            }
        }.execute();
    }

    private void initComponents(View view) {
        fabSearch = view.findViewById(R.id.transactions_fab_search);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchAccountActivity.class);
                startActivity(intent);
            }
        });

        statistics = view.findViewById(R.id.transactions_btn_show_statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StatisticsActivity.class);
                startActivity(intent);
            }
        });

        lvTransactions = view.findViewById(R.id.transactions_lv_operations);
        lvTransactions.setOnItemClickListener(changeOrDelete());
        if (getArguments() != null) {
            transactions = getArguments().getParcelableArrayList(TRANSACTIONS_KEY);
        }
        if (getContext() != null) {
            TransactionsAdapter adapter =
                    new TransactionsAdapter(
                            getContext().getApplicationContext(),
                            R.layout.lv_transaction_view,
                            transactions,
                            getLayoutInflater());
            lvTransactions.setAdapter(adapter);
        }
    }

    private AdapterView.OnItemClickListener changeOrDelete() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.transaction_click_option))
                        .setNegativeButton(getString(R.string.transaction_click_delete),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Transaction transaction = transactions.get(position);
                                        new BankingRepository().deleteTransaction(getContext(),
                                                transaction);
                                        transactions.remove(position);
                                        notifyTransaction();
                                    }
                                })
                        .setPositiveButton(getString(R.string.transaction_click_edit),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final View alertDialogView = getLayoutInflater()
                                                .inflate(R.layout.alert_dialog_change, null);
                                        AlertDialog.Builder change = new AlertDialog.Builder(getContext())
                                                .setTitle(getString(R.string.transaction_lv_to))
                                                .setPositiveButton(getString(R.string.preference_btn_ok),
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                updateTransactionDialog(position, alertDialogView);
                                                            }
                                                        });
                                        change.setView(alertDialogView).create().show();
                                    }
                                })
                        .setNeutralButton(getString(R.string.transaction_click_cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                dialog.create().show();
            }
        };
    }

    private void updateTransactionDialog(int position, View alertDialogView) {
        Transaction transaction = transactions.get(position);
        String newBeneficiary = ((TextInputEditText) alertDialogView
                .findViewById(R.id.change_et_to))
                .getText().toString().trim();
        if(!newBeneficiary.isEmpty()) {
            transaction.setNamedBeneficiary(newBeneficiary);
        } else {
            Toast.makeText(getContext(),
                    R.string.add_account_err_name,
                    Toast.LENGTH_LONG)
                    .show();
        }
        new BankingRepository().updateTransaction(getContext(),
                transaction);
        transactions.set(position, transaction);
        notifyTransaction();
    }

    public void addTransaction() {
        if(getArguments() != null) {
            Transaction transaction = getArguments().getParcelable(AccountsFragment.SEND_TRANSACTION_KEY);
            if (transaction != null) {
                new BankingRepository().insertTransaction(getContext(), transaction);
                transactions.add(transaction);
                notifyTransaction();
            }
        }
    }

    private void notifyTransaction() {
        TransactionsAdapter adapter = (TransactionsAdapter)
                lvTransactions.getAdapter();
        adapter.notifyDataSetChanged();
    }

}
