package com.akruzen.briefer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    TextView titleTextView, contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Find Views by id
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        // Method Calls
        setContentTextView();
    }

    private void setContentTextView() {
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        titleTextView.setText(title);
        contentTextView.setText(content);
    }

}