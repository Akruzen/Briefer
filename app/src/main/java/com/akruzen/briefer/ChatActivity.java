/*
* Code developed by Akruzen (Omkar Phadke)
* Connect with me on Github via https://github.com/Akruzen
* */

package com.akruzen.briefer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChatActivity extends AppCompatActivity {

    TextView titleTextView, contentTextView;
    TextInputEditText questionEditText;
    MaterialButton askButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Find Views by id
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        questionEditText = findViewById(R.id.questionTextInput);
        askButton = findViewById(R.id.askButton);
        // Method Calls
        setContentTextView();
        handleListeners();
    }

    private void setContentTextView() {
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        titleTextView.setText(title);
        contentTextView.setText(content);
    }

    private void handleListeners() {
        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean shouldAskButtonBeActive = s.length() > 0 // Something should be present
                        && (!s.toString().trim().isEmpty()); // And it should not be empty
                askButton.setEnabled(shouldAskButtonBeActive);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

}