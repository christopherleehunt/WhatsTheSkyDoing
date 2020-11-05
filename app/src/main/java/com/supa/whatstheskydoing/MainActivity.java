package com.supa.whatstheskydoing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText cityName;
    private Button button;
    private TextView weatherReport;
    private ImageView weatherIcon;

    private String apiUrl = "";
    private String imageUrl = "";
    private static String apiKey = "04099d8a18eed5f1c260654124f484fd";

    private Bitmap bitmap = null;

    Weather weather;

    DecimalFormat df = new DecimalFormat("0.00");

    class Weather {
        String condition;
        String description;
        String icon;
        double temp;
        double temp_high;
        double temp_low;

        public Weather(String c, String d, String i, double t, double t1, double t2) {
            this.condition = c;
            this.description = d;
            this.icon = i;
            this.temp = t;
            this.temp_high = t1;
            this.temp_low = t2;
        }

        @NonNull
        @Override
        public String toString() {
            return description+"\n\nTemp: "+(int)temp+"\u00B0F"+"\nHigh: "+(int)temp_high+"\u00B0F"+"\nLow: "+(int)temp_low+"\u00B0F";
        }
    }

    public void findWeather(View view) {
        try {
            String city = URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            apiUrl = "Https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey+"&units=imperial";

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(cityName.getWindowToken(),0);
            makeWeatherRequest();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Weather not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeWeatherRequest() {
        StringRequest request = new StringRequest(apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject weatherObject = new JSONObject(response);
                        setWeatherData(weatherObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Weather not available", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void downloadIconDisplayWeather(String url) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    weatherReport.setVisibility(View.VISIBLE);
                    weatherIcon.setImageBitmap(response);
                    weatherReport.setText(weather.toString());
                }
            }
        }, 0,0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Icon not available", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void setWeatherData(JSONObject weatherObject) throws JSONException {
        JSONObject weatherObj = weatherObject.getJSONArray("weather").getJSONObject(0);
        JSONObject mainObj = weatherObject.getJSONObject("main");

        String condition = weatherObj.getString("main");
        String description = weatherObj.getString("description");
        String icon = weatherObj.getString("icon");
        double temp = (mainObj.getInt("temp"));
        double temp_min = mainObj.getInt("temp_min");
        double temp_max = mainObj.getInt("temp_max");

        weather = new Weather(condition, description, icon, temp, temp_max, temp_min);
        imageUrl = "https://openweathermap.org/img/wn/"+weather.icon+"@2x.png";
        downloadIconDisplayWeather(imageUrl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        button = findViewById(R.id.button);
        weatherIcon = findViewById(R.id.weatherIcon);
        weatherReport = findViewById(R.id.weatherReport);
        weatherReport.setText("");
    }
}