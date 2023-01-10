package eu.ase.ro.dam.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import eu.ase.ro.dam.R;
import eu.ase.ro.dam.database.model.Transaction;

public class TransactionsAdapter extends ArrayAdapter<Transaction> {
    private Context context;
    private int resource;
    private List<Transaction> transactions;
    private LayoutInflater layoutInflater;

    public TransactionsAdapter(@NonNull Context context,
                               int resource,
                               List<Transaction> transactions,
                               LayoutInflater layoutInflater) {
        super(context, resource, transactions);
        this.context = context;
        this.resource = resource;
        this.transactions = transactions;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.lv_transaction_view, parent, false);
        Transaction transaction = transactions.get(position);
        if(transaction != null) {
            TextView tvOwnerIban = view.findViewById(R.id.lv_transaction_et_owner_iban);
            TextView tvTransactionType = view.findViewById(R.id.lv_transaction_et_type);
            TextView tvTransactionTo = view.findViewById(R.id.lv_transaction_et_to);
            TextView tvAmount = view.findViewById(R.id.lv_transaction_et_amount);

            tvOwnerIban.setText(transaction.getOwnerIban());
            tvTransactionType.setText(transaction.getTransactionType());
            tvTransactionTo.setText(tvTransactionTo.getText().toString()
                    + " " + transaction.getNamedBeneficiary());
            tvAmount.setText(String.format("%.2f", transaction.getAmount()));
        }
        return view;
    }
}
