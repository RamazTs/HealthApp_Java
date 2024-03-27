package openweathermaplib.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zhicheng fu
 */

public class Clouds {

    @SerializedName("all")
    private double all;

    public double getAll() {
        return all;
    }
}


