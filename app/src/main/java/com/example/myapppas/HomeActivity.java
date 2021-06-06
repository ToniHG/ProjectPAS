package com.example.myapppas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class HomeActivity extends AppCompatActivity {
    EditText city_text,ip_text;
    TextView detailsWeather;
    //URL
    private final String baseURL = "https://api.weatherapi.com/v1/current.json?key=730f19e5d5104569ba6201930210606&q=";
    //Data of the weather
    private String last_updated,temp_c,wind_kph,precip_mm,humidity,uv_intensity,co,no2,so2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        city_text = findViewById(R.id.editTextCity);
        ip_text = findViewById(R.id.editTextIP);
        detailsWeather = findViewById(R.id.weatherResults);
    }

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
                        String cityResponse = jsonResponse.getJSONObject("location").getString("name");
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
                        out[0] = out[0] + ("Weather in " + cityResponse + " " + last_updated
                                + "\n Temp (Cº): " + temp_c
                                + "\n Wind (KPH): " + wind_kph
                                + "\n Rain (MM): " + precip_mm
                                + "\n Humidity: " + humidity
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
}