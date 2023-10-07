package com.akruzen.briefer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akruzen.briefer.Constants.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddContentFileActivity extends AppCompatActivity {

    Uri selectedFileUri;
    DocumentFile documentFile;
    ContentResolver contentResolver;
    InputStream inputStream;
    PdfReader reader;
    TinyDB tinyDB;
    String fileExtension, fileName;
    TextInputEditText questionFileTextInputEditText;
    TextView fileNameTV, fileTypeTV, pagesTV, totalCharsTV, splittingTV;
    ConstraintLayout constraintLayout;
    MaterialButton fileTypeButton;
    MaterialCardView splittingCardView;

    public void saveFABClicked(View view) {
        // Save the file
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content_file);
        // Find view by id
        questionFileTextInputEditText = findViewById(R.id.questionFileTextInput);
        fileTypeButton = findViewById(R.id.fileTypeButton);
        fileNameTV = findViewById(R.id.fileNameTV);
        fileTypeTV = findViewById(R.id.fileTypeTV);
        pagesTV = findViewById(R.id.pagesTV);
        totalCharsTV = findViewById(R.id.totalCharsTV);
        splittingTV = findViewById(R.id.splittingTV);
        constraintLayout = findViewById(R.id.addContentFileActivityConstraintLayout);
        splittingCardView = findViewById(R.id.splittingCardView);
        // Object creation
        tinyDB = new TinyDB(this);
        // Method Calls
        getIntentExtras();
        setTexts();
    }

    private void getIntentExtras() {
        selectedFileUri = Objects.requireNonNull(getIntent().getExtras()).getParcelable(Constants.FILE_INTENT_EXTRA);
        fileExtension = Objects.requireNonNull(getIntent().getExtras()).getString(Constants.FILE_EXTENSION_INTENT_EXTRA);
    }

    private void setTexts() {
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
            try {
                StringBuilder extractedText = new StringBuilder();
                reader = setPdfReader();
                int pageCount = reader.getNumberOfPages();
                for (int i = 0; i < pageCount; i++) {
                    // to extract the PDF content from the different pages
                    extractedText.append(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()).append("\n");
                }
                int charCount = extractedText.toString().trim().length();
                documentFile = DocumentFile.fromSingleUri(this, selectedFileUri);
                fileName = documentFile.getName();
                runOnUiThread(() -> {
                    fileTypeButton.setText(fileExtension);
                    pagesTV.setText("Pages: " + pageCount);
                    totalCharsTV.setText("Total Characters: " + charCount);
                    if (charCount == 0) {
                        Snackbar s = Snackbar.make(constraintLayout, getString(R.string.incorrect_file_warning), Snackbar.LENGTH_INDEFINITE);
                        s.setAction("Got it", view -> s.dismiss());
                        s.setTextMaxLines(6);
                        s.show();
                    }
                    String charLimitStr = tinyDB.getString(Constants.getCharLimitKey());
                    int charLimit = charLimitStr == "" ? Integer.parseInt(Constants.DEFAULT_CHAR_LIMIT) : Integer.parseInt(charLimitStr);
                    if (charCount > charLimit) {
                        splittingTV.setText("Splitting: " + (int)(Math.ceil(charCount / charLimit)) + " parts");
                        splittingCardView.setVisibility(View.VISIBLE);
                    } else {
                        splittingTV.setText("Splitting: No splitting required");
                    }
                    questionFileTextInputEditText.setText(fileName);
                    fileNameTV.setText("Filename: " + fileName);
                    fileTypeTV.setText("File Type: " + documentFile.getType());
                });
                runOnUiThread(dialog::dismiss); // Dismiss the dialog upon completion
            } catch (NullPointerException e) {
                runOnUiThread(() -> Toast.makeText(this, "One or more attributes of file are null", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Error reading the PDF", Toast.LENGTH_SHORT).show());
                throw new RuntimeException(e);
            }

        });
    }

    private PdfReader setPdfReader() throws IOException {
        try {
            contentResolver = getContentResolver();
            inputStream = contentResolver.openInputStream(selectedFileUri);
            if (inputStream != null) {
                return new PdfReader(inputStream);
            } else {
                // Handle case where inputStream is null
                // This can happen if the document couldn't be opened
                Toast.makeText(this, "Could not open the document to read contents", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

}