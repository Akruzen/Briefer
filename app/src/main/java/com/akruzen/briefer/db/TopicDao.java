package com.akruzen.briefer.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TopicDao {
    @Query("SELECT * FROM Topic")
    List<Topic> getAllTopics(); // Get all topics

    @Query("SELECT * FROM Topic WHERE uid = :uid")
    Topic getTopic(String uid); // Get topic by uid

    @Insert
    void insertTopic (Topic topic);

    @Delete
    void deleteTopic (Topic topic);
}
