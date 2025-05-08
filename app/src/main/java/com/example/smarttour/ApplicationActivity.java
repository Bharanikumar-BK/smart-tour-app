package com.example.smarttour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ApplicationActivity extends AppCompatActivity {

    private TextView headingTextView, detailsTextView;
    private Button feedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        headingTextView = findViewById(R.id.heading);
        detailsTextView = findViewById(R.id.details);
        feedbackButton = findViewById(R.id.feedbackButton);

        // Get the data passed from PlaceDetailsActivity
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String education = getIntent().getStringExtra("education");

        // Set heading and details
        headingTextView.setText("Application Received");
        detailsTextView.setText("Name: " + name + "\nEmail: " + email + "\nEducation: " + education);

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ApplicationActivity.this, FeedbackActivity.class));
            }
        });
    }
}
