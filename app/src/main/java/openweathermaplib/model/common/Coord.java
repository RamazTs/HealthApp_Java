package openweathermaplib.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zhicheng fu
 */

public class Coord {

    @SerializedName("lon")
    private double lon;

    @SerializedName("lat")
    private double lat;

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
