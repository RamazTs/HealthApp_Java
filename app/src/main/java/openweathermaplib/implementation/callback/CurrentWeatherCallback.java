package openweathermaplib.implementation.callback;

import openweathermaplib.model.currentweather.CurrentWeather;

public interface CurrentWeatherCallback{
    void onSuccess(CurrentWeather currentWeather);
    void onFailure(Throwable throwable);
}
