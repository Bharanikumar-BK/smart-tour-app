package com.example.smarttour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PlacesActivity extends AppCompatActivity {

    private ListView hotelListView;
    private ListView attractionListView;
    private ListView jobPlaceListView;
    private DBHelper dbHelper;
    private String selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        TextView hotelHeading = findViewById(R.id.hotel_heading);
        TextView attractionHeading = findViewById(R.id.attraction_heading);
        TextView jobPlaceHeading = findViewById(R.id.job_place_heading);
        hotelListView = findViewById(R.id.hotel_listview);
        attractionListView = findViewById(R.id.attraction_listview);
        jobPlaceListView = findViewById(R.id.job_place_listview);
        dbHelper = new DBHelper(this);

        // Get selected location from intent
        selectedLocation = getIntent().getStringExtra("selected_location");

        // Fetch and display places for the selected location
        displayPlaces(selectedLocation);

        // Set up onClick listeners for each ListView
        setupListViewClickListeners();
    }

    private void displayPlaces(String location) {
        // Get hotels, attractions, and job places for the selected location
        List<String> hotels = dbHelper.getPlacesByType(location, "hotel");
        List<String> attractions = dbHelper.getPlacesByType(location, "attraction");
        List<String> jobPlaces = dbHelper.getPlacesByType(location, "job_place");

        // Set the ListView adapters to display the lists
        ArrayAdapter<String> hotelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hotels);
        hotelListView.setAdapter(hotelAdapter);

        ArrayAdapter<String> attractionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attractions);
        attractionListView.setAdapter(attractionAdapter);

        ArrayAdapter<String> jobPlaceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jobPlaces);
        jobPlaceListView.setAdapter(jobPlaceAdapter);
    }

    private void setupListViewClickListeners() {
        // For hotels
        hotelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedHotel = (String) parent.getItemAtPosition(position);
                // Launch PlaceDetailsActivity with type "hotel"
                launchPlaceDetailsActivity(selectedHotel, "hotel");
            }
        });

        // For attractions
        attractionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedAttraction = (String) parent.getItemAtPosition(position);
                // Launch PlaceDetailsActivity with type "attraction"
                launchPlaceDetailsActivity(selectedAttraction, "attraction");
            }
        });

        // For job places
        jobPlaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedJobPlace = (String) parent.getItemAtPosition(position);
                // Launch PlaceDetailsActivity with type "job_place"
                launchPlaceDetailsActivity(selectedJobPlace, "job_place");
            }
        });
    }

    private void launchPlaceDetailsActivity(String placeName, String placeType) {
        Intent intent = new Intent(PlacesActivity.this, PlaceDetailsActivity.class);
        intent.putExtra("place_name", placeName);
        intent.putExtra("place_type", placeType);
        intent.putExtra("location", selectedLocation);

        // Depending on the place type, navigate to the appropriate activity after PlaceDetailsActivity
        if (placeType.equals("hotel") || placeType.equals("attraction")) {
            // For hotels and attractions, navigate to PlaceDetailsActivity to handle booking
            startActivity(intent);
        } else if (placeType.equals("job_place")) {
            // For job places, navigate to PlaceDetailsActivity to handle application
            startActivity(intent);
        }
    }
}
