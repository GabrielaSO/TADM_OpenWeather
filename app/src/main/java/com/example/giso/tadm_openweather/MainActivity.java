package com.example.giso.tadm_openweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Wrapper;

public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = "3b94ce67988fcad56aeac3f86b5f3370";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=Tepic,mx&APPID=3b94ce67988fcad56aeac3f86b5f3370");
        new GetWeatherTask(textView).execute(url);
    }

   /* public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(MainActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
    }*/
    public class Wrapper
    {
        String mainTemp;
        String mainTempMax;
        String mainTempMin;
        String mainHum;
        String weatherMain;
        String weatherDesc;

        public Wrapper() {
            this.mainTemp = mainTemp;
            this.mainTempMax = mainTempMax;
            this.mainTempMin = mainTempMin;
            this.mainHum = mainHum;
            this.weatherMain = weatherMain;
            this.weatherDesc = weatherDesc;
        }
    }

    private class GetWeatherTask extends AsyncTask<String, Void, Wrapper> {
        private TextView textView;
        String mainTemp = "UNDEFINED";
        String mainTempMax = "UNDEFINED";
        String mainTempMin = "UNDEFINED";
        String mainHum = "UNDEFINED";
        String weatherMain= "UNDEFINED";
        String weatherDesc="UNDEFINED";

        public GetWeatherTask(TextView textView) {
            this.textView = textView;
        }

        @Override
        protected Wrapper doInBackground(String... strings) {
            Wrapper w = new Wrapper();

            try {
                URL url = new URL(strings[0]);
                //URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Tepic,mx&APPID=3b94ce67988fcad56aeac3f86b5f3370!");
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                Log.i("Mensaje: ",urlConnection.getResponseMessage());

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                Log.d("JSON", builder.toString());

                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                JSONObject main = topLevel.getJSONObject("main");

                JSONArray weatherList = topLevel.getJSONArray("weather");

                for (int i = 0; i < weatherList.length(); i++) {
                    JSONObject listObj = weatherList.getJSONObject(i);
                    w.weatherMain= listObj.getString("main");
                    w.weatherDesc= listObj.getString("description");
                }

                w.mainTemp = String.valueOf(main.getDouble("temp"));
                w.mainTempMax = String.valueOf(main.getDouble("temp_max"));
                w.mainTempMin = String.valueOf(main.getDouble("temp_min"));
                w.mainHum = String.valueOf(main.getDouble("humidity"));


                urlConnection.disconnect();

            } catch (IOException | JSONException e) {
                Log.i("ERROR", e.getMessage());
                e.printStackTrace();
            }

            return (w);
        }

        @Override
        protected void onPostExecute(Wrapper w) {
            try {
                textView.setText(
                        "Estado: "+ w.weatherMain+
                                "\nDescripcion: "+w.weatherDesc+
                                "\nTemperatura actual: "+ w.mainTemp +
                                "\nTemperatura max: "+ w.mainTempMax +
                                "\nTemperatura min: "+ w.mainTempMin +
                                "\nHumedad : "+w.mainHum
                );
            }catch (Exception e){Log.i("ERROR: ",e.getMessage());}

        }
    }
}
