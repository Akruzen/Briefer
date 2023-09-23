package com.akruzen.briefer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.slider.Slider;

import org.checkerframework.checker.units.qual.A;
import org.tensorflow.lite.task.text.qa.QaAnswer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Constants.Constants;

public class SettingsActivity extends AppCompatActivity implements BertQaHelper.AnswererListener {

    TextView threadCountTextView, statusTextView, charLimitTextView, testTimeTextView;
    TinyDB tinyDB;
    BertQaHelper bertQaHelper;
    MaterialButtonToggleGroup delegateToggleGroup;
    MaterialButton resetButton;
    Slider charLimitSlider;
    LinearProgressIndicator precisionIndicator;
    String charLimit;
    Activity activity;

    public void increaseThreadCount(View view) {
        if (Integer.parseInt(threadCountTextView.getText().toString()) < 10) {
            threadCountTextView.setText(String.valueOf(Integer.parseInt(threadCountTextView.getText().toString()) + 1));
            tinyDB.putString(Constants.getThreadCountKey(), threadCountTextView.getText().toString());
            updateStatusTextView();
        }
    }

    public void decreaseThreadCount(View view) {
        if (Integer.parseInt(threadCountTextView.getText().toString()) > 1) {
            threadCountTextView.setText(String.valueOf(Integer.parseInt(threadCountTextView.getText().toString()) - 1));
            tinyDB.putString(Constants.getThreadCountKey(), threadCountTextView.getText().toString());
            updateStatusTextView();
        }
    }

    public void resetButtonTapped(View view) {
        tinyDB.putString(Constants.getDelegateKey(), "0");
        tinyDB.putString(Constants.getThreadCountKey(), "2");
        threadCountTextView.setText("2");
        delegateToggleGroup.check(R.id.cpuBtn);
        updateStatusTextView();
    }

    public void resetCharLimit(View view) {
        String txt = "Limiting to 4000 characters";
        charLimitTextView.setText(txt);
        charLimitSlider.setValue(4000);
    }

    public void testCharLimitPressed(View view) {
        int charLimit = (int) charLimitSlider.getValue();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(R.layout.progress_dialog);
        builder.setCancelable(false);
        builder.setNegativeButton("Dismiss", (dialog, which) -> {
            dialog.dismiss();
            executor.shutdownNow(); // Stop the thread when tapped on dismiss
        });
        AlertDialog dialog = builder.create();
        // Show dialog before starting the test
        handler.post(dialog::show); // You can replace lambda function with method reference
        executor.execute(() -> {
            // Background work goes here
            doCharLimitTest(charLimit, dialog, this);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Initialize Objects
        tinyDB = new TinyDB(this);
        activity = this;
        // Find Views by id
        statusTextView = findViewById(R.id.statusTextView);
        threadCountTextView = findViewById(R.id.threadCountTextView);
        delegateToggleGroup = findViewById(R.id.delegateToggleButton);
        resetButton = findViewById(R.id.resetButton);
        charLimitSlider = findViewById(R.id.charLimitSlider);
        charLimitTextView = findViewById(R.id.charLimitTextView);
        testTimeTextView = findViewById(R.id.testTimeTextView);
        precisionIndicator = findViewById(R.id.precisionIndicator);
        // Method Calls
        updateToggleButtonGroup();
        setOnClickListeners();
        setThreadCountTextView();
        setCharLimit();
        setPrecision(); // Must be called AFTER charLimit
        updateStatusTextView(); // Status text view should be the LAST to be called

    }

    private void updateToggleButtonGroup() {
        String delegateStr = tinyDB.getString(Constants.getDelegateKey());
        switch (delegateStr) {
            default:
            case "0":
                delegateToggleGroup.check(R.id.cpuBtn);
                break;
            case "1":
                delegateToggleGroup.check(R.id.gpuBtn);
                break;
            case "2":
                delegateToggleGroup.check(R.id.nnapiBtn);
                break;
        }
    }
    private void updateStatusTextView() {
        String delegateStr = delegateToggleGroup.getCheckedButtonId() == R.id.cpuBtn ? "CPU" :
                (delegateToggleGroup.getCheckedButtonId() == R.id.gpuBtn ? "GPU" : "NNAPI");
        String status =  "Currently set to " + threadCountTextView.getText().toString()
                + " thread(s).\nUsing " + delegateStr + " as a delegate.";
        statusTextView.setText(status);
        setResetBtnState();
    }

    private void setResetBtnState() {
        if (!threadCountTextView.getText().toString().equals("2") ||
        delegateToggleGroup.getCheckedButtonId() != R.id.cpuBtn) {
            resetButton.setEnabled(true);
        } else {
            resetButton.setEnabled(false);
        }
    }

    private void setThreadCountTextView() {
        String threadsStr = tinyDB.getString(Constants.getThreadCountKey());
        if (threadsStr.trim().equals("")) threadsStr = "2";
        threadCountTextView.setText(threadsStr);
    }

    private void setOnClickListeners() {
        delegateToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                tinyDB.putString(Constants.getDelegateKey(), checkedId == R.id.cpuBtn ? "0" : (checkedId == R.id.gpuBtn ? "1" : "2"));
                updateStatusTextView();
            }
        });
        charLimitSlider.addOnChangeListener((slider, value, fromUser) -> {
            String txt = "Limiting to " + String.format("%.0f", value) + " characters";
            charLimitTextView.setText(txt);
            tinyDB.putString(Constants.getCharLimitKey(), String.format("%.0f", value));
            setPrecision();
        });
    }

    private void setCharLimit() {
        charLimit = tinyDB.getString(Constants.getCharLimitKey());
        charLimit = charLimit.equals("") ? "4000" : charLimit; // If not set by user, use default
        String txt = "Limiting to " + charLimit + " characters";
        charLimitTextView.setText(txt);
        charLimitSlider.setValue(Float.parseFloat(charLimit));
    }

    private void doCharLimitTest(int charLimit, AlertDialog dialog, Activity activity) {
        System.out.println("Started test thread");
        // Thread.sleep(5000);
        initializeBertQaHelper();
        // TODO: Get a proper passage without dialogs.
        String oliverTwist = Constants.getOliverTwist();
        String question = getString(R.string.oliver_twist_question);
        oliverTwist = oliverTwist.substring(0, charLimit); // Trim the passage to char limit
        bertQaHelper.answer(oliverTwist, question);
        System.out.println("Finished executing");
        activity.runOnUiThread(dialog::dismiss); // Dismiss the dialog upon completion
    }

    private void initializeBertQaHelper() {
        String threadCount = tinyDB.getString(Constants.getThreadCountKey());
        String delegate = tinyDB.getString(Constants.getDelegateKey());
        if (threadCount != null && !threadCount.isEmpty() && delegate != null && !delegate.isEmpty()) {
            bertQaHelper = new BertQaHelper(this, Integer.parseInt(threadCount),
                    Integer.parseInt(delegate), this);
        } else {
            bertQaHelper = new BertQaHelper(this, 2, 0, this);
        }
    }

    @Override
    public void onError(@NonNull String error) {
        activity.runOnUiThread(() -> Toast.makeText(activity, "Settings not supported!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onResults(List results, long inferenceTime) {
        activity.runOnUiThread(() -> {
            if (results == null) {
                String testTimeStr = "Test failed!";
                testTimeTextView.setText("Excessive load, try reducing the character limit");
                testTimeTextView.setText(testTimeStr);
            } else {
                String testTimeStr = "";
                testTimeTextView.setText("");
                if ((int) charLimitSlider.getValue() > 8000) {
                    testTimeStr = "Test succeeded and took " + inferenceTime + " milliseconds.";
                    testTimeStr += "\nCharacter limit is too high, expect precision loss.";
                    testTimeTextView.setText(testTimeStr);
                } else {
                    testTimeStr = "Test succeeded and took " + inferenceTime + " milliseconds.";
                    testTimeTextView.setText(testTimeStr);
                }
            }
        });
    }

    private void setPrecision() {
        int intCharLimit = Integer.parseInt(tinyDB.getString(Constants.getCharLimitKey()));
        int precisionLevel = -1;
        if (intCharLimit == 4000) { // default value
            precisionLevel = 5;
            precisionIndicator.setProgressCompat(80, true);
        } else if (intCharLimit <= 2000) { // lowest value
            precisionLevel = 7;
            precisionIndicator.setProgressCompat(95, true);
        } else if (intCharLimit < 4000) {
            precisionLevel = 6;
            precisionIndicator.setIndicatorColor(Color.parseColor("#44d28e"));
            precisionIndicator.setProgressCompat(85, true);
        } else if (intCharLimit <= 8000) {
            precisionLevel = 4;
            precisionIndicator.setProgressCompat(70, true);
        } else if (intCharLimit <= 15000) {
            precisionLevel = 3;
            precisionIndicator.setProgressCompat(50, true);
        } else if (intCharLimit <= 20000) {
            precisionLevel = 2;
            precisionIndicator.setProgressCompat(25, true);
        } else {
            precisionLevel = 1;
            precisionIndicator.setProgressCompat(15, true);
        }
        setPrecisionColor(precisionLevel);
    }

    private void setPrecisionColor(int precisionLevel) {
        if (DynamicColors.isDynamicColorAvailable()) {
            precisionIndicator.setIndicatorColor(
                    precisionLevel == 7 ? getResources().getColor(com.google.android.material.R.color.material_dynamic_primary30) :
                            precisionLevel == 6 ? getResources().getColor(com.google.android.material.R.color.material_dynamic_primary40) :
                                    precisionLevel == 5 ? getResources().getColor(com.google.android.material.R.color.material_dynamic_primary50) :
                                            precisionLevel == 4 ? getResources().getColor(com.google.android.material.R.color.material_dynamic_neutral_variant60) :
                                                    precisionLevel == 3 ? getResources().getColor(com.google.android.material.R.color.material_dynamic_neutral60) :
                                                            Color.parseColor("#B00020")
            );
        }
    }
}