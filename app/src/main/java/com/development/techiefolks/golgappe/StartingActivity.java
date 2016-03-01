package com.development.techiefolks.golgappe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class StartingActivity extends AppCompatActivity {
    TextView signUp, forgotPassword, error;
    EditText email, password;
    ActionBar actionBar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        initialize();
    }

    private void initialize() {
        signUp = (TextView) findViewById(R.id.as_signUp);
        forgotPassword = (TextView) findViewById(R.id.as_forgotPassword);
        email = (EditText) findViewById(R.id.userid);
        password = (EditText) findViewById(R.id.userpassword);
        error = (TextView) findViewById(R.id.as_error);
        signUp.setText(Html.fromHtml("<u><i>New User? Sign up here<i></u>"));
        forgotPassword.setText(Html.fromHtml("<u><i>Forgot Password<i></u>"));
        actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void signIn(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (!Common.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.getText().toString().matches("^[A-Za-z0-9._]+@[A-Za-z0-9]+\\.[A-Za-z]{2,6}$")) {
            displayError("Wrong email format");
            return;
        }
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .build();
        GetDataAPI api = adapter.create(GetDataAPI.class);
        String encoded_password = Encrypt.cryptWithMD5(password.getText().toString());
        String query = "SELECT IF(EXISTS(SELECT * FROM user WHERE user_id ='" + email.getText().toString() + "' and user_password='" + encoded_password + "'), '1', '0') as result;";
        Call<ResponseBody> call = api.getData(query, "1");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getString("success").equals("1")) {
                        String output = jsonObject.getJSONArray("output").getJSONObject(0).getString("arg0");
                        if (output.equals("1")) {
                            saveSession();
                            startHomeActivity();
                        } else {
                            displayError("Invalid email/password");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (progressDialog.isShowing()) {
                        progressDialog.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
            }
        });

    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUp_Activity.class);
        startActivity(intent);
    }

    private void startHomeActivity() {
        finish();
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void displayError(String message) {
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }


    public void goTonext(View view) {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("movie_id", "15");
        startActivity(intent);
        finish();
    }
    public void saveSession()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("sessionData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userid", email.getText().toString());
        editor.putString("password", Encrypt.cryptWithMD5(password.getText().toString()));
        editor.commit();
    }

    public void forgotPassword(View view) {
        Intent intent=new Intent(this,ResetPassword.class);
        startActivity(intent);
    }
}
