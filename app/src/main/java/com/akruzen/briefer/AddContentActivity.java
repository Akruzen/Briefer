package com.akruzen.briefer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Constants.Constants;

public class AddContentActivity extends AppCompatActivity {

    TextInputLayout titleTextInput, contentTextInput;
    TinyDB tinyDB;

    public void saveButtonClicked(View view) {
        String title = Objects.requireNonNull(titleTextInput.getEditText()).getText().toString();
        String content = Objects.requireNonNull(contentTextInput.getEditText()).getText().toString();
        if (title.length() > 1 && content.length() > 300) {
            ArrayList<String> titleList = tinyDB.getListString(Constants.getTitleListKey());
            titleList.add(title);
            ArrayList<String> contentList = tinyDB.getListString(Constants.getContentListKey());
            contentList.add(content);
            tinyDB.putListString(Constants.getTitleListKey(), titleList);
            tinyDB.putListString(Constants.getContentListKey(), contentList);
            Log.i("My Logger", "titleList:" + titleList);
            Log.i("My Logger", "contentList:" + contentList);
            finish();
        } else {
            Toast.makeText(this, "Data too short", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        // Find views by id
        titleTextInput = findViewById(R.id.titleTextField);
        contentTextInput = findViewById(R.id.contentTextField);
        // Object Creation
        tinyDB = new TinyDB(this);
    }
}