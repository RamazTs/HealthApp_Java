package com.zc.medical_ai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import speech.GoogleVoiceTypingDisabledException;
import speech.Logger;
import speech.Speech;
import speech.SpeechDelegate;
import speech.SpeechRecognitionNotAvailable;
import speech.SpeechUtil;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import ch.qos.logback.core.net.SyslogOutputStream;

public class MainActivity2 extends AppCompatActivity implements SpeechDelegate {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> questionList = new ArrayList<String>();
    private dataStorage storage = dataStorage.getInstance();
    private questionService questions;
    private TextView questionText;
    Speech speech;
    private ImageButton recButton;
    private final int PERMISSIONS_REQUEST = 1;
    private int curIndex = 0;
//    multipleChoiceQuestion multipleChoiceFormat;
//    freeformQuestion freeformFormat;
    //boolean ttsDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        questionText = (TextView)findViewById(R.id.questionView);
        recButton = (ImageButton)findViewById(R.id.recButton);
        recButton.setOnClickListener(view -> onButtonPress());

        speech = Speech.init(this);

//        multipleChoiceFormat = new multipleChoiceQuestion();
//        freeformFormat = new freeformQuestion();

        if(this.getApplicationContext() != null) {
            System.out.println("context is not null");
            try {
                questions = questionService.getInstance(this.getApplicationContext());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            tts = ttsService.getInstance(this.getApplicationContext());
//            questions = questionService.getInstance(this.getApplicationContext());
        }
        storage.storeDate(getCurrentTimestamp());

        askQuestion(questions.getQuestion(curIndex));

    }

    private void askQuestion(String q){
//        System.out.println("comparing " + questions.getQuestionType(curIndex) + " to multiple choice");
//        System.out.println(questions.getQuestionType(curIndex) == "multiple choice");
//        System.out.println(questions.getQuestionType(curIndex).equals("multiple choice"));

//        if (questions.getQuestionType(curIndex).equals("multiple choice")){
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, multipleChoiceFormat);
//            transaction.addToBackStack(null); // Optional: add to back stack
//            transaction.commit();
//
//        }else if (questions.getQuestionType(curIndex).equals("freeform")) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, freeformFormat);
//            transaction.addToBackStack(null); // Optional: add to back stack
//            transaction.commit();

        speech.say(q);
        questionText.setText(q);
    }

    private void storeAnswer(String ans){
        storage.storeAns(curIndex, ans);
        curIndex++;
        if(curIndex == questions.getMapSize()){
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        }
        askQuestion(questions.getQuestion(curIndex));
    }

    public String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
                .getInstance().getTime());
    }

    private void onButtonPress(){
        recordAudio();
    }

    private void recordAudio(){
        System.out.println("in recordAudio()");
        //while(ttsDone) {
        if (Speech.getInstance().isListening()) {
            System.out.println("stopped listening");
            Speech.getInstance().stopListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("onrecoraudiopermissiongranted()");
                onRecordAudioPermissionGranted();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST);
            }
        }
        //}
    }

    public void onStartOfSpeech() {
    }
    public void onSpeechRmsChanged(float value) {
        //Log.d(getClass().getSimpleName(), "Speech recognition rms is now " + value +  "dB");
    }
    public void onSpeechPartialResults(List<String> results) {
//        text.setText("");
//        for (String partial : results) {
//            text.append(partial + " ");
//        }
    }
    public void onSpeechResult(String result) {

        storeAnswer(result);
    }

    private void onRecordAudioPermissionGranted() {
        System.out.println("in onRecordAudioPermissionGranted()");
        try {
            System.out.println("trying stoptexttospeech");
            Speech.getInstance().stopTextToSpeech();
            System.out.println("success!");
            System.out.println("trying startlistening");
            Speech.getInstance().startListening(MainActivity2.this);
            System.out.println("success!");

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(MainActivity2.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }
}