package com.example.smarttour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private Spinner locationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationSpinner = findViewById(R.id.location_spinner);
        Button proceedButton = findViewById(R.id.proceed_button);
        DBHelper dbHelper = new DBHelper(this);

        // Populate Spinner with locations
        List<String> locations = dbHelper.getAllLocations(); // Method to retrieve locations from DB
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLocation = locationSpinner.getSelectedItem().toString();
                if (!selectedLocation.isEmpty()) {
                    Intent intent = new Intent(LocationActivity.this, PlacesActivity.class);
                    intent.putExtra("selected_location", selectedLocation);
                    startActivity(intent);
                }
            }
        });
    }
}

