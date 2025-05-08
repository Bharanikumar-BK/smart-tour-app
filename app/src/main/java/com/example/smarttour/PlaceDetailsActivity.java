package com.example.smarttour;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PlaceDetailsActivity extends AppCompatActivity {

    private TextView educationTextView;
    private EditText nameEditText, emailEditText, dateEditText, timeEditText;
    private Button applyButton;
    private DBHelper dbHelper;
    private FirebaseAuth mAuth;

    private String placeType, placeName, location;
    private final String[] educationLevels = {"BSc", "MSc", "BCom", "MCom", "BA", "MA", "BE", "B.Tech"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // Initialize UI elements
        TextView nameTextView = findViewById(R.id.name);
        TextView addressTextView = findViewById(R.id.address);
        nameEditText = findViewById(R.id.Name);
        emailEditText = findViewById(R.id.email);
        dateEditText = findViewById(R.id.dateEditText); // Only for hotels and attractions
        timeEditText = findViewById(R.id.timeEditText); // Only for hotels and attractions
        educationTextView = findViewById(R.id.educationTextView); // For job places
        Button bookButton = findViewById(R.id.bookButton); // For hotels and attractions
        applyButton = findViewById(R.id.applyButton); // For job places
        dbHelper = new DBHelper(this);
        mAuth = FirebaseAuth.getInstance();

        // Get data from intent
        placeName = getIntent().getStringExtra("place_name");
        placeType = getIntent().getStringExtra("place_type");
        location = getIntent().getStringExtra("location");

        // Set the place name and fetch the address
        nameTextView.setText(placeName);
        String address = dbHelper.getPlaceAddress(location, placeType, placeName);
        addressTextView.setText(address);

        // Show relevant fields and buttons based on place type
        if ("hotel".equals(placeType) || "attraction".equals(placeType)) {
            // Display date and time fields and book button for hotels and attractions
            dateEditText.setVisibility(View.VISIBLE);
            timeEditText.setVisibility(View.VISIBLE);
            bookButton.setVisibility(View.VISIBLE);
            educationTextView.setVisibility(View.GONE); // Hide education field
            applyButton.setVisibility(View.GONE); // Hide apply button
        } else if ("job_place".equals(placeType)) {
            // Display education selection and apply button for job places
            dateEditText.setVisibility(View.GONE); // Hide date field
            timeEditText.setVisibility(View.GONE); // Hide time field
            bookButton.setVisibility(View.GONE); // Hide book button
            educationTextView.setVisibility(View.VISIBLE); // Show education field
            applyButton.setVisibility(View.VISIBLE); // Show apply button

            // Set click listener to open education selection dialog
            educationTextView.setOnClickListener(v -> showEducationDialog());
        }

        // Handle book button click (for hotels and attractions)
        bookButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String time = timeEditText.getText().toString().trim();

            if (email.isEmpty() || name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(PlaceDetailsActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                return;
            }

            sendEmailVerification(email, name, date, time);
        });

        // Handle apply button click (for job places)
        applyButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String education = educationTextView.getText().toString().trim();

            if (email.isEmpty() || name.isEmpty() || education.isEmpty()) {
                Toast.makeText(PlaceDetailsActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                return;
            }

            sendEmailVerification(email, name, education, "");
        });
    }

    // Method to send email verification (common for both book and apply actions)
    private void sendEmailVerification(String email, String name, String detail1, String detail2) {
        mAuth.createUserWithEmailAndPassword(email, "default_password")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                if (verificationTask.isSuccessful()) {
                                    if ("hotel".equals(placeType) || "attraction".equals(placeType)) {
                                        navigateToBookingConfirmationActivity(name, email, detail1, detail2);
                                    } else if ("job_place".equals(placeType)) {
                                        navigateToApplicationActivity(name, email, detail1);
                                    }
                                } else {
                                    Toast.makeText(PlaceDetailsActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(PlaceDetailsActivity.this, "Failed to create user for email verification.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to show education selection dialog
    private void showEducationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceDetailsActivity.this);
        builder.setTitle("Select Education Level");

        builder.setItems(educationLevels, (dialog, which) -> {
            educationTextView.setText(educationLevels[which]);
        });

        builder.show();
    }

    // Navigate to booking confirmation for hotels and attractions
    private void navigateToBookingConfirmationActivity(String name, String email, String date, String time) {
        Intent intent = new Intent(PlaceDetailsActivity.this, BookingConfirmationActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        startActivity(intent);
    }

    // Navigate to application activity for job places
    private void navigateToApplicationActivity(String name, String email, String education) {
        Intent intent = new Intent(PlaceDetailsActivity.this, ApplicationActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("education", education);
        startActivity(intent);
    }
}
