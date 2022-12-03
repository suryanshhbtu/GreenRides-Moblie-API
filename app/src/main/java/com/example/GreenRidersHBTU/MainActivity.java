package com.example.GreenRidersHBTU;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.GreenRidersHBTU.Model.BaseUrl;
import com.example.GreenRidersHBTU.Model.LoginResult;
import com.example.GreenRidersHBTU.RetrofitApiCalls.RetrofitInterface;
import com.example.GreenRidersHBTU.User.LoggedUserActivity;
import com.example.GreenRidersHBTU.Admin.AdminHome;
import com.example.GreenRidersHBTU.Guard.LoggedGuardActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    SharedPreferences sharedPreferences;

    public static final String fileName = "login";
    public static final String Email = "email";
    public static final String Password = "password";

    private Retrofit retrofit;  // global variable of retrofit class
    private RetrofitInterface retrofitInterface; // global variable of retrofit Interface
    private BaseUrl baseUrl = new BaseUrl();
    private String BASE_URL = baseUrl.getBaseUrl();
    public static String AUTH_TOKEN = "";
    public static String role = "";
    public static boolean addCycle = false;
    public static String _id = "";

    private LoginResult result;

    // 1-DEC
    Button btnverifyCaptcha;
    String SITE_KEY = "6Lfs30cjAAAAAJ7iE_ykAiAiDL7Wc4Yt9hIxySA0";
    String SECRET_KEY = "6Lfs30cjAAAAACfm1ceeuRQf1WAIm8_30drsiutN";
    RequestQueue queue;


    //Integerated SignUp
    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    TextView textView;
    private static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // above defined
                .addConverterFactory(GsonConverterFactory.create()) // json -> javaObject
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class); // instantinsing

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        final TextView signUp = findViewById(R.id.signup);
        final CheckBox showPassword = findViewById(R.id.showPassword);
        final EditText passwordET = findViewById(R.id.passwordEditText);
         showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                 if(b){
                     passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                 }else{
                     passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                 }
             }
         });
        if(sharedPreferences.contains(Email))
        {
            ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Authenticating...");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();
//            Toast.makeText(MainActivity.this, "Wait...",
//                    Toast.LENGTH_SHORT).show();
            loggedCaptcha();

        }
        else
        {

            TextView loginBtn = (TextView) findViewById(R.id.login);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginCaptcha();
                }});
             // defined below
            // call Login Activity
            // Stay at the current activity.
        }




        // if signup button is presses
//        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getCycleHandler("WIFI:S:Tatto;T:WPA;P:e98$ajewr;H:false;;");
//                handleSignupDialog(); // defined below
//                setRentedHandler("WIFI:S:Tatto;T:WPA;P:e98$ajewr;H:false;;");
//            }
//        });

//        findViewById(R.id.signUpTV).setOnClickListener(View);
//        startActivity(new Intent(MainActivity.this, AdminHome.class));
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView)
            {
                TextView tv = (TextView) v;
                tv.setTextSize(14);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText("");
                tv.setTextColor(Color.parseColor("#FFFFFF"));
                tv.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.signupgooglebutton));
                tv.setSingleLine(true);
                tv.setPadding(45, 45, 45, 45);

                return;
            }
        }

        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            gotoProfile();
        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }

    private void gotoProfile(){
        // Captcha to be added here
        signUpCaptcha();
    }



    private void handleLoginDialog() {
        final EditText emailEdit = (EditText) findViewById(R.id.emailEditText);
        final EditText passwordEdit = (EditText) findViewById(R.id.passwordEditText);

                if(emailEdit.getText().toString().isEmpty() || passwordEdit.getText().toString().isEmpty()){

                    Toast.makeText(MainActivity.this, "Please Enter All Fields.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Logging You In...",
                            Toast.LENGTH_SHORT).show();
//                    ProgressDialog dialog=new ProgressDialog(MainActivity.this);
//                    dialog.setMessage("Authenticating...");
//                    dialog.setCancelable(false);
//                    dialog.setInverseBackgroundForced(false);
//                    dialog.show();
                    HashMap<String, String> map = new HashMap<>();
                    // preparing for post
                    final String emailData = emailEdit.getText().toString(), passwordData = passwordEdit.getText().toString();
                    map.put("email", emailData);
                    map.put("password", passwordData);

                      // post request
                Call<LoginResult> call = retrofitInterface.executeLogin(map);
                // execute http request
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

                        if (response.code() == 200) {

                             result = response.body();
                            // remember me check box
//                            final CheckBox rememberMe = findViewById(R.id.rememberMe);
//                            rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                @Override
//                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                                    if(!b){
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(Email, emailData);
                                        editor.putString(Password, passwordData);
                                        editor.commit();
//                                    }
//                                }
//                            });

                            Toast.makeText(MainActivity.this, "Login Successfully",
                                    Toast.LENGTH_LONG).show();
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
//                            builder1.setTitle(result.getMessage());
//                            builder1.setMessage(result.getToken());
//                            builder1.show();
                            AUTH_TOKEN = result.getToken();

                            if(result.getRole().equals("guard")){
                                _id = result.get_id();
                                role = "guard";
//                                Toast.makeText(MainActivity.this, " Guard SAAB",
//                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, LoggedGuardActivity.class);
////                                Log.i("SURFYANSH", result.toString());
                                intent.putExtra("_id", result.get_id());
                                intent.putExtra("name", result.getName());
                                intent.putExtra("email", result.getEmail());
                                startActivity(intent);
                            }else if(result.getRole().equals("student")){
                                _id = result.get_id();
                                role = "student";
                                Intent intent = new Intent(MainActivity.this, LoggedUserActivity.class);
//                                Log.i("SURFYANSH", result.toString());
                                intent.putExtra("_id", result.get_id());
                                intent.putExtra("name", result.getName());
                                intent.putExtra("rollno", result.getRollno());
                                intent.putExtra("branch", result.getBranch());
                                intent.putExtra("email", result.getEmail());
                                if (!result.getCycleid().equals(""))
                                    intent.putExtra("cycleid", result.getCycleid());
                                else {
                                    intent.putExtra("cycleid", "Not Rented");
                                }
                                startActivity(intent);
                            }else{
                                _id = result.get_id();
                                role = "admin";
//                                Toast.makeText(MainActivity.this, " Guard SAAB",
//                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, AdminHome.class);
////                                Log.i("SURFYANSH", result.toString());
                                intent.putExtra("_id", result.get_id());
                                intent.putExtra("name", result.getName());
                                intent.putExtra("email", result.getEmail());
                                startActivity(intent);
                            }

                        }else{
                            Toast.makeText(MainActivity.this, "Wrong Credentials",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Internal Server Error" ,
                                Toast.LENGTH_LONG).show();
                        // t.getMessage is showing site
                        Log.i("TAG",t.getMessage());
                    }
                });

            }
    }

    private void handleLoggedUser() {

//        Toast.makeText(MainActivity.this, "Loging You In...",
//                Toast.LENGTH_SHORT).show();
        HashMap<String, String> map = new HashMap<>();
        // preparing for post
        map.put("email", sharedPreferences.getString(Email,""));
        map.put("password",sharedPreferences.getString(Password,""));
        // post request
        Call<LoginResult> call = retrofitInterface.executeLogin(map);
        // execute http request
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

                if (response.code() == 200) {
                    Toast.makeText(MainActivity.this, "Login Successfully",
                            Toast.LENGTH_LONG).show();
                    result = response.body();

//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
//                            builder1.setTitle(result.getMessage());
//                            builder1.setMessage(result.getToken());
//                            builder1.show();
                    AUTH_TOKEN = result.getToken();

                    if(result.getRole().equals("guard")){
                        _id = result.get_id();
                        role = "guard";
//                                Toast.makeText(MainActivity.this, " Guard SAAB",
//                                        Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, LoggedGuardActivity.class);
////                                Log.i("SURFYANSH", result.toString());
                        intent.putExtra("_id", result.get_id());
                        intent.putExtra("name", result.getName());
                        intent.putExtra("email", result.getEmail());
                        startActivity(intent);
                    }else if(result.getRole().equals("student")){
                        _id = result.get_id();
                        role = "student";
                        Intent intent = new Intent(MainActivity.this, LoggedUserActivity.class);
//                                Log.i("SURFYANSH", result.toString());
                        intent.putExtra("_id", result.get_id());
                        intent.putExtra("name", result.getName());
                        intent.putExtra("rollno", result.getRollno());
                        intent.putExtra("branch", result.getBranch());
                        intent.putExtra("email", result.getEmail());
                        if (!result.getCycleid().equals(""))
                            intent.putExtra("cycleid", result.getCycleid());
                        else {
                            intent.putExtra("cycleid", "Not Rented");
                        }
                        startActivity(intent);
                    }else{
                        _id = result.get_id();
                        role = "admin";
//                                Toast.makeText(MainActivity.this, " Guard SAAB",
//                                        Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, AdminHome.class);
////                                Log.i("SURFYANSH", result.toString());
                        intent.putExtra("_id", result.get_id());
                        intent.putExtra("name", result.getName());
                        intent.putExtra("email", result.getEmail());
                        startActivity(intent);
                    }

                } else if (response.code() == 404) {
                    Toast.makeText(MainActivity.this, "Wrong Credentials",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Wrong Credentials",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void loginCaptcha(){
        SafetyNet.getClient(MainActivity.this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        // Indicates communication with reCAPTCHA service was
                        // successful.
                        String userResponseToken = recaptchaTokenResponse.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                            handleCaptchaResult(userResponseToken);
                            // Validate the user response token using the
                            // reCAPTCHA siteverify API.
                            handleLoginDialog();
//                            Toast.makeText(MainActivity.this, "Verified That You Are Not A Robot", Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "VALIDATION STEP NEEDED");
                        }else {
                            finish();
                            onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e("TAG", "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e("TAG", "Error: " + e.getMessage());
                        }
                    }
                });
    }
    public void loggedCaptcha(){
        SafetyNet.getClient(MainActivity.this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        // Indicates communication with reCAPTCHA service was
                        // successful.
                        String userResponseToken = recaptchaTokenResponse.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                            handleCaptchaResult(userResponseToken);
                            // Validate the user response token using the
                            // reCAPTCHA siteverify API.
                            handleLoggedUser();
//                            Toast.makeText(MainActivity.this, "Verified That You Are Not A Robot", Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "VALIDATION STEP NEEDED");
                        }else {
                            finish();
                            onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e("TAG", "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e("TAG", "Error: " + e.getMessage());
                        }
                    }
                });
    }

    public void signUpCaptcha(){
        SafetyNet.getClient(MainActivity.this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        // Indicates communication with reCAPTCHA service was
                        // successful.
                        String userResponseToken = recaptchaTokenResponse.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                            handleCaptchaResult(userResponseToken);
                            // Validate the user response token using the
                            // reCAPTCHA siteverify API.
                            Intent intent=new Intent(MainActivity.this,SignUpUser.class);
                            startActivity(intent);
//                            Toast.makeText(MainActivity.this, "Verified That You Are Not A Robot", Toast.LENGTH_SHORT).show();
                            Log.e("TAG", "VALIDATION STEP NEEDED");
                        }else {
                            finish();
                            onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e("TAG", "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e("TAG", "Error: " + e.getMessage());
                        }
                    }
                });
    }
    void handleCaptchaResult(final String responseToken) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                            }
                        } catch (Exception ex) {
                            Log.d("TAG", "Error message: " + ex.getMessage());

                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error message: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(request);
    }


}
