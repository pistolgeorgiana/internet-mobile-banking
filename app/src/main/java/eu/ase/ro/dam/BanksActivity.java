package eu.ase.ro.dam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.dam.util.Bank;

public class BanksActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private final static String FIREBASE_REFERENCE_PATH = "banks";

    private List<Bank> banksList = new ArrayList<>();
    private int currentBankIndex = -1;

    private TextInputEditText etBic;
    private TextInputEditText etName;
    private TextInputEditText etCountry;
    private FloatingActionButton fabAdd;
    private Button btnCancel;
    private Button btnDelete;
    private Spinner spnType;
    private ListView lvBanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banks);
        initComponents();
    }

    private void initComponents() {
        getSupportActionBar().hide();
        populateListView();

        etBic = findViewById(R.id.banks_et_bic);
        etName = findViewById(R.id.banks_et_name);
        etCountry = findViewById(R.id.banks_et_country);

        spnType = findViewById(R.id.banks_spn_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(getApplicationContext(),
                        R.array.banks_spn_type,
                        R.layout.spinner_item);
        spnType.setAdapter(adapter);

        lvBanks = findViewById(R.id.banks_lv_banks);
        setBanksAdapter();
        lvBanks.setOnItemClickListener(setBankIndex());

        fabAdd = findViewById(R.id.banks_fab_save);
        fabAdd.setOnClickListener(saveBank());

        btnCancel = findViewById(R.id.banks_btn_cancel);
        btnCancel.setOnClickListener(editBank());

        btnDelete = findViewById(R.id.banks_btn_delete);
        btnDelete.setOnClickListener(deleteBank());
    }

    private AdapterView.OnItemClickListener setBankIndex() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentBankIndex = position;
                if(currentBankIndex >= 0) {
                    String bankId = banksList.get(currentBankIndex).getId();
                    reference.child(bankId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Bank bank = dataSnapshot.getValue(Bank.class);
                            etBic.setText(bank.getBIC());
                            etName.setText(bank.getName());
                            etCountry.setText(bank.getCountry());
                            spnType.setSelection(setSpinner(bank.getType()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("Editing status", "error ocurred while fetching data...");
                        }
                    });
                }
            }
        };
    }

    private void populateListView() {
        reference = FirebaseDatabase.getInstance().getReference(FIREBASE_REFERENCE_PATH);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Bank> banks = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    banks.add(snap.getValue(Bank.class));
                }
                clearViews();
                banksList.clear();
                banksList.addAll(banks);
                notifiyBank();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Status", "action cancelled");
            }
        });
    }

    private void setBanksAdapter() {
        ArrayAdapter<Bank> banksAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                banksList);
        lvBanks.setAdapter(banksAdapter);
    }

    private boolean validateInput() {
        if(etBic.getText() == null || etBic.getText()
                .toString().trim().isEmpty() || etBic.getText()
                .toString().trim().length() != 4) {
            return false;
        }
        if(etName.getText() == null || etName.getText()
                .toString().isEmpty()) {
            return false;
        }
        if(etCountry.getText() == null || etCountry.getText()
                .toString().isEmpty()) {
            return false;
        }
        return true;
    }

    private View.OnClickListener saveBank() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()) {
                    String id = null;
                    if(currentBankIndex >= 0) {
                        id = banksList.get(currentBankIndex).getId();
                    } else {
                        id = reference.push().getKey();
                    }
                    Bank bank = new Bank(id, etBic.getText()
                            .toString().trim().toUpperCase(),
                            etName.getText().toString().trim(),
                            etCountry.getText().toString().trim(),
                            spnType.getSelectedItem().toString());
                    reference.child(bank.getId()).setValue(bank);
                }
            }
        };
    }

    private View.OnClickListener editBank() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBankIndex = -1;
                clearViews();
                finish();
            }
        };
    }

    private int setSpinner(String bankType) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>)
                spnType.getAdapter();
        for(int i=0; i<adapter.getCount(); i++) {
            if(adapter.getItem(i).equals(bankType)) {
                return i;
            }
        }
        return -1;
    }

    private View.OnClickListener deleteBank() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentBankIndex >= 0) {
                    reference.child(banksList.get(currentBankIndex).getId()).removeValue();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.banks_firebase_delete),
                            Toast.LENGTH_LONG);
                }
            }
        };
    }

    private void clearViews() {
        currentBankIndex = -1;
        etBic.setText("");
        etName.setText("");
        etCountry.setText("");
        spnType.setSelection(0);
    }

    private void notifiyBank() {
        ArrayAdapter<Bank> bankAdapter = (ArrayAdapter<Bank>) lvBanks.getAdapter();
        bankAdapter.notifyDataSetChanged();
    }
}
