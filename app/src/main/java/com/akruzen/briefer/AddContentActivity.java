/*
 * Code developed by Akruzen (Omkar Phadke)
 * Connect with me on Github via https://github.com/Akruzen
 * */

package com.akruzen.briefer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import Constants.Constants;

public class AddContentActivity extends AppCompatActivity {

    TextInputLayout titleTextInput, contentTextInput;
    TextInputEditText contentTextInputEditText, questionTextInputEditText;
    TinyDB tinyDB;

    public void saveButtonClicked(View view) {
        String title = Objects.requireNonNull(titleTextInput.getEditText()).getText().toString();
        String content = Objects.requireNonNull(contentTextInput.getEditText()).getText().toString();
        if (title.length() > 1 && content.trim().length() > 300) {
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
        titleTextInput = findViewById(R.id.questionTextField);
        contentTextInput = findViewById(R.id.contentTextField);
        contentTextInputEditText = findViewById(R.id.contentTextInput);
        questionTextInputEditText = findViewById(R.id.questionTextInput);
        // Object Creation
        tinyDB = new TinyDB(this);
        // Method Calls
        setCharLimit();
    }

    private void setCharLimit() {
        int charLimit = Integer.parseInt(tinyDB.getString(Constants.getCharLimitKey()));
        titleTextInput.setCounterMaxLength(charLimit);
        contentTextInput.setCounterMaxLength(charLimit);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(charLimit);
        contentTextInputEditText.setFilters(filterArray);
    }
}