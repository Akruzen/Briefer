package com.akruzen.briefer.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Topic.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TopicDao topicDao();
}
