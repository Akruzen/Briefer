/*
 * Code developed by Akruzen (Omkar Phadke)
 * Connect with me on Github via https://github.com/Akruzen
 * */

package com.akruzen.briefer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.akruzen.briefer.db.AppDatabase;
import com.akruzen.briefer.db.Topic;
import com.akruzen.briefer.db.TopicDao;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import com.akruzen.briefer.Constants.Constants;

public class MainActivity extends AppCompatActivity {

    TinyDB tinyDB;
    AppDatabase db;
    TopicDao topicDao;
    List<Topic> topicList;
    ListView titleListView;
    ArrayList<String> titleList, contentList;
    ArrayAdapter<String> titleArrayAdapter;
    ScrollView scrollView;

    public void onFABClicked (View view) {
        Intent intent = new Intent(this, AddContentActivity.class);
        startActivity(intent);
    }

    public void infoButtonClicked (View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("About");
        builder.setMessage(getString(R.string.about_app));
        builder.setPositiveButton("View on GitHub", (dialog, which) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Akruzen/Briefer"));
            startActivity(browserIntent);
        });
        builder.setNegativeButton("Dismiss", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    public void helpButtonClicked (View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void aboutButtonClicked (View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void settingsButtonClicked (View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list view when activity is resumed
        populateListView();
        if (titleArrayAdapter != null) {
            titleArrayAdapter.notifyDataSetChanged();
        }
        setScrollViewVisibility();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find views by id
        titleListView = findViewById(R.id.titleListView);
        scrollView = findViewById(R.id.scrollView);
        // Object Creation
        db = Room.databaseBuilder(this, AppDatabase.class, "TopicDatabase").allowMainThreadQueries().build();
        topicDao = db.topicDao();
        tinyDB = new TinyDB(this);
        // Method Calls
        populateListView(); // Keep this call first in onCreate as it initializes the list
        setScrollViewVisibility();
    }

    private void populateListView() {
        topicList = topicDao.getAllTopics();
        titleList = new ArrayList<>();
        contentList = new ArrayList<>();
        if (!topicList.isEmpty()) {
            // Generate titleList and contentList from topic object
            for (Topic topic : topicList) {
                titleList.add(topic.title);
                contentList.add(topic.content);
            }
            // Create an ArrayAdapter to display the ArrayList elements in the ListView
            titleArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList);
            // Set the adapter to the titleListView
            titleListView.setAdapter(titleArrayAdapter);
            titleListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("title", titleList.get(position));
                intent.putExtra("content", contentList.get(position));
                startActivity(intent);
            });
            titleListView.setOnItemLongClickListener((parent, view, position, id) -> {
                // Display the delete confirmation dialog
                showDeleteMaterialDialog(topicList.get(position), this, "Delete", "Are you sure you want to delete " + titleList.get(position) + "?");
                return true; // Returning true here means we have consumed the event and no further click events will be fired.
            });
        }
    }

    private void showDeleteMaterialDialog(Topic topic, Context context, String title, String content) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("Delete", (dialog, which) -> {
            titleList.remove(topic.title);
            contentList.remove(topic.content);
            topicDao.deleteTopic(topic);
            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            // Refresh the list view
            titleArrayAdapter.notifyDataSetChanged();
            // Set Scroll View image
            setScrollViewVisibility();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void setScrollViewVisibility() {
        if (topicList.isEmpty()) {
            scrollView.setVisibility(View.VISIBLE);
        } else {
            scrollView.setVisibility(View.GONE);
        }
    }


}