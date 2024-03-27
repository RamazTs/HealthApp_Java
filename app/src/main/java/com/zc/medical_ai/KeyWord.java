package com.zc.medical_ai;

import org.xm.Similarity;

public class KeyWord {

    public static String[] keywords={"bad","heavy","severe","mild","high","low"};
    public static String getMostSimiliarWord(String sentence)
    {
        double value=0;
        String finalWord="none";
        String[] words=sentence.split(" ");
        if(words!=null&&words.length>0)
        {
            for(String word:words)
            {
                for(String keyword: KeyWord.keywords)
                {
//                    double conceptSimilarityResult = Similarity.conceptSimilarity(word, keyword);
//                    if(value<conceptSimilarityResult)
//                    {
//                        finalWord=word;
//                        value=conceptSimilarityResult;
//                    }
                    if(word.equals(keyword))
                    {
                        finalWord=word;
                        break;
                    }
                }
            }
        }
        return finalWord;
    }

}
