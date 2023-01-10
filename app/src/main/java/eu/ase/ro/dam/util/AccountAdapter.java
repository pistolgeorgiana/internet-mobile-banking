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
import eu.ase.ro.dam.database.model.Account;

public class AccountAdapter extends ArrayAdapter<Account> {
    private Context context;
    private int resource;
    private List<Account> accounts;
    private LayoutInflater layoutInflater;

    public AccountAdapter(@NonNull Context context,
                          int resource,
                          List<Account> accounts,
                          LayoutInflater layoutInflater) {
        super(context, resource, accounts);
        this.context = context;
        this.resource = resource;
        this.accounts = accounts;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(resource, parent, false);
        TextView tvAccount = convertView.findViewById(R.id.lv_account_tv_current_account);
        TextView tvName = convertView.findViewById(R.id.lv_account_tv_name);
        TextView tvCurrencyAmount = convertView.findViewById(R.id.lv_account_tv_amount);

        tvAccount.setText(accounts.get(position).getIban());
        tvName.setText(accounts.get(position).getName());
        tvCurrencyAmount.setText(String.format("%.2f", accounts.get(position).getAmount())
                    + " " + accounts.get(position).getCurrency());

        return convertView;
    }
}
