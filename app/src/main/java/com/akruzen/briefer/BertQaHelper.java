package com.akruzen.briefer;

import android.content.Context;
import android.os.SystemClock;

import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.core.ComputeSettings;
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer;
import org.tensorflow.lite.task.text.qa.QaAnswer;
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions.Builder;

import java.util.List;

public class BertQaHelper {

    // Define class variables
    private Context context;
    private int numThreads;
    private int currentDelegate;
    // Create object of interface
    private AnswererListener answererListener;
    private BertQuestionAnswerer bertQuestionAnswerer;

    public static final int DELEGATE_CPU = 0;
    public static final int DELEGATE_GPU = 1;
    public static final int DELEGATE_NNAPI = 2;
    private static final String BERT_QA_MODEL = "mobilebert.tflite";
    private static final String TAG = "BertQaHelper";

    // Class constructor
    public BertQaHelper(Context context, int numThreads, int currentDelegate ,AnswererListener answererListener) {
        this.context = context;
        this.answererListener = answererListener;
        this.numThreads = numThreads;
        this.currentDelegate = currentDelegate;
        setupBertQuestionAnswerer();
    }

    // Constructor Overloading
    public BertQaHelper(Context context, AnswererListener answererListener) {
        this(context, 2, 0, answererListener);
    }

    public void clearBertQuestionAnswerer() {
        bertQuestionAnswerer = null;
    }

    private void setupBertQuestionAnswerer() {
        BaseOptions.Builder baseOptionsBuilder = new BaseOptions.Builder() {
            @Override
            public BaseOptions.Builder setComputeSettings(ComputeSettings computeSettings) {
                return null;
            }

            @Override
            public BaseOptions.Builder setNumThreads(int numThreads) {
                return null;
            }

            @Override
            public BaseOptions build() {
                return null;
            }
        }.setNumThreads(numThreads);

        switch (currentDelegate) {
            case DELEGATE_CPU:
                // Default
                break;
            case DELEGATE_GPU:
                CompatibilityList compatibilityList = new CompatibilityList();
                if (compatibilityList.isDelegateSupportedOnThisDevice()) {
                    baseOptionsBuilder.useGpu();
                } else {
                    if (answererListener != null) {
                        answererListener.onError("GPU is not supported on this device");
                    }
                }
                break;
            case DELEGATE_NNAPI:
                baseOptionsBuilder.useNnapi();
                break;
        }

        BertQuestionAnswerer.BertQuestionAnswererOptions options = new Builder() {
            @Override
            public Builder setBaseOptions(BaseOptions baseOptions) {
                return null;
            }

            @Override
            public BertQuestionAnswerer.BertQuestionAnswererOptions build() {
                return null;
            }
        }
                .setBaseOptions(baseOptionsBuilder.build())
                .build();

    }

    public void answer(String contextOfQuestion, String question) {
        if (bertQuestionAnswerer == null) {
            setupBertQuestionAnswerer();
        }

        // Inference time is the difference between the system time at the start and finish of the
        // process
        long inferenceTime = SystemClock.uptimeMillis();

        List<QaAnswer> answers = bertQuestionAnswerer != null ? bertQuestionAnswerer.answer(contextOfQuestion, question) : null;
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime;
        if (answererListener != null) {
            answererListener.onResults(answers, inferenceTime);
        }
    }

    // Define interface AnswererListener
    interface AnswererListener {
        void onError(String error);
        void onResults(List<QaAnswer> results, long inferenceTime);
    }
}
