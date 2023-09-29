package com.akruzen.briefer.db;

import static com.google.common.reflect.Reflection.getPackageName;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Topic {

    /*-- Types dictionary --*/
    public static final int TYPE_PLAIN_TEXT = 1;
    public static final int TYPE_SHARED_TEXT = 2;
    public static final int TYPE_PDF_FILE = 21;
    public static final int TYPE_TXT_FILE = 22;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "uid")
    public String uid;

    @NonNull
    @ColumnInfo (name = "title")
    public String title; // Title

    @NonNull
    @ColumnInfo (name = "content")
    public String content; // Actual content

    @ColumnInfo (name = "type")
    public int type; // If it is plain text, pdf file, txt file, etc.

    @NonNull
    @ColumnInfo (name = "time_stamp")
    public String updateTimeStamp; // When was the topic last updated

    @ColumnInfo (name = "app_version")
    public int appVersion; // App version when the topic was created

    @ColumnInfo (name = "sdk_version")
    public int sdkVersion; // Android version when the topic was created

    public Topic(@NonNull String title, @NonNull String content, int type, @NonNull String updateTimeStamp, int appVersion, int sdkVersion) {
        // Auto generate UID based on current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);

        this.uid = sdf.format(new Date());
        this.title = title;
        this.content = content;
        this.type = type;
        this.updateTimeStamp = updateTimeStamp;
        this.appVersion = appVersion;
        this.sdkVersion = sdkVersion;
    }
}
