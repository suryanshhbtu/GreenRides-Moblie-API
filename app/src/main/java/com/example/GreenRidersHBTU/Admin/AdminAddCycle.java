package com.example.GreenRidersHBTU.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.GreenRidersHBTU.Guard.LoggedGuardActivity;
import com.example.GreenRidersHBTU.MainActivity;
import com.example.GreenRidersHBTU.Model.BaseUrl;
import com.example.GreenRidersHBTU.R;
import com.example.GreenRidersHBTU.RetrofitApiCalls.RetrofitInterface;
import com.example.GreenRidersHBTU.Scanners.ScannerViewAddCycle;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminAddCycle extends AppCompatActivity {

    private Retrofit retrofit;  // global variable of retrofit class
    private RetrofitInterface retrofitInterface; // global variable of retrofit Interface


    // getting baseUrl of Server
    private BaseUrl baseUrl = new BaseUrl();
    private String BASE_URL = baseUrl.getBaseUrl();

    public static  TextView qrTV; //  cycle QR TextView -> store value of scanned QR
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_cycle);

        MainActivity.addCycle = true;
        qrTV = (TextView)  findViewById(R.id.qrTV);
        findViewById(R.id.QRLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(LoggedUserActivity.this, "Button Dababa",
//                        Toast.LENGTH_LONG).show();

                // DEALING WITH LONG TERM CAMERA ON
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {

                                Toast.makeText(AdminAddCycle.this, "Due To Inactivity Closing Camera",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                                Log.i("tag", "This'll run  milliseconds later");
                            }
                        },
                        8000);
                startActivity(new Intent(AdminAddCycle.this, ScannerViewAddCycle.class));
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // above defined
                .addConverterFactory(GsonConverterFactory.create()) // json -> javaObject
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class); // instantinsing
        addCycleHandler();

    }
    private void addCycleHandler() {
        LinearLayout addCycleLL = (LinearLayout) findViewById(R.id.addCycleLL);

        addCycleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AdminAddCycle.this, "Sending Data...",
                        Toast.LENGTH_SHORT).show();
                HashMap<String, String> map = new HashMap<>();
                // preparing for post
                if(qrTV.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddCycle.this);
                    builder.setTitle("Invalid Input");
                    builder.setMessage("Scan the QR first");
                    builder.show();
                    return;
                }
                map.put("cycleid", qrTV.getText().toString());
                map.put("status", "");
                map.put("stdid", "");
                // post request
                Call<Void> call = retrofitInterface.executeCycleSignup(map);
                // execute http request
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.code() == 200) {
                            Toast.makeText(AdminAddCycle.this, "Cycles Added Successfully",
                                    Toast.LENGTH_LONG).show();
                            qrTV.setText("Scan To Add Another Cycle");

                        } else{
                            Toast.makeText(AdminAddCycle.this, "Wrong Credentials",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AdminAddCycle.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }
}