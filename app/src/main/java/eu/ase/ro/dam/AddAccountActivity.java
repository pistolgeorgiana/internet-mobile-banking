package eu.ase.ro.dam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import eu.ase.ro.dam.database.model.Account;

public class AddAccountActivity extends AppCompatActivity {
    public static final int REQ_CODE_ADD_ACCOUNT = 100;
    public static final String ADD_ACCOUNT_KEY = "addAccountKey";
    TextView tvIban;
    TextView tvName;
    TextView tvAmount;
    Spinner spnCurrency;
    Button btnAddAccount;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        getSupportActionBar().hide();
        initComponents();
        intent = getIntent();
    }

    private void initComponents() {
        tvIban = findViewById(R.id.account_tv_iban);
        tvName = findViewById(R.id.account_tv_name);
        tvAmount = findViewById(R.id.account_tv_amount);
        spnCurrency = findViewById(R.id.account_spn_currency);

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                getApplicationContext(),
                R.array.add_account_currency,
                R.layout.spinner_item
        );
        spnCurrency.setAdapter(currencyAdapter);

        btnAddAccount = findViewById(R.id.account_btn_add);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {
                    Account account = createAccount();
                    intent.putExtra(ADD_ACCOUNT_KEY, account);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private Account createAccount() {
        String iban = tvIban.getText().toString().toUpperCase();
        String name = tvName.getText().toString().trim();
        Double amount = Double.parseDouble(tvAmount.getText().toString());
        String currency = spnCurrency.getSelectedItem().toString();
        return new Account(iban, name, currency, amount);
    }

    private boolean validateData() {
        if(tvIban.getText() == null
                || tvIban.getText().toString().trim().isEmpty()
                || !tvIban.getText().toString().toUpperCase()
                        .startsWith(getString(R.string.validate_iban_starts_ro))
                || tvIban.getText().toString().trim().length() != 24) {
            Toast.makeText(getApplicationContext(),
                    R.string.add_account_err_iban,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(tvName.getText() == null
                || tvName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    R.string.add_account_err_name,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(tvAmount.getText() == null
                || tvAmount.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    R.string.add_account_err_amount,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
