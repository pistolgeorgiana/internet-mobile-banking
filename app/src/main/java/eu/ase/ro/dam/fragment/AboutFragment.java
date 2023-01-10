package eu.ase.ro.dam.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import eu.ase.ro.dam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {
    public static final String PREFERENCES_KEY = "preferencesKey";
    public static final String RATING_BAR_VALUE = "ratingBarValue";
    public static final String SWITCH_VALUE = "switchValue";
    public static final String TEXT_INPUT_VALUE = "textInputValue";

    private boolean switchChecked = true;

    private RatingBar ratingBar;
    private SharedPreferences sharedPreferences;
    private Switch addSuggestion;
    private TextInputEditText suggestions;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        ratingBar = view.findViewById(R.id.about_rating_bar);

        if (getActivity() != null) {
            sharedPreferences = getActivity().
                    getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
            float starsRate = sharedPreferences.getFloat(RATING_BAR_VALUE, -1);
            if (starsRate != -1) {
                ratingBar.setRating(starsRate);
            }


            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putFloat(RATING_BAR_VALUE, rating);
                    edit.apply();
                    if (rating < 5) {
                        View alertDialogView = getLayoutInflater().inflate
                                (R.layout.alert_dialog_preference, null);

                        addSuggestion = alertDialogView.findViewById(R.id.preference_switch_share);
                        suggestions = alertDialogView.findViewById(R.id.preference_et_suggestion);

                        boolean checked = sharedPreferences.getBoolean(SWITCH_VALUE, false);
                        if(checked) {
                            addSuggestion.setChecked(true);
                            String suggestion = sharedPreferences.getString(TEXT_INPUT_VALUE, "No suggestions");
                            if (!suggestion.equals("No suggestions")) {
                                suggestions.setText(suggestion);
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder
                                (getContext())
                                .setPositiveButton(R.string.preference_btn_ok,
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences.Editor writer = sharedPreferences.edit();
                                        switchChecked = sharedPreferences.getBoolean(SWITCH_VALUE, false);
                                        if (switchChecked) {
                                            writer.putString(TEXT_INPUT_VALUE,
                                                    suggestions.getText().toString());
                                        }
                                        writer.apply();
                                    }
                                });

                        if(addSuggestion != null) {
                            addSuggestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    SharedPreferences.Editor writer = sharedPreferences.edit();
                                    writer.putBoolean(SWITCH_VALUE, isChecked);
                                    writer.apply();
                                }
                            });
                        }

                        builder.setView(alertDialogView);
                        builder.create().show();
                    }
                    Toast.makeText(getContext().getApplicationContext(),
                            getString(R.string.about_toast_ratingbar_feedback),
                            Toast.LENGTH_LONG).show();
                    ratingBar.setRating(rating);
                }
            });
        }
    }
}
