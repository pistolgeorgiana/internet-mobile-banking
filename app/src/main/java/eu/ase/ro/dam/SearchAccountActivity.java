package eu.ase.ro.dam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.dam.database.model.Account;
import eu.ase.ro.dam.database.repository.BankingRepository;
import eu.ase.ro.dam.util.AccountAdapter;
import eu.ase.ro.dam.util.Bank;

public class SearchAccountActivity extends AppCompatActivity {

    private static final String FILE_NAME = "myaccounts.txt";
    private List<Account> accounts = new ArrayList<>();

    TextInputEditText etOwner;
    Button btnSearch;
    Button btnCancel;
    Button btnExport;
    ListView lvAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_account);
        initComponents();
    }

    private void initComponents() {
        getSupportActionBar().hide();

        etOwner = findViewById(R.id.search_et_name);

        btnCancel = findViewById(R.id.search_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnExport = findViewById(R.id.search_btn_export);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToTxt();
            }
        });

        btnSearch = findViewById(R.id.search_btn_find);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accounts.clear();
                notifyAccount();
                String owner = etOwner.getText().toString().trim();
                if(owner != null) {
                    new BankingRepository.FetchAccountsByOwner(getApplicationContext()) {
                        @Override
                        protected void onPostExecute(List<Account> res) {
                            if(res != null) {
                                accounts.addAll(res);
                                notifyAccount();
                            }
                        }
                    }.execute(owner);
                }
            }
        });

        lvAccounts = findViewById(R.id.search_lv_found_accounts);
        setAccountAdapter();
    }

    private void exportToTxt() {
        if(accounts != null) {
            try {
                FileOutputStream stream = openFileOutput(FILE_NAME, MODE_PRIVATE);
                for (Account account : accounts) {
                    stream.write((account.getIban() + ' ').getBytes());
                    stream.write((account.getName() + ' ').getBytes());
                    stream.write((account.getCurrency() + ' ').getBytes());
                    stream.write((account.getAmount().toString() + "\n").getBytes());
                }
                stream.close();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.search_txt_exported),
                        Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void setAccountAdapter() {
        AccountAdapter adapter = new AccountAdapter(
                getApplicationContext(),
                R.layout.lv_account_view,
                accounts, getLayoutInflater());
        lvAccounts.setAdapter(adapter);
    }

    private void notifyAccount() {
        AccountAdapter adapter = (AccountAdapter) lvAccounts.getAdapter();
        adapter.notifyDataSetChanged();
    }
}
