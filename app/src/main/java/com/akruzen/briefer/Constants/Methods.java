package com.akruzen.briefer.Constants;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

public class Methods {

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void copyToClipBoard (String data, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("resultClipData", data);
        clipboard.setPrimaryClip(clipData);
    }

    public static int getFileType(Context context, Uri selectedFileUri) throws NullPointerException{
        ContentResolver contentResolver = context.getContentResolver();
        String mimeType = contentResolver.getType(selectedFileUri);
        String fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (fileExtension != null) {
            if (fileExtension.equalsIgnoreCase("pdf")) {
                return Constants.FILE_TYPE_PDF;
            } else if (fileExtension.equalsIgnoreCase("txt")) {
                return Constants.FILE_TYPE_TXT;
            }
            return Constants.FILE_TYPE_UNKNOWN;
        }
        throw new NullPointerException("File Extension is null");
    }

}
