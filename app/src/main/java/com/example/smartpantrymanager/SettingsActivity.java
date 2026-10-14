package com.example.smartpantrymanager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
public class SettingsActivity extends AppCompatActivity {
    private Switch switchExpiryAlerts;
    private Spinner spinnerDefaultUnit;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_DEFAULT_UNIT = "default_unit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        spinnerDefaultUnit = findViewById(R.id.spinnerDefaultUnit);
        preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );
        // Restore the saved expiry-alert setting
        boolean alertsEnabled = preferences.getBoolean(
                KEY_EXPIRY_ALERTS, true
        );
        switchExpiryAlerts.setChecked(alertsEnabled);
        // Restore the saved default unit
        String savedUnit = preferences.getString(
                KEY_DEFAULT_UNIT, "Grams (g)"
        );
        // Set up the unit dropdown
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.pantry_units,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerDefaultUnit.setAdapter(adapter);
        int position = adapter.getPosition(savedUnit);
        if (position >= 0) {
            spinnerDefaultUnit.setSelection(position);
        }
        // Save the expiry-alert preference whenever it changes
        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    preferences.edit()
                            .putBoolean(KEY_EXPIRY_ALERTS, isChecked)
                            .apply();
                }
        );
        // Save the selected default unit
        spinnerDefaultUnit.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        String unit = parent
                                .getItemAtPosition(position)
                                .toString();
                        preferences.edit()
                                .putString(KEY_DEFAULT_UNIT, unit)
                                .apply();
                    }
                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
    }
}