package com.akruzen.briefer.dataset;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class LoadDataSetClientJava {
    private static final String TAG = "BertAppDemo";
    private static final String JSON_DIR = "qa.json";

    private final Context context;

    public LoadDataSetClientJava(Context context) {
        this.context = context;
    }

    // Load json file into a data object.
    public DatasetJava loadJson() {
        DatasetJava dataSet = null;
        try {
            InputStream inputStream = context.getAssets().open(JSON_DIR);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String stringJson = bufferedReader.readLine();
            Type datasetType = new TypeToken<DatasetJava>() {}.getType();
            dataSet = new Gson().fromJson(stringJson, datasetType);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return dataSet;
    }
}

