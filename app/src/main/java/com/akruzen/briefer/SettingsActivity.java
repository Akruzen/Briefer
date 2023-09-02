package com.akruzen.briefer;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import Constants.Constants;

public class SettingsActivity extends AppCompatActivity {

    TextView threadCountTextView, statusTextView;
    TinyDB tinyDB;
    MaterialButtonToggleGroup delegateToggleGroup;
    MaterialButton resetButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Initialize Objects
        tinyDB = new TinyDB(this);
        // Find Views by id
        statusTextView = findViewById(R.id.statusTextView);
        threadCountTextView = findViewById(R.id.threadCountTextView);
        delegateToggleGroup = findViewById(R.id.delegateToggleButton);
        resetButton = findViewById(R.id.resetButton);
        // Method Calls
        updateToggleButtonGroup();
        setOnClickListeners();
        setThreadCountTextView();
        updateStatusTextView(); // Status text view should be the last to be called

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
        threadCountTextView.setText(threadsStr);
    }

    private void setOnClickListeners() {
        delegateToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                tinyDB.putString(Constants.getDelegateKey(), checkedId == R.id.cpuBtn ? "0" : (checkedId == R.id.gpuBtn ? "1" : "2"));
                updateStatusTextView();
            }
        });
    }
}