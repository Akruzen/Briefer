package com.akruzen.briefer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.tensorflow.lite.task.text.qa.QaAnswer;

import java.util.ArrayList;
import java.util.List;

import Constants.Constants;
import Constants.Methods;

public class QuickChatActivity extends AppCompatActivity implements BertQaHelper.AnswererListener{

    TinyDB tinyDB;
    static List<String> titleList = new ArrayList<>();
    static int currTopicIndex = -3;
    // -3 means some error, -2 means text is shared, -1 means 'all topic' is selected, and 0 onwards is index of selected topic
    static boolean isExtraStringReceived = false;
    static String extraText = "";
    TextInputLayout quickAskTextInputLayout, topicTextInputLayout;
    TextInputEditText quickAskTextInputEditText;
    AutoCompleteTextView topicAutoCompleteTextView;
    MaterialButtonToggleGroup searchToggleGroup;
    TextView quickResultTextView, searchInTextView;
    MaterialButton quickAskButton;
    BertQaHelper bertQaHelper;

    public void quickAskButtonTapped(View view) {
        if (verifyChecks()) { // such as nothing is typed, etc.
            assert quickAskTextInputEditText.getText() != null; // Check already performed in text watcher
            String question = quickAskTextInputEditText.getText().toString();
            if (currTopicIndex == -1) { // Search in all topics
                // Implement logic for searching in all topics.
                Toast.makeText(this, "This feature is still work in progress!", Toast.LENGTH_SHORT).show();
            }
            else if (currTopicIndex == -2) {
                // If text is shared
                bertQaHelper.answer(extraText, question);
            }
            else if (currTopicIndex >= 0) { // Search in selected topic
                // The currTopicIndex will hold index of which title to search in
                String content = tinyDB.getListString(Constants.getContentListKey()).get(currTopicIndex);
                bertQaHelper.answer(content, question);
            }
            else {
                // Some values are missing, don't launch model
                Toast.makeText(this, "All fields are mandatory!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_chat);
        // Find view by ID
        quickAskTextInputLayout = findViewById(R.id.quickAskTextInputLayout);
        searchToggleGroup = findViewById(R.id.searchToggleButton);
        quickResultTextView = findViewById(R.id.quickResultTextView);
        quickAskButton = findViewById(R.id.quickAskBtn);
        searchInTextView = findViewById(R.id.searchInTextView);
        topicTextInputLayout = findViewById(R.id.topicTextInputLayout);
        topicAutoCompleteTextView = findViewById(R.id.topicAutoCompleteTextView);
        searchInTextView = findViewById(R.id.searchInTextView);
        quickAskTextInputEditText = findViewById(R.id.quickAskTextInputEditText);
        // Initialize Variables
        tinyDB = new TinyDB(this);
        titleList = tinyDB.getListString(Constants.getTitleListKey());
        // Method calls
        isExtraStringReceived = isExtraString();
        initialSetup();
        addOnClickListeners();
        initializeBertQaHelper();
    }

    private void initialSetup() {
        if (isExtraStringReceived) { // Directly ask questions on that
            searchToggleGroup.setVisibility(View.GONE);
            searchInTextView.setText("Searching in your shared text");
            currTopicIndex = -2; // It means that text is shared. Check is performed in on click of ask button
        } else {
            if (titleList.isEmpty()) {
                searchToggleGroup.setEnabled(false);
                quickAskTextInputLayout.setEnabled(false);
                quickAskButton.setEnabled(false);
                quickResultTextView.setText("No content found! Open the app to add some content");
            } else {
                // Populate the topic in drop down
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, titleList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                topicAutoCompleteTextView.setAdapter(adapter);
            }
            searchToggleGroup.check(R.id.selectedBtn);
        }
    }

    private void addOnClickListeners() {
        searchToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.selectedBtn) {
                    // Let the user decide which topic to select
                    quickAskTextInputEditText.setEnabled(false);
                    String currTopic = topicAutoCompleteTextView.getText().toString();
                    if (!currTopic.trim().equals("")) {
                        // Toggling to the segmented button retains drop down value, hence check is necessary
                        String setter = "Searching in \"" + currTopic + "\"";
                        searchInTextView.setText(setter);
                    }
                    topicTextInputLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "This feature is still work in progress!", Toast.LENGTH_SHORT).show();
                    currTopicIndex = -1; // -1 means all topics should be searched in
                    topicTextInputLayout.setVisibility(View.GONE);
                    searchInTextView.setText("This feature is still work in progress!");
                    quickAskTextInputEditText.setEnabled(false);
                }
            }
        });
        topicAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = "Searching in \"" + titleList.get(position) + "\""; // Sequence of dropdown will be same as the title list
            searchInTextView.setText(selected);
            currTopicIndex = position;
        });
        quickAskTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean shouldAskButtonBeActive = s.length() > 0 // Something should be present
                        && (!s.toString().trim().isEmpty()); // And it should not be empty
                quickAskButton.setEnabled(shouldAskButtonBeActive);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean verifyChecks() {
        boolean flag = true;
        if (searchToggleGroup.getCheckedButtonId() == R.id.selectedBtn) {
            if (topicAutoCompleteTextView.getText() == null ||
                    topicAutoCompleteTextView.getText().toString().trim().equals("")) {
                flag = false;
            }
            if (currTopicIndex < 0) flag = false; // It means user chose to select a topic but did not choose any
        }
        if (currTopicIndex == -3) flag = false; // Straight away disallow since -2 value does not mean anything, it's a default value
        // Add more conditions here if needed
        return flag;
    }

    @Override
    public void onError(@NonNull String error) {
        Log.i("Explicit Error", error);
    }

    @Override
    public void onResults(List results, long inferenceTime) {
        // Maintain the arguments here as it is since it ensures compatible argument types with Kotlin and Java both
        if (results != null && !results.isEmpty()) {
            // Answer was generated successfully, hide the keyboard
            Methods.hideKeyboard(this);
            // Toast.makeText(this, "Generation Success", Toast.LENGTH_SHORT).show();
            StringBuilder text = new StringBuilder("Select the result text to copy.<br><br>");
            for (int i = 0; i < results.size(); i++) {
                if (i > 5) break; // Display only top 5 results
                QaAnswer answer = (QaAnswer) results.get(i);
                // Remove blank spaces, new lines and carriage returns from the answer
                String ans = answer.text.trim().replace("\n", " ").replace("\r", " ");
                // Don't add serial number if only one result
                text.append(results.size() > 1 ? "<b>" + (i + 1) + ") " + "</b>" + ans + "<br>" : answer.text + "<br>");
            }
            // Convert the syntax into HTML before setting
            quickResultTextView.setText(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT));
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

    private boolean isExtraString() {
        // This method checks if quick chat was called by selecting/sharing the text
        boolean flag = false;
        try {
            // This is for getting the text from the three dot action menu
            CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            if (text != null && !text.toString().trim().equals("")) {
                if (text.toString().trim().length() < 300) {
                    Toast.makeText(this, "Content should have at least 300 characters.", Toast.LENGTH_SHORT).show();
                    finish(); // Do not process text less than 300 characters
                } else {
                    flag = true;
                    extraText = text.toString().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!flag) { // If no three dot menu text is found, then try receiving from share sheet
            try {
                Intent receivedIntent = getIntent();
                String receivedAction = receivedIntent.getAction();
                String receivedType = receivedIntent.getType();
                if (receivedAction.equals(Intent.ACTION_SEND) && receivedType.startsWith("text/")) {
                    String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                    if (!receivedText.trim().equals("")) {
                        flag = true;
                        extraText = receivedText;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

}