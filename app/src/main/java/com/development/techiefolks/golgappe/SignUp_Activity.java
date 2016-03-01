package com.development.techiefolks.golgappe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUp_Activity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout layout1, layout2, layout3, layout4, layout5, layout6;
    EditText name, email, mobile, password;
    Button next1, next2, next3, next4, next5, letsgo;
    ImageView userdp;
    RadioButton male, female, others;
    TextView error;
    ProgressDialog progressDialog;
    String usergender = "";
    ActionBar actionbar;
    Bitmap bitmap;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();
    }

    private void initialize() {
        layout1 = (RelativeLayout) findViewById(R.id.asu_1);
        layout2 = (RelativeLayout) findViewById(R.id.asu_2);
        layout3 = (RelativeLayout) findViewById(R.id.asu_3);
        layout4 = (RelativeLayout) findViewById(R.id.asu_4);
        layout5 = (RelativeLayout) findViewById(R.id.asu_5);
        layout6 = (RelativeLayout) findViewById(R.id.asu_6);
        name = (EditText) findViewById(R.id.signUp_name);
        email = (EditText) findViewById(R.id.signUp_email);
        mobile = (EditText) findViewById(R.id.signUp_mobile);
        password = (EditText) findViewById(R.id.signUp_password);
        next1 = (Button) findViewById(R.id.signUp_next_1);
        next2 = (Button) findViewById(R.id.signUp_next_2);
        next3 = (Button) findViewById(R.id.signUp_next_3);
        next4 = (Button) findViewById(R.id.signUp_next_4);
        next5 = (Button) findViewById(R.id.signUp_next_5);
        letsgo = (Button) findViewById(R.id.signUp_next_6);
        male = (RadioButton) findViewById(R.id.gender_male);
        female = (RadioButton) findViewById(R.id.gender_female);
        others = (RadioButton) findViewById(R.id.gender_others);
        error = (TextView) findViewById(R.id.asu_error);
        userdp = (ImageView) findViewById(R.id.userdp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bitmap = null;
        actionbar = getSupportActionBar();
        actionbar.setTitle("Name");
        next1.setOnClickListener(this);
        next2.setOnClickListener(this);
        next3.setOnClickListener(this);
        next4.setOnClickListener(this);
        next5.setOnClickListener(this);
        letsgo.setOnClickListener(this);

        email.addTextChangedListener(watcher);
    }
    TextWatcher watcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            String chars=s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String chars=s.toString();

        }

        @Override
        public void afterTextChanged(Editable s) {
            String chars=s.toString();
            if(chars.length()==0)
                return;
            char lastChar=chars.charAt(chars.length()-1);
            int c=0;
            for(int i=0;i<chars.length();i++)
            {
                if(chars.charAt(i)=='@')
                c++;
            }
            if(c>1)
            {
                email.setText(chars.substring(0,chars.length()-1));
                email.setSelection(email.getText().length());
            }
            if(chars.length()==1 && lastChar=='@')
            {
                email.setText("");
            }

        }
    };

    @Override
    public void onClick(View v) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e)
        {}
        error.setVisibility(View.INVISIBLE);
        switch (v.getId()) {
            case R.id.signUp_next_1:

                String username = name.getText().toString();
                if (!username.matches("^[A-Za-z\\s]{1,}")) {
                    displayError("Wrong name format");
                } else {
                    actionbar.setTitle("Email");
                    layout1.setVisibility(View.INVISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(layout2);
                }
                break;
            case R.id.signUp_next_2:

                String useremail = email.getText().toString();
                if (!useremail.matches("^[A-Za-z0-9._]+@[A-Za-z0-9]+\\.[A-Za-z]{2,6}$")) {
                    displayError("Wrong email format");
                } else {
                    isUserUnique();
                }
                break;
            case R.id.signUp_next_3:
                String usermobile = mobile.getText().toString();
                if (usermobile.length() != 10) {
                    displayError("Wrong mobile number");
                } else {
                    actionbar.setTitle("Gender");
                    layout3.setVisibility(View.INVISIBLE);
                    layout4.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(layout4);
                }
                break;
            case R.id.signUp_next_4:
                if (male.isChecked())
                    usergender = "Male";
                else if (female.isChecked())
                    usergender = "Female";
                else if (others.isChecked())
                    usergender = "Others";
                else
                    displayError("Please select a gender");
                if (usergender != "") {
                    actionbar.setTitle("Password");
                    layout4.setVisibility(View.INVISIBLE);
                    layout5.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(layout5);
                }
                break;
            case R.id.signUp_next_5:
                String userpassword = password.getText().toString();
                if (userpassword.length() < 6) {
                    displayError("Password should be more than 6 characters");
                } else {
                    actionbar.setTitle("Profile Picture");
                    layout5.setVisibility(View.INVISIBLE);
                    layout6.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(layout6);
                }
                break;
            case R.id.signUp_next_6:
                if (Common.isNetworkAvailable(this)) {
                    storeUserData();
                } else {
                    Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }

    public void loadImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                userdp.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void isUserUnique() {
        if (!Common.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.getText().toString().matches("^[A-Za-z0-9._]+@[A-Za-z0-9]+\\.[A-Za-z]{2,6}$")) {
            displayError("Wrong email format");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .build();
        GetDataAPI api = adapter.create(GetDataAPI.class);
        String query = "SELECT IF(EXISTS(SELECT * FROM user WHERE user_id ='" + email.getText().toString() + "'), '1', '0') as result;";
        Call<ResponseBody> call = api.getData(query, "1");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getString("success").equals("1")) {
                        String output = jsonObject.getJSONArray("output").getJSONObject(0).getString("arg0");
                        if (output.equals("1")) {
                            progressBar.setVisibility(View.INVISIBLE);
                            displayError("Email already registered!");
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            layout2.setVisibility(View.INVISIBLE);
                            layout3.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeIn).duration(500).playOn(layout3);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you want to cancel the sign up process?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.setNegativeButton("No", null);
        alert.setCancelable(false);
        alert.show();
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void storeUserData() {
        if (!Common.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding you to our family");
        progressDialog.show();
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .build();
        AddDataAPI api = adapter.create(AddDataAPI.class);
        String gender = "Others";
        if (male.isChecked())
            gender = "Male";
        else if (female.isChecked())
            gender = "Female";
        String base64Image = "";
        if (bitmap != null) {
            base64Image = getStringImage(bitmap);
        } else {
            Bitmap bmap = BitmapFactory.decodeResource(SignUp_Activity.this.getResources(), R.drawable.user);
            base64Image = getStringImage(bmap);
        }
        Call<ResponseBody> call = api.getResponse(
                "addUser.php",
                name.getText().toString(),
                email.getText().toString(),
                mobile.getText().toString(),
                gender,
                Encrypt.cryptWithMD5(password.getText().toString()),
                base64Image
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getString("success").equals("1")) {
                        progressDialog.dismiss();
                        saveSession();
                        startHomeActivity();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp_Activity.this, "Internal Error", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
    public void saveSession()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("sessionData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userid",email.getText().toString());
        editor.putString("password",Encrypt.cryptWithMD5(password.getText().toString()));
        editor.commit();
    }
}
