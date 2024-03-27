package com.zc.medical_ai;

import android.os.health.SystemHealthManager;

import java.util.HashMap;
import java.util.Map;

public class questionnaireAnswers {
    private Map<Integer, String> answerMap = new HashMap<Integer, String>();
    private String date;
    public questionnaireAnswers() {
    }

    public String getQuestionnaireAnswer(int i){
        return answerMap.get(i);
    }

    public void setQuestionnaireAnswer(int question, String answer){
        if(answerMap != null)
            this.answerMap.put(question, answer);
        else
            System.out.println("answermap is null");
    }

    public int getSize(){
        return answerMap.size();
    }

    public void setDate(String d){
        date = d;
    }

    public String getDate(){
        return date;
    }
}
