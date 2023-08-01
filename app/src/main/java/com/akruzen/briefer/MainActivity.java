package com.akruzen.briefer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Constants.Constants;

public class MainActivity extends AppCompatActivity {

    TinyDB tinyDB;
    ListView titleListView;

    public void onFABClicked (View view) {
        List<String> titleList = tinyDB.getListString(Constants.getTitleListKey());
        List<String> contentList = tinyDB.getListString(Constants.getContentListKey());
        Log.i("My Logger", "titleList:" + titleList.toString());
        Log.i("My Logger", "contentList:" + contentList.toString());
        Intent intent = new Intent(this, AddContentActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find views by id
        titleListView = findViewById(R.id.titleListView);
        // Object Creation
        tinyDB = new TinyDB(this);
        // Method Calls
        populateListView();
    }

    private void populateListView() {
        ArrayList<String> titleList = tinyDB.getListString(Constants.getTitleListKey());
        if (!titleList.isEmpty()) {
            // Create an ArrayAdapter to display the ArrayList elements in the ListView
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList);
            // Set the adapter to the titleListView
            titleListView.setAdapter(arrayAdapter);
        }
    }

}