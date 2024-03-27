package openweathermaplib.model.threehourforecast;

import com.google.gson.annotations.SerializedName;
import openweathermaplib.model.common.Clouds;
import openweathermaplib.model.common.Main;
import openweathermaplib.model.common.Rain;
import openweathermaplib.model.common.Snow;
import openweathermaplib.model.common.Sys;
import openweathermaplib.model.common.Weather;
import openweathermaplib.model.common.Wind;

import java.util.List;


public class ThreeHourForecastWeather {

    @SerializedName("dt")
    private Long dt;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("visibility")
    private Long visibility;

    @SerializedName("pop")
    private Double pop;

    @SerializedName("rain")
    private Rain rain;

    @SerializedName("snow")
    private Snow snow;

    @SerializedName("sys")
    private Sys mSys;

    @SerializedName("dt_txt")
    private String dtTxt;

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public Long getVisibility() {
        return visibility;
    }

    public Double getPop() {
        return pop;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public void setSnow(Snow snow) {
        this.snow = snow;
    }

    public Sys getmSys() {
        return mSys;
    }

    public void setmSys(Sys mSys) {
        this.mSys = mSys;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public void setDtTxt(String dtTxt) {
        this.dtTxt = dtTxt;
    }
}
