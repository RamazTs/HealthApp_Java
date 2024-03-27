package com.zc.medical_ai;
import java.util.*;

public class recordService {
    // Private constructor to prevent external instantiation
    private recordService() {

    }

    // Private static instance variable
    public static recordService instance;

    // Public static method to access the instance
    public static recordService getInstance() {
        if (instance == null) {
            instance = new recordService();
        }
        return instance;
    }

    // Other methods and properties of the Singleton class
}