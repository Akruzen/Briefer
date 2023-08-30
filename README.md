
# Briefer

An Android application based on TensorFlow's BERT to perform NLP operations on runtime contents.

Google released [BERT](<https://en.wikipedia.org/wiki/BERT_(language_model)>) (Bidirectional Encoder Representations from Transformers) in 2018 aiming for high performance lightweight Natural Language Processing for processors having limited resources such as those of smartphones. In 2022, Google launched a [demo application](<https://github.com/tensorflow/examples/tree/master/lite/examples/bert_qa/android>) on GitHub for showcasing BERT's usage on Android smartphones. However, the app was made in Kotlin and had a limited functionality. Hence I decided to use the same BERT technology used in the app but make it more user friendly and improve the features to make the app actually usable.

## Features
- Dynamically add/delete new topic of interest
- View past topics
- No need of internet connection
- Follows Google's latest Material You guidelines with dynamic color themeing


## Screenshots

About App             |  Adding content  |  List of previous topics  |  Asking questions
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
![](https://github.com/Akruzen/Briefer/blob/master/app/docs/SS0.png)  |  ![](https://github.com/Akruzen/Briefer/blob/master/app/docs/SS1.png)  |  ![](https://github.com/Akruzen/Briefer/blob/master/app/docs/SS2.png)  |  ![](https://github.com/Akruzen/Briefer/blob/master/app/docs/SS3.png)

## Installation

#### For common users:
To directly install the apk on your phone, you can check current releases and download the apk file from [release section](<https://github.com/Akruzen/Briefer/releases>).

Points to consider while installing:
- Make sure you are using Android Oreo (8.0) or above
- The APK file size is drastically large (around 175 MB) since the BERT language model is included inside the APK file which is around 100 MB.

#### For developers:
To open and run the Android Application, you will need to install [Android Studio](<https://developer.android.com/studio>) or any other supported IDE.
Then clone this repository using

```bash
  git clone https://github.com/Akruzen/Briefer
```

This project makes use of both Java and Kotlin for its execution. Hence, if you intend to use this in your own project, make sure you install Kotlin dependency:

```bash
  implementation 'androidx.core:core-ktx:1.9.0'
  implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.9.0'
```
Also, you will need to add Tensorflow Lite libraries in your build.gradle file:
```bash
  implementation 'org.tensorflow:tensorflow-lite-task-text:0.4.4'
  implementation 'org.tensorflow:tensorflow-lite-gpu-delegate-plugin:0.4.4'
  implementation 'org.tensorflow:tensorflow-lite-gpu:2.13.0'
```
## Feedback

If you have any feedback, you can reach me on [Discord](<https://discordapp.com/users/akruzen#2652>).
