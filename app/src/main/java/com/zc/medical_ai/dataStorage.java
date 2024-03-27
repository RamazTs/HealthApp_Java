package com.zc.medical_ai;
import java.util.*;

public class dataStorage {
    // Private constructor to prevent external instantiation
    private dataStorage() {
    }

    // Private static instance variable
    private String date;
    private Map<Integer, String> answerMap = new HashMap<Integer, String>();

    private Map<Integer, questionnaireAnswers> entryMap = new HashMap<Integer, questionnaireAnswers>();
    public static dataStorage instance;
    private int index;

    // Public static method to access the instance
    public static dataStorage getInstance() {
        if (instance == null) {
            instance = new dataStorage();
        }
        return instance;
    }
    private Void makeNewEntry(){
        questionnaireAnswers entry = new questionnaireAnswers();
        entryMap.put(entryMap.size(), entry);
        return null;
    }

    public questionnaireAnswers getEntry(int i){
        if(entryMap.size() >= 0) {
            System.out.println("size" + entryMap.size());
            return entryMap.get(i);
        }
        else
            return null;
    }

    // Other methods and properties of the Singleton class
    public void storeAns(int i, String ans) {
        if(entryMap != null) {
            System.out.println(entryMap.size());
            getEntry(entryMap.size() - 1).setQuestionnaireAnswer(i, ans);
        }else
            System.out.println("entryMap is null");
    }

    public String getAnswer(int index){
        return getEntry(entryMap.size()).getQuestionnaireAnswer(index);
    }

    public int getSize() {
        return entryMap.size();
    }

    public  void storeDate(String d){
        makeNewEntry();
        getEntry(entryMap.size() - 1).setDate(d);
    }
    public void setIndex(int i){
        index = i;
    }

    public int getIndex(){
        return index;
    }
//    public String getDate() { return date;}
}