package eu.ase.ro.dam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import eu.ase.ro.dam.fragment.AccountsFragment;
import eu.ase.ro.dam.database.model.Account;
import eu.ase.ro.dam.database.model.Transaction;

public class AddTransactionActivity extends AppCompatActivity {
    public final static String ADD_TRANSACTION_KEY = "addTransactionKey";
    public final static String UPDATE_ACCOUNT_KEY = "updateAccountKey";
    public final static int REQ_CODE_ADD_TRANSACTION = 120;
    Account modifiedAccount;
    TextView tvIban;
    TextView tvTransactionType;
    TextInputEditText tvBeneficiaryIban;
    TextInputEditText tvNamedBeneficiary;
    TextInputEditText tvAmount;
    Button btnDone;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        initComponents();
        intent = getIntent();
        getBundleExtras();
    }

    private void getBundleExtras() {
        if(intent.hasExtra(AccountsFragment.OWNER_IBAN_KEY)) {
            tvTransactionType.setText(intent.getStringExtra(AccountsFragment.TRANSACTION_TYPE_KEY));
            modifiedAccount = intent.getParcelableExtra(AccountsFragment.OWNER_IBAN_KEY);
            tvIban.setText(modifiedAccount.getIban());
        }
    }

    private void initComponents() {
        getSupportActionBar().hide();
        tvIban = findViewById(R.id.add_transaction_tv_iban_from);
        tvTransactionType = findViewById(R.id.add_transaction_tv_type);
        tvBeneficiaryIban = findViewById(R.id.add_transaction_tv_iban_beneficiary);
        tvNamedBeneficiary = findViewById(R.id.add_transaction_et_name);
        tvAmount = findViewById(R.id.add_transaction_et_amount);
        btnDone = findViewById(R.id.add_transaction_btn_done);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {
                    Double sumBeforeTransaction = modifiedAccount.getAmount();
                    updateAccount();
                    intent.putExtra(UPDATE_ACCOUNT_KEY, modifiedAccount);
                    if(sumBeforeTransaction != modifiedAccount.getAmount()) {
                        Transaction transaction = createTransaction();
                        intent.putExtra(ADD_TRANSACTION_KEY, transaction);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private Account updateAccount() {
        String[] transactionsType = getResources().getStringArray(R.array.pick_new_operation);
        String choice = tvTransactionType.getText().toString();
        Double givenAmount = Double.parseDouble(tvAmount.getText().toString());
        if(choice.equals(transactionsType[0])
            || choice.equals(transactionsType[1])
            || choice.equals(transactionsType[2])) {
            if(givenAmount > modifiedAccount.getAmount()) {
                Toast.makeText(getApplicationContext(),
                        R.string.transaction_no_money_error,
                        Toast.LENGTH_LONG).show();
            } else {
                modifiedAccount.setAmount(modifiedAccount.getAmount()
                        - givenAmount);
            }
        } else {
            modifiedAccount.setAmount(modifiedAccount.getAmount()
                    + givenAmount);
        }
        return modifiedAccount;
    }

    private Transaction createTransaction() {
        String ownerIban = tvIban.getText().toString();
        String transactionType = tvTransactionType.getText().toString();
        String beneficiaryIban = tvBeneficiaryIban.getText()
                .toString().trim().toUpperCase();
        String namedBeneficiary = tvNamedBeneficiary.getText().toString().trim();
        Double amount = Double.parseDouble(tvAmount.getText().toString());
        return new Transaction(ownerIban, transactionType,
                beneficiaryIban, namedBeneficiary, amount);
    }

    private boolean validateData() {
        if(tvBeneficiaryIban.getText() == null
                || tvBeneficiaryIban.getText().toString().trim().isEmpty()
                || !tvBeneficiaryIban.getText().toString().toUpperCase()
                .startsWith(getString(R.string.validate_iban_starts_ro))
                || tvBeneficiaryIban.getText().toString().trim().length() != 24) {
            Toast.makeText(getApplicationContext(),
                    R.string.add_account_err_iban,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(tvNamedBeneficiary.getText() == null
                || tvNamedBeneficiary.getText().toString().trim().isEmpty()) {
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
