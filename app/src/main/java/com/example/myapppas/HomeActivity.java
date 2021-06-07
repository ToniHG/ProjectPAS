package com.example.myapppas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    //Data entry for weather
    EditText city_text,ip_text;
    //Output for the weather data
    TextView detailsWeather;
    //Local database for weather
    dbWeatherManager myDBManager;
    //URL
    private final String baseURL = "https://api.weatherapi.com/v1/current.json?key=730f19e5d5104569ba6201930210606&q=";
    //Data of the weather
    private String city,last_updated,temp_c,wind_kph,precip_mm,humidity,uv_intensity,co,no2,so2;
    //Data of the sensors
    private SensorManager sensorManager;
    private Sensor sensorLux, sensorHum, sensorTemp, sensorPress;
    private float valueLigth,valueHum,valueTemp, valuePress;
    //Output for the weather data
    TextView detailsSensors;
    //Database Firebase Realtime Sensors
    FirebaseDatabase dbSensors;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        city_text = findViewById(R.id.editTextCity);
        ip_text = findViewById(R.id.editTextIP);
        detailsWeather = findViewById(R.id.weatherResults);
        myDBManager = new dbWeatherManager(this.getBaseContext());
        detailsSensors = findViewById(R.id.sensorsResults);
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorPress = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorTemp = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        sensorHum = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorLux = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener((SensorEventListener) mSensorListener,sensorPress, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) mSensorListener,sensorTemp, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) mSensorListener,sensorHum, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) mSensorListener,sensorLux, SensorManager.SENSOR_DELAY_NORMAL);
        dbSensors = FirebaseDatabase.getInstance();
        dbRef = dbSensors.getReference("user");
    }

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PRESSURE){
                valuePress = event.values[0];
            }else if (event.sensor.getType() == Sensor.TYPE_TEMPERATURE){
                valueTemp = event.values[0];
            }else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
                valueHum = event.values[0];
            }else if (event.sensor.getType() ==Sensor.TYPE_LIGHT){
                valueLigth = event.values[0];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //Nothing to do
        }
    };

    public void logOutSession(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void getWeather(View view){
        //Data
        final String[] out = {""};
        String urlRequest = baseURL;
        String stcity = city_text.getText().toString().trim();
        String stdip = ip_text.getText().toString().trim();
        if(!TextUtils.isEmpty(stcity) || !TextUtils.isEmpty(stdip)){
            if(!TextUtils.isEmpty(stcity)){
                urlRequest = baseURL + stcity + "&aqi=yes";
            }
            else{
                urlRequest = baseURL + stdip + "&aqi=yes";
            }
            //Request for api with volley
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlRequest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonObjectCurrent = jsonResponse.getJSONObject("current");
                        city = jsonResponse.getJSONObject("location").getString("name");
                        last_updated = jsonObjectCurrent.getString("last_updated");
                        temp_c = jsonObjectCurrent.getString("temp_c");
                        wind_kph = jsonObjectCurrent.getString("wind_kph");
                        precip_mm = jsonObjectCurrent.getString("precip_mm");
                        humidity = jsonObjectCurrent.getString("humidity");
                        uv_intensity = jsonObjectCurrent.getString("humidity");
                        JSONObject jsonObjectAIQ = jsonObjectCurrent.getJSONObject("air_quality");
                        co = jsonObjectAIQ.getString("co");
                        no2 = jsonObjectAIQ.getString("no2");
                        so2 = jsonObjectAIQ.getString("so2");
                        out[0] = out[0] + ("Weather in " + city + " " + last_updated
                                + "\n Temp (Cº): " + temp_c
                                + "\n Wind (KPH): " + wind_kph
                                + "\n Rain (MM): " + precip_mm
                                + "\n Humidity(%): " + humidity
                                + "\n Air Quality: "
                                + "\n\t CO: " + co
                                + "\n\t NO2: " + no2
                                + "\n\t SO2: " + so2);
                        detailsWeather.setText(out[0]);

                    }catch(JSONException e){
                        Toast.makeText(HomeActivity.this,"Error while creating response: " + e.toString().trim(),Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity.this,"Error with request API:" + error.toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        else{
            Toast.makeText(HomeActivity.this, "Both texts cannot be empty!!", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveDataWeather(View view){

        boolean resultInsert;
        SQLiteDatabase myWeatherDB = myDBManager.getWritableDatabase();
        int rows = (int) DatabaseUtils.queryNumEntries(myWeatherDB, dbWeatherManager.weather_dbEntry.TABLE_NAME);
        int nextRow = rows + 1;
        //Creating the content values to save the data
        ContentValues values = new ContentValues();
        values.put(dbWeatherManager.weather_dbEntry.ID, String.valueOf(nextRow));
        values.put(dbWeatherManager.weather_dbEntry.CITY, city);
        values.put(dbWeatherManager.weather_dbEntry.DATE, last_updated);
        values.put(dbWeatherManager.weather_dbEntry.TEMP, temp_c);
        values.put(dbWeatherManager.weather_dbEntry.WIN, wind_kph);
        values.put(dbWeatherManager.weather_dbEntry.HUM, humidity);
        values.put(dbWeatherManager.weather_dbEntry.RAIN, precip_mm);
        values.put(dbWeatherManager.weather_dbEntry.UVI, uv_intensity);
        values.put(dbWeatherManager.weather_dbEntry.CO, co);
        values.put(dbWeatherManager.weather_dbEntry.NO2, no2);
        values.put(dbWeatherManager.weather_dbEntry.SO2, so2);
        resultInsert = myDBManager.insertDataWeather(myWeatherDB,values);
        if(resultInsert){
            Toast.makeText(HomeActivity.this, "Data saved in local database.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(HomeActivity.this, "There was some problem saving the data.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSensorsData(View view){
        final String[] out = {""};
        //Generate the output valueLigth, valueHum, valueGrav, valueTemp, valuePress;
        out[0] = out[0] + ("Data from the sensors: "
                + "\n Ligth (Lux): " + String.valueOf(valueLigth)
                + "\n Humidity (%): " + String.valueOf(valueHum)
                + "\n Temperature (C): " + String.valueOf(valueTemp)
                + "\n Pressure (hPa): " + String.valueOf(valuePress));
        Toast.makeText(HomeActivity.this, "Data from sensors showed.", Toast.LENGTH_SHORT).show();
        detailsSensors.setText(out[0]);
    }

    public void sendDatatoCloud(View view){
        String out = "";
        out = out + ("Ligth(Lux)=" + String.valueOf(valueLigth)
                + ",\n Humidity(%)=" + String.valueOf(valueHum)
                + ",\n Temperature(C)=" + String.valueOf(valueTemp)
                + ",\n Pressure (hPa)=" + String.valueOf(valuePress));
        //dbRef.setValue(FirebaseAuth.getInstance().getCurrentUser());
        List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
        Map<String, String> dataToSave = new HashMap<>();
        dataToSave.put("Ligth(Lux)",String.valueOf(valueLigth));
        dataToSave.put("Humidity(%)",String.valueOf(valueHum));
        dataToSave.put("Temperature(C)",String.valueOf(valueTemp));
        dataToSave.put("Pressure(hPa)",String.valueOf(valuePress));
        listData.add(dataToSave);
        dbRef.setValue(listData);
    }
}