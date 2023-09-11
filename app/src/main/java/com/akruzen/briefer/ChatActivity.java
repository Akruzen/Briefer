/*
* Code developed by Akruzen (Omkar Phadke)
* Connect with me on Github via https://github.com/Akruzen
* */

package com.akruzen.briefer;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.task.text.qa.QaAnswer;

import java.util.List;

import Constants.Constants;
import Constants.Methods;

public class ChatActivity extends AppCompatActivity implements BertQaHelper.AnswererListener {

    TextView titleTextView, contentTextView, resultTextView, noteTextView;
    TextInputEditText questionEditText;
    MaterialButton askButton;
    BertQaHelper bertQaHelper;
    TinyDB tinyDB;

    public void askButtonTapped(View view) {
        assert questionEditText.getText() != null; // Check already performed in text watcher
        String question = questionEditText.getText().toString();
        String content = contentTextView.getText().toString();
        bertQaHelper.answer(content, question);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Find Views by id
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        questionEditText = findViewById(R.id.questionTextInput);
        resultTextView = findViewById(R.id.resultTextView);
        askButton = findViewById(R.id.askButton);
        noteTextView = findViewById(R.id.noteTextView);
        // Initialize Objects
        tinyDB = new TinyDB(this);
        initializeBertQaHelper(); // initialize bertQaHelper
        // bertQaHelper.initialize();
        // Method Calls
        setContentTextView();
        handleListeners();
    }

    private void setContentTextView() {
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String delegateStr = tinyDB.getString(Constants.getDelegateKey());
        titleTextView.setText(title);
        contentTextView.setText(content);
        String threadsCount = tinyDB.getString(Constants.getThreadCountKey());
        threadsCount = threadsCount.trim().equals("") ? "2" : threadsCount;
        delegateStr = (delegateStr.equals("2") ? "NNAPI" : (delegateStr.equals("1") ? "GPU" : "CPU"));
        String setting = " Running " + threadsCount + " thread(s) on " + delegateStr;
        // Only mention thread and delegate count if default value is changed
        String note = "Note: Results might not be accurate."
                + ((threadsCount.equals("2") && delegateStr.equals("CPU")) ? "" : setting);
        noteTextView.setText(note);
    }

    private void handleListeners() {
        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean shouldAskButtonBeActive = s.length() > 0 // Something should be present
                        && (!s.toString().trim().isEmpty()); // And it should not be empty
                askButton.setEnabled(shouldAskButtonBeActive);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    @Override
    public void onError(@NonNull String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(List results, long inferenceTime) {
        // Maintain the arguments here as it is since it ensures compatible argument types with Kotlin and Java both
        if (results != null && !results.isEmpty()) {
            // Answer was generated successfully, hide the keyboard
            Methods.hideKeyboard(this);
            // Toast.makeText(this, "Generation Success", Toast.LENGTH_SHORT).show();
            StringBuilder text = new StringBuilder("<br>");
            for (int i = 0; i < results.size(); i++) {
                if (i > 5) break; // Display only top 5 results
                QaAnswer answer = (QaAnswer) results.get(i);
                // Remove blank spaces, new lines and carriage returns from the answer
                String ans = answer.text.trim().replace("\n", " ").replace("\r", " ");
                // Don't add serial number if only one result, make serial number bold
                text.append(results.size() > 1 ? "<b>" + (i + 1) + ") " + "</b>" + ans + "<br>" : answer.text + "<br>");
            }
            resultTextView.setText(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            Toast.makeText(this, "Null result!", Toast.LENGTH_SHORT).show();
        }
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
}