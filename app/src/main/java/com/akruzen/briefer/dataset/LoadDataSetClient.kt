/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensorflow.lite.examples.bertqa.dataset

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.akruzen.briefer.dataset.DataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class LoadDataSetClient(private val context: Context) {

    private companion object {
        private const val TAG = "BertAppDemo"
        private const val JSON_DIR = "qa.json"
    }

    // Load json file into a data object.
    fun loadJson(): DataSet? {
        var dataSet: DataSet? = null
        try {
            val inputStream: InputStream = context.assets.open(JSON_DIR)
            val bufferReader = inputStream.bufferedReader()
            val stringJson: String = bufferReader.use { it.readText() }
            val datasetType = object : TypeToken<DataSet>() {}.type
            dataSet = Gson().fromJson(stringJson, datasetType)
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
        }

        // saveFileToPublicMusic(context, JSON_DIR);

        return dataSet
    }

    /*// Function to save the file in the public music directory
    private fun saveFileToPublicMusic(context: Context, fileName: String) {
        // Get the file from the app's private mode
        val privateFile = File(context.filesDir, fileName)

        // Check if the file exists and can be read
        if (!privateFile.exists() || !privateFile.canRead()) {
            // File doesn't exist or cannot be read, handle the error accordingly
            return
        }

        // Get the public music directory
        val publicMusicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)

        // Create a new file in the public music directory
        val publicFile = File(publicMusicDirectory, fileName)

        // Copy the content from the private file to the public file
        try {
            FileInputStream(privateFile).use { inputStream ->
                FileOutputStream(publicFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception as needed
        }
        Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show()
    }*/

}

/*
package com.akruzen.briefer.dataset

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.examples.bertqa.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class LoadDataSetClient(private val context: Context) {

    companion object {
        private const val TAG = "BertAppDemo"
        private const val JSON_DIR = "qa2.json"
    }

    private lateinit var publicMusicDirectory: File

    init {
        publicMusicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    }

    // Load json file into a data object.
    fun loadJson(): DataSet? {

        // saveJsonDataInPrivateMode(context, jsonData, JSON_DIR)

        var dataSet: DataSet? = null
        try {
            */
/*val inputStream: InputStream = context.assets.open(JSON_DIR)
            val bufferReader = inputStream.bufferedReader()*//*

            // val stringJson: String = bufferReader.use { it.readText() }
            val stringJson = getQueryTextFromSharedPreferences(context, "query") ?: "{\"contents\":[[\"Dear UPI user A/C X4451 debited by 250.0 on date 23Jul23 trf to Vishwanath Watch Refno 320435952225. If not u? call 1800111109. -SBI\"]],\"questions\":[[\"Is it debit or credit?\"]],\"titles\":[[\"Bank Payment\"]]}"
            val datasetType = object : TypeToken<DataSet>() {}.type
            dataSet = Gson().fromJson(stringJson, datasetType)
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
        }

        saveJsonToPublicMusic(dataSet)

        return dataSet
    }

    fun saveJsonDataInPrivateMode(context: Context, jsonData: String, fileName: String) {
        try {
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(jsonData.toByteArray())
            fileOutputStream.close()
            Toast.makeText(context, "New JSON data saved privately", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to save New JSON data privately", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            // Handle the exception as needed
        }
    }


    // Save json file into the public music directory.
    private fun saveJsonToPublicMusic(dataSet: DataSet?) {
        try {
            val jsonFile = File(publicMusicDirectory, JSON_DIR)

            val jsonString = Gson().toJson(dataSet)

            FileOutputStream(jsonFile).use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }

            Toast.makeText(context, "JSON data saved to public music directory", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
            Toast.makeText(context, "Error saving JSON data", Toast.LENGTH_SHORT).show()
        }
    }

    // Retrieve the query text from shared preferences
    private fun getQueryTextFromSharedPreferences(context: Context, queryTextKey : String): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val retStr = sharedPreferences.getString(queryTextKey, "")
        Toast.makeText(context, "Loaded from Shared Preferences", Toast.LENGTH_LONG).show()
        println("Inside what is loaded: $retStr")
        return retStr
    }

}
*/
