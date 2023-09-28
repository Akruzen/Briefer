/*
 * Code developed by Akruzen (Omkar Phadke)
 * Connect with me on Github via https://github.com/Akruzen
 * */

package com.akruzen.briefer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.akruzen.briefer.Constants.Constants;
import com.akruzen.briefer.db.AppDatabase;
import com.akruzen.briefer.db.Topic;
import com.akruzen.briefer.db.TopicDao;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddContentActivity extends AppCompatActivity {

    TextInputLayout titleTextInput, contentTextInput;
    TextInputEditText contentTextInputEditText, questionTextInputEditText;
    TinyDB tinyDB;
    TopicDao topicDao;
    AppDatabase db;

    public void saveButtonClicked(View view) {
        String title = Objects.requireNonNull(titleTextInput.getEditText()).getText().toString();
        String content = Objects.requireNonNull(contentTextInput.getEditText()).getText().toString();
        if (title.length() > 1 && content.trim().length() > 300) {
            try {
                // Ready the required params
                String currDateTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK).format(new Date());
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                int version = pInfo.versionCode;
                int sdkVersion = Build.VERSION.SDK_INT;

                // Create a db entry
                Topic topic = new Topic(); // Primary key gets auto generated while creating object
                topic.title = title;
                topic.content = content;
                topic.updateTimeStamp = currDateTime; // Time stamp will be the same as current time since object is getting created
                topic.appVersion = version;
                topic.sdkVersion = sdkVersion;
                topicDao.insertTopic(topic);
                Log.i("Database", "titleList:" + topic.title);
                Log.i("Database", "contentList:" + topic.content);
                finish();
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                Toast.makeText(this, "Failed to add topic", Toast.LENGTH_SHORT).show();
            }
             /*ArrayList<String> titleList = tinyDB.getListString(Constants.getTitleListKey());
             titleList.add(title);
             ArrayList<String> contentList = tinyDB.getListString(Constants.getContentListKey());
             contentList.add(content);
             tinyDB.putListString(Constants.getTitleListKey(), titleList);
             tinyDB.putListString(Constants.getContentListKey(), contentList);*/
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
        db = Room.databaseBuilder(this, AppDatabase.class, "TopicDatabase").allowMainThreadQueries().build();
        topicDao = db.topicDao();
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