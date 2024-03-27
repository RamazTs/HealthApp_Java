package com.zc.medical_ai;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.xm.Similarity;
import org.xm.similarity.word.hownet.concept.ConceptSimilarity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import mail_sender.MailSender;
import openweathermaplib.constant.Units;
import openweathermaplib.implementation.OpenWeatherMapHelper;
import openweathermaplib.implementation.callback.CurrentWeatherCallback;
import openweathermaplib.model.currentweather.CurrentWeather;
import speech.GoogleVoiceTypingDisabledException;
import speech.Logger;
import speech.Speech;
import speech.SpeechDelegate;
import speech.SpeechRecognitionNotAvailable;
import speech.SpeechUtil;
import speech.SupportedLanguagesListener;
import speech.TextToSpeechCallback;
import speech.UnsupportedReason;
import speech.ui.SpeechProgressView;

public class MainActivity extends AppCompatActivity implements SpeechDelegate {

    private static final int LOCATION_PERMISSION_ID = 44;
    private static final String OPEN_WEATHER_MAP_API_KEY ="e7845f99b424051c232ed9d347f533d7";
    private final int PERMISSIONS_REQUEST = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ImageButton button;
    private Button speak;
    private TextView text;
    private TextView systemLoading;
    private EditText textToSpeech;
    private SpeechProgressView progress;
    private LinearLayout linearLayout;
    private TableLayout tableLayout;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location current_Location;
    OpenWeatherMapHelper weather_helper;
    private dataStorage storage = dataStorage.getInstance();

    //    String location_address="";
//    String weather_string="";
//    String to_email="sren@sdsu.edu";
    private TextToSpeech.OnInitListener mTttsInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(final int status) {
            switch (status) {
                case TextToSpeech.SUCCESS:
                    Logger.info(LOG_TAG, "TextToSpeech engine successfully started");
                    break;

                case TextToSpeech.ERROR:
                    Logger.error(LOG_TAG, "Error while initializing TextToSpeech engine!");
                    break;

                default:
                    Logger.error(LOG_TAG, "Unknown TextToSpeech status: " + status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Speech.init(this, getPackageName(), mTttsInitListener);
        systemLoading=findViewById(R.id.system_loading);
        linearLayout = findViewById(R.id.linearLayout);
//        tableLayout = findViewById(R.id.medical_info_table);
//        tableLayout.setColumnShrinkable(0, true); //first column
//        tableLayout.setColumnStretchable(1, true); //second column
//        tableLayout.setColumnStretchable(2, true); //third column
        button = findViewById(R.id.button);
        button.setOnClickListener(view -> onButtonClick());
        button = findViewById(R.id.button2);
        button.setOnClickListener(view -> onButton2Click());

        //speak = findViewById(R.id.speak);
        //speak.setOnClickListener(view -> onSpeakClick());

        //text = findViewById(R.id.text);
        //textToSpeech = findViewById(R.id.textToSpeech);
        progress = findViewById(R.id.progress);

        int[] colors = {
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.darker_gray),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
        };
        progress.setColors(colors);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weather_helper = new OpenWeatherMapHelper(OPEN_WEATHER_MAP_API_KEY);
        weather_helper.setUnits(Units.IMPERIAL);
        getLastLocation();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.supportedSTTLanguages:
                onSetSpeechToTextLanguage();
                return true;

            case R.id.supportedTTSLanguages:
                onSetTextToSpeechVoice();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSetSpeechToTextLanguage() {
        Speech.getInstance().getSupportedSpeechToTextLanguages(new SupportedLanguagesListener() {
            @Override
            public void onSupportedLanguages(List<String> supportedLanguages) {
                CharSequence[] items = new CharSequence[supportedLanguages.size()];
                supportedLanguages.toArray(items);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Current language: " + Speech.getInstance().getSpeechToTextLanguage())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Locale locale;

                                if (Build.VERSION.SDK_INT >= 21) {
                                    locale = Locale.forLanguageTag(supportedLanguages.get(i));
                                } else {
                                    String[] langParts = supportedLanguages.get(i).split("-");

                                    if (langParts.length >= 2) {
                                        locale = new Locale(langParts[0], langParts[1]);
                                    } else {
                                        locale = new Locale(langParts[0]);
                                    }
                                }

                                Speech.getInstance().setLocale(locale);
                                Toast.makeText(MainActivity.this, "Selected: " + items[i], Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("Cancel", null)
                        .create()
                        .show();
            }


            @Override
            public void onNotSupported(UnsupportedReason reason) {
                switch (reason) {
                    case GOOGLE_APP_NOT_FOUND:
                        showSpeechNotSupportedDialog();
                        break;

                    case EMPTY_SUPPORTED_LANGUAGES:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.set_stt_langs)
                                .setMessage(R.string.no_langs)
                                .setPositiveButton("OK", null)
                                .show();
                        break;
                }
            }
        });
    }

    private void onSetTextToSpeechVoice() {
        List<Voice> supportedVoices = Speech.getInstance().getSupportedTextToSpeechVoices();

        if (supportedVoices.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.set_tts_voices)
                    .setMessage(R.string.no_tts_voices)
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        CharSequence[] items = new CharSequence[supportedVoices.size()];
        Iterator<Voice> iterator = supportedVoices.iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Voice voice = iterator.next();

            items[i] = voice.toString();
            i++;
        }

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Current: " + Speech.getInstance().getTextToSpeechVoice())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Speech.getInstance().setVoice(supportedVoices.get(i));
                        Toast.makeText(MainActivity.this, "Selected: " + items[i], Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Speech.getInstance().shutdown();
    }

    private void onButtonClick() {
//        if (Speech.getInstance().isListening()) {
//            Speech.getInstance().stopListening();
//        } else {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                onRecordAudioPermissionGranted();
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST);
//            }
//        }
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    private void onButton2Click() {
//        if (Speech.getInstance().isListening()) {
//            Speech.getInstance().stopListening();
//        } else {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                onRecordAudioPermissionGranted();
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST);
//            }
//        }
        Intent intent = new Intent(MainActivity.this, questionnaireHistory.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                onRecordAudioPermissionGranted();
                getLastLocation();
            } else {
                // permission denied, boo!
                Toast.makeText(MainActivity.this, R.string.permission_required, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onRecordAudioPermissionGranted() {
        button.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);

        try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(progress, MainActivity.this);

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    private void onSpeakClick() {
        if (textToSpeech.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.input_something, Toast.LENGTH_LONG).show();
            return;
        }

        Speech.getInstance().say(textToSpeech.getText().toString().trim(), new TextToSpeechCallback() {
            @Override
            public void onStart() {
                Toast.makeText(MainActivity.this, "TTS onStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleted() {
                Toast.makeText(MainActivity.this, "TTS onCompleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {
        //Log.d(getClass().getSimpleName(), "Speech recognition rms is now " + value +  "dB");
    }

    public String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
                .getInstance().getTime());
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

//    public void addTextRow(String value) {
//        TableRow tableRow = new TableRow(this);
//        TextView textTime = new TextView(this);
//        //textTime.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        textTime.setText(getCurrentTimestamp());
//
//        TextView btnShow = new TextView(this);
////        btnShow.setText("Del  ");
//        //btnShow.setLayoutParams(new LinearLayout.LayoutParams(10, 20));
//
//
//        //btnShow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        btnShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewGroup parent = (ViewGroup) v.getParent();
//                tableLayout.removeView(parent);
//                //Toast.makeText(MainActivity.this, R.string.welcome_message, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        //TextView textTemp = new TextView(this);
//        //textTemp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        //textTemp.setText("80");
//
//
//        TextView textValue = new TextView(this);
//        //textValue.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        textValue.setText(value);
//
//        TextView text_keyword = new TextView(this);
//        String main_key_word=KeyWord.getMostSimiliarWord(value);
//        text_keyword.setText(main_key_word);
//        tableRow.setLayoutParams(new
//                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
//                TableRow.LayoutParams.MATCH_PARENT));
//        tableRow.addView(btnShow);
//        tableRow.addView(textTime);
//        tableRow.addView(text_keyword);
//        tableRow.addView(textValue);
//        tableLayout.addView(tableRow);
//        Log.v("test_medical",value);
////        String server_email="";
////        String server_code="";
////        MailSender.sendToUserMail("Patient Information--Zhicheng Fu","", weather_string,main_key_word,value,server_email, server_code, to_email);
//        //linearLayout.
//    }

    @Override
    public void onSpeechResult(String result) {

        button.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        //text.setText(result);

        if (result.isEmpty()) {
            Speech.getInstance().say(getString(R.string.repeat));

        } else {
//            addTextRow(result);
            Speech.getInstance().say(result + ". information saved!");
        }
    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
//        text.setText("");
//        for (String partial : results) {
//            text.append(partial + " ");
//        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(MainActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            current_Location=location;
                            Log.v("location", current_Location.getLongitude()+" "+current_Location.getLatitude());
                            weather_helper.getCurrentWeatherByGeoCoordinates(current_Location.getLatitude(), current_Location.getLongitude(), new CurrentWeatherCallback() {
                                @Override
                                public void onSuccess(CurrentWeather currentWeather) {
//                    Log.v(TAG, "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
//                            +"Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
//                            +"Temperature: " + currentWeather.getMain().getTempMax()+"\n"
//                            +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
//                            +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry()
//                    );
                                    systemLoading.setVisibility(View.GONE);
//                                    weather_string=currentWeather.getWeather().get(0).getDescription()+", Temperature: "+currentWeather.getMain().getTempMax()+" Humidity: "+currentWeather.getMain().getHumidity()+" City: "+currentWeather.getName()+" Country: "+currentWeather.getSys().getCountry();
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    Log.v("weather_info", throwable.getMessage());
                                }
                            });
                            // latitudeTextView.setText(location.getLatitude() + "");
                            //longitTextView.setText(location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            current_Location = locationResult.getLastLocation();
            Log.v("location222", current_Location.getLongitude()+" "+current_Location.getLatitude());

            weather_helper.getCurrentWeatherByGeoCoordinates(current_Location.getLatitude(), current_Location.getLongitude(), new CurrentWeatherCallback() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
//                    Log.v(TAG, "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
//                            +"Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
//                            +"Temperature: " + currentWeather.getMain().getTempMax()+"\n"
//                            +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
//                            +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry()
//                    );
                    systemLoading.setVisibility(View.GONE);
//                    weather_string=currentWeather.getWeather().get(0).getDescription()+", Temperature: "+currentWeather.getMain().getTempMax()+",  Humidity: "+currentWeather.getMain().getHumidity()+", City: "+currentWeather.getName()+" Country: "+currentWeather.getSys().getCountry();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.v("weather_info", throwable.getMessage());
                }
            });

        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}