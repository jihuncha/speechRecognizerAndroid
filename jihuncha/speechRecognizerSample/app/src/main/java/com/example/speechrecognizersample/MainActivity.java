package com.example.speechrecognizersample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button btSpeechStart;
    private Button btSpeechStop;
    private TextView tvSpeechResult;

    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;

    private String temporaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);

        initView();
        makeSpeechIntent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    public void initView() {
        btSpeechStart = (Button) findViewById(R.id.bt_speech_start);
        btSpeechStop = (Button) findViewById(R.id.bt_speech_stop);
        tvSpeechResult = (TextView) findViewById(R.id.tv_speech_result);

        btSpeechStart.setOnClickListener(this);
        btSpeechStop.setOnClickListener(this);
    }

    public void makeSpeechIntent() {
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //TODO MODEL_FREE_FORM 과 WEB_SEARCH 의 차이점을 모르겠음...
        //TODO 음성 인식 결과를 웹 검색에 쓰려면 LANGUAGE_MODEL_WEB_SEARCH, 그외에 용도에는 LANGUAGE_MODEL_FREE_FORM??
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //음성인식기에 추가되는 key?
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        //언어지정
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR");
        //TODO 결과값에 대한 max 값인데 잘 동작하지 않는다..
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        //1. EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS
        //-> 음성 입력을 멈춘 후 완료했다고 인식하는데 까지 걸리는 시간.
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000000);

        //2. EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS
        //-> 발화의 최소 시간(?)
        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 100000);

        //3. EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS
        //-> 음성 입력을 멈춘 후 완료했다고 인식하는데 까지 걸리는 시간.
//        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 100000);

    }

    //SpeechRecognizer implements method
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "onError - " + error);
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                tvSpeechResult.setText("오디오 에러");
                break;

            case SpeechRecognizer.ERROR_CLIENT:
                tvSpeechResult.setText("클라이언트 에러");
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                tvSpeechResult.setText("퍼미션없음");
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                tvSpeechResult.setText("네트워크 에러");
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                tvSpeechResult.setText("네트웍 타임아웃");
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                tvSpeechResult.setText("적당한 결과를 찾지 못하였음");
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                tvSpeechResult.setText("Recognizer Busy");
                break;

            case SpeechRecognizer.ERROR_SERVER:
                tvSpeechResult.setText("서버이상");
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                tvSpeechResult.setText("말하는 시간초과");
                break;

            default:
                tvSpeechResult.setText("알수없음");
                break;
        }

    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults - " + results.toString());
        ArrayList<String> resultAllSpeech = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        //TODO 결과값은 0번쨰가 가장 확률이 높은것
        StringBuilder resultText = new StringBuilder();
        Log.d(TAG, "result Size : " + resultAllSpeech.size());
        for(int i = 0; i < resultAllSpeech.size() ; i++){
            Log.w(TAG,  "result - "+ resultAllSpeech.get(i));
            resultText.append(resultAllSpeech.get(i) + "\n");
        }

        Log.d(TAG, "predict : " + resultAllSpeech.get(0));
        tvSpeechResult.setText(resultAllSpeech.get(0));

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent");
    }


    //onclick implements method
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_speech_start :
                Log.d(TAG, "onClick - bt_speech_start");
                speechRecognizer.startListening(speechIntent);
                break;

            case R.id.bt_speech_stop :
                Log.d(TAG, "onClick - bt_speech_stop");
                speechRecognizer.stopListening();
                break;

        }
    }

}
