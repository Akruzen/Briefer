package com.akruzen.briefer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

public class AddContentActivity extends AppCompatActivity {

    TextInputLayout titleTextInput, contentTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        titleTextInput = findViewById(R.id.titleTextInput);
        contentTextInput = findViewById(R.id.contentTextInput);
    }
}