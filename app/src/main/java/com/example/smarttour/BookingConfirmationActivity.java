package com.example.smarttour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView headingTextView, detailsTextView;
    private Button feedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        headingTextView = findViewById(R.id.heading);
        detailsTextView = findViewById(R.id.details);
        feedbackButton = findViewById(R.id.feedbackButton);

        // Get the data passed from PlaceDetailsActivity
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");

        // Set heading and details
        headingTextView.setText("Booking Success");
        detailsTextView.setText("Name: " + name + "\nEmail: " + email + "\nDate: " + date + "\nTime: " + time);

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingConfirmationActivity.this, FeedbackActivity.class));
            }
        });
    }
}
