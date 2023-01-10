package eu.ase.ro.dam;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import eu.ase.ro.dam.fragment.AboutFragment;
import eu.ase.ro.dam.fragment.AccountsFragment;
import eu.ase.ro.dam.fragment.ProfileFragment;
import eu.ase.ro.dam.fragment.TransactionsFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    public static final AccountsFragment fragment1 = new AccountsFragment();
    public static final TransactionsFragment fragment2 = new TransactionsFragment();
    public static final ProfileFragment fragment3 = new ProfileFragment();
    public static final AboutFragment fragment4 = new AboutFragment();
    final FragmentManager fm = getSupportFragmentManager();
    public static Fragment active = fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        if(savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_account_balance);
        }
    }

    private void initComponents() {
        fm.beginTransaction().add(R.id.fragment_layout_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragment_layout_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragment_layout_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_layout_container,fragment1, "1").commit();
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);
        getSupportActionBar().hide();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_account_balance:
                            fm.beginTransaction().hide(active).show(fragment1).commit();
                            active = fragment1;
                            break;
                        case R.id.nav_transactions_history:
                            fm.beginTransaction().hide(active).show(fragment2).commit();
                            active = fragment2;
                            break;
                        case R.id.nav_profile:
                            fm.beginTransaction().hide(active).show(fragment3).commit();
                            active = fragment3;
                            break;
                        case R.id.nav_about:
                            fm.beginTransaction().hide(active).show(fragment4).commit();
                            active = fragment4;
                            break;
                    }
                    return true;
                }
            };

}
