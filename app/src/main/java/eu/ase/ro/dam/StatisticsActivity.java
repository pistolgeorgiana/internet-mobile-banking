package eu.ase.ro.dam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.dam.database.repository.BankingRepository;
import eu.ase.ro.dam.util.PieChart;

public class StatisticsActivity extends AppCompatActivity {

    private ArrayList<Double> values = new ArrayList<>();

    TextView plusAmount;
    TextView minusAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initComponents();
    }

    private void initComponents() {
        getSupportActionBar().hide();

        displayStatistics();
    }

    private void displayStatistics() {
        String[] operations = getResources().getStringArray(R.array.pick_new_operation);
        final List<String> minusOperations = new ArrayList<>();
        final List<String> plusOperations = new ArrayList<>();
        for(int i=0; i<operations.length; i++) {
            if(i <= operations.length/2) {
                minusOperations.add(operations[i]);
            } else {
                plusOperations.add(operations[i]);
            }
        }

        plusAmount = findViewById(R.id.statistics_plus_amount);
        minusAmount = findViewById(R.id.statistics_minus_amount);

        new BankingRepository.GetAmountByType(getApplicationContext()) {
            @Override
            protected void onPostExecute(List<Double> result) {
                if(result != null) {
                    displayGraphic(result);
                }
            }
        }.execute(plusOperations, minusOperations);
    }

    private void displayGraphic(List<Double> result) {
        values.addAll(result);
        plusAmount.setText(String.format("+ %5.2f", values.get(0)));
        minusAmount.setText(String.format("- %5.2f", values.get(1)));

        values = toRadians(result);
        RelativeLayout graphicLayout = findViewById(R.id.pie_chart_my_transactions);
        PieChart pieChart = new PieChart(getApplicationContext(), values);
        graphicLayout.addView(pieChart);
    }

    private ArrayList<Double> toRadians(List<Double> values) {
        ArrayList<Double> result = new ArrayList<>();
        Double sum = 0.0;
        for(Double value:values) {
            sum += value;
        }
        for(int i=0; i < values.size(); i++) {
            result.add(i, 360 * (values.get(i) / sum));
        }
        return result;
    }
}
