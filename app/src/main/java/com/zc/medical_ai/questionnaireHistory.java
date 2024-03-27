package com.zc.medical_ai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import android.os.Bundle;

import java.io.IOException;

public class questionnaireHistory extends AppCompatActivity {
    private LinearLayout linearLayout;
    private questionService questions;
    private dataStorage answers;
//    questionnaireEntry entryFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_history);

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);


        try {
            questions = questionService.getInstance(this);
            answers = dataStorage.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("is answers null? " + answers);
        if(answers != null){
            System.out.println("not null");
            for(int i = 0; i < answers.getSize(); i++ ) {
                System.out.println("making new button " + i + " " + answers.getEntry(i).getDate());
                makeNewRow("entry " + answers.getEntry(i).getDate(), i);
            }
        }

//        entryFormat = new questionnaireEntry();
    }

    private void makeNewRow(String s, int entryIndex){
        LinearLayout newRow = new LinearLayout(this);
        newRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        newRow.setOrientation(LinearLayout.HORIZONTAL);
        newRow.setGravity(Gravity.CENTER);

        Button buttonView = new Button(this);
        buttonView.setText(s);
        buttonView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        buttonView.setTag(entryIndex);

        newRow.addView(buttonView);

        buttonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                answers.setIndex((Integer)v.getTag());
                Intent intent = new Intent(questionnaireHistory.this, questionnaireEntry.class);
                startActivity(intent);
            }
        });
        System.out.println("making new row");
        linearLayout.addView(newRow);
    }
}