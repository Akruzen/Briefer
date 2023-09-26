package com.akruzen.briefer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AboutActivity extends AppCompatActivity {

    TextView versionNameTextView;

    public void contactPressed(View view) {
        String uriString = "https://www.linkedin.com/in/omkar-phadke-b97b741ba/";
        if (view.getId() == R.id.githubButton) {
            uriString = "https://github.com/Akruzen";
        } else if (view.getId() == R.id.discordButton) {
            uriString = "https://discordapp.com/users/akruzen#2652";
        } else if (view.getId() == R.id.sourceCodeButton) {
            uriString = "https://github.com/Akruzen/Briefer";
        }
        // Else default behaviour will be to open linked in
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
    }

    public void changeLogPressed(View view) {
        showChangeLogBottomSheet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // Find views by id
        versionNameTextView = findViewById(R.id.versionNameTextView);
        // Method Calls
        setUpTextViews();
    }

    private void setUpTextViews() {
        String version = "Version: Unknown";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = "Version: " + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            versionNameTextView.setText(version);
        }
    }

    private void showChangeLogBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.changelog_bottom_sheet);
        bottomSheetDialog.show();
    }
}