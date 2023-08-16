package com.akruzen.briefer.dataset;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.stream.Collectors;

public class DatasetJava {
    @SerializedName("titles")
    private final List<List<String>> titles;
    @SerializedName("contents")
    private final List<List<String>> contents;
    @SerializedName("questions")
    public final List<List<String>> questions;

    public DatasetJava(List<List<String>> titles, List<List<String>> contents, List<List<String>> questions) {
        this.titles = titles;
        this.contents = contents;
        this.questions = questions;
    }

    public List<String> getTitles() {
        return titles.stream().map(it -> it.get(0)).collect(Collectors.toList());
    }

    public List<String> getContents() {
        return contents.stream().map(it -> it.get(0)).collect(Collectors.toList());
    }
}

