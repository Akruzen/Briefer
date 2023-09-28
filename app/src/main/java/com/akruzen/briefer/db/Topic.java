package com.akruzen.briefer.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Topic {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "uid")
    public String uid;

    @ColumnInfo (name = "title")
    public String title; // Title

    @ColumnInfo (name = "content")
    public String content; // Actual content

    @ColumnInfo (name = "type")
    public String type; // If it is plain text, pdf file, txt file, etc.

    @ColumnInfo (name = "time_stamp")
    public String updateTimeStamp; // When was the topic last updated

    @ColumnInfo (name = "app_version")
    public int appVersion; // App version when the topic was created

    @ColumnInfo (name = "sdk_version")
    public int sdkVersion; // Android version when the topic was created

    public Topic() {
        // Auto generate UID based on current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
        uid = sdf.format(new Date());
    }
}
