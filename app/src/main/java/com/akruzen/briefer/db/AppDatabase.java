package com.akruzen.briefer.db;

import androidx.room.RoomDatabase;

public abstract class AppDatabase extends RoomDatabase {
    public abstract TopicDao topicDao();
}
