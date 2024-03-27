package com.zc.medical_ai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;

public class questionnaireEntry extends AppCompatActivity {

    private questionService questions;
    private dataStorage answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questionnaire_entry);

        TableLayout tableLayout = findViewById(R.id.tableLayout);

//        super.onCreate(savedInstanceState);
        try {
            questions = questionService.getInstance(this);
            answers = dataStorage.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(answers != null){
            for(int i = 0; i < answers.getEntry(answers.getIndex()).getSize(); i++) {
                tableLayout.addView(makeNewRow(questions.getQuestion(i), answers.getEntry(answers.getIndex()).getQuestionnaireAnswer(i)));
            }
        }
    }

    private View makeNewRow(String q, String a){
        TableRow tableRow = new TableRow(this);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tableRow.setOrientation(TableLayout.HORIZONTAL);
        tableRow.setGravity(Gravity.LEFT);

        TextView questionTextView = new TextView(this);

        questionTextView.setText(q);
        questionTextView.setLayoutParams(new TableRow.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                .5f));

        TextView answerTextView = new TextView(this);

        answerTextView.setText(a);
        answerTextView.setLayoutParams(new TableRow.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                .5f));

        tableRow.addView(questionTextView);
        tableRow.addView(answerTextView);

        return tableRow;
    }
}