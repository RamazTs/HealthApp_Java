package com.zc.medical_ai;
import java.util.*;
import android.content.Context;
import java.util.Scanner;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class questionService {
    // Private constructor to prevent external instantiation
    private questionService(Context context) throws IOException {
        main(context);
    }

    // Private static instance variable
    private Map<Integer, String> questionMap = new HashMap<Integer, String>();
    private Map<Integer, String> questionTypes = new HashMap<Integer, String>();
    public static questionService instance;

    // Public static method to access the instance
    public static questionService getInstance(Context context) throws IOException {
        if (instance == null) {
            instance = new questionService(context);
        }
        return instance;
    }

    // Other methods and properties of the Singleton class
    public void main(Context context) throws IOException {
//        questionMap.put(0, "In total, how long did you sleep?");
//        questionMap.put(1, "At what time did you go to bed?");
//        questionMap.put(2, "At what time did you try to go to sleep?");
//        questionMap.put(3, "How long did it take you to fall asleep?");
//        questionMap.put(4, "How many times did you wake up, not counting your final awakening?");
//        questionMap.put(5, "In total, how long did these awakenings last?");
//        questionMap.put(6, "At what time was your final awakening?");
//        questionMap.put(7, "Did you wake up earlier than you desired?");
//        questionMap.put(8, "If yes, how many minutes?");
//        questionMap.put(9, "At what time did you get out of bed for the day?");
//        questionMap.put(10, "How would you rate the quality of your sleep?");

//        StringBuilder stringBuilder = new StringBuilder();

        try {
            int i = 0;
            InputStream inputStream = context.getAssets().open("questions.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Create a regex pattern to match text between start and end delimiters
                String regex = "\\[(.*?)\\] \\[(.*?)\\]";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                System.out.println(matcher);

                while (matcher.find()) {// Group 1 contains the text between delimiters
                    questionTypes.put(i, matcher.group(1));
                    questionMap.put(i, matcher.group(2));
//                    System.out.println("putting in " + matchedText);
//                    stringBuilder.append(line).append("\n");
                    i++;
                }
            }
            System.out.println(questionMap.size());
            for (int j = 0; j < questionMap.size(); j++) {
                System.out.println(questionMap.get(j));
                System.out.println(questionTypes.get(j));
            }
            bufferedReader.close();
            inputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }

//        System.out.println("testing!");
//        System.out.println(getMapSize());
//        for(int i  = 0; i <= getMapSize(); i++){
//            System.out.println(getQuestion(i));
//        }
    }

    public String getQuestion(int index){
        return questionMap.get(index);
    }

    public int getMapSize() {
        return questionMap.size();
    }

    public String getQuestionType(int index) {
        return questionTypes.get(index);
    }
}