package com.development.techiefolks.golgappe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by AR on 2/29/2016.
 */
public class ResetPassword extends AppCompatActivity {
    RelativeLayout layout1, layout2, layout3;
    ProgressBar progressBar;
    TextView error;
    EditText textEmail, textCode, newPassword, confirmNewPassword;
    Button submitEmail, submitCode;
    String REQ_CODE = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        layout1 = (RelativeLayout) findViewById(R.id.afp_1);
        layout2 = (RelativeLayout) findViewById(R.id.afp_2);
        layout3 = (RelativeLayout) findViewById(R.id.afp_3);
        error = (TextView) findViewById(R.id.email_error);
        textEmail = (EditText) findViewById(R.id.text_email);
        textCode = (EditText) findViewById(R.id.text_code);
        newPassword = (EditText) findViewById(R.id.newPassword1);
        confirmNewPassword = (EditText) findViewById(R.id.newPassword2);
        submitEmail = (Button) findViewById(R.id.submit_email);
        progressBar = (ProgressBar) findViewById(R.id.progressBarFP);
        textEmail.addTextChangedListener(watcher);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String chars = s.toString();
            if (chars.length() == 0)
                return;
            char lastChar = chars.charAt(chars.length() - 1);
            int c = 0;
            for (int i = 0; i < chars.length(); i++) {
                if (chars.charAt(i) == '@')
                    c++;
            }
            if (c > 1) {
                textEmail.setText(chars.substring(0, chars.length() - 1));
                textEmail.setSelection(textEmail.getText().length());
            }
            if (chars.length() == 1 && lastChar == '@') {
                textEmail.setText("");
            }

        }
    };

    public void displayError(String message) {
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }

    public void validateEmail(final String email) {
        if (!Common.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .build();
        GetDataAPI api = adapter.create(GetDataAPI.class);
        String query = "SELECT IF(EXISTS(SELECT * FROM user WHERE user_id ='" + email + "'), '1', '0') as result;";
        Call<ResponseBody> call = api.getData(query, "1");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getString("success").equals("1")) {
                        String output = jsonObject.getJSONArray("output").getJSONObject(0).getString("arg0");
                        if (output.equals("1")) {
                            //send email to user with the code
                            Random random = new Random();
                            int code = random.nextInt((99999 - 10000) - 1) + 10000;
                            sendMail(email, Integer.toString(code));
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            displayError("Email not registered");
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

    private void sendMail(String email, final String code) {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .build();
        ForgotPasswordAPI api = adapter.create(ForgotPasswordAPI.class);
        Call<ResponseBody> response = api.sendPasswordResetMail(email, code);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body = response.body().string();
                    if (body.equals("Message has been sent successfully")) {
                        displayError("");
                        layout1.setVisibility(View.INVISIBLE);
                        layout2.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        REQ_CODE = code;
                        //YoYo.with(Techniques.FadeIn).duration(500).playOn(layout2);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ResetPassword.this, body, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void resetPassword(View v) {
        String userEmail = textEmail.getText().toString();
        if (!userEmail.matches("^[A-Za-z0-9._]+@[A-Za-z0-9]+\\.[A-Za-z]{2,6}$")) {
            displayError("Wrong email format");
        } else {
            validateEmail(userEmail);
        }
    }

    public void submitCode(View v) {
        if (textCode.getText().toString().equals(REQ_CODE)) {
            displayError("");
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.VISIBLE);
        } else {
            displayError("Invalid Code");
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you want to cancel the password reset process?");
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

    public void updatePassword(View view) {
        if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
            displayError("Passwords do not match");
            return;
        }
        if (!Common.isNetworkAvailable(this)) {
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        displayError("");
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .build();
        AddDataAPI api=adapter.create(AddDataAPI.class);
        String query="UPDATE user SET user_password='"+Encrypt.cryptWithMD5(newPassword.getText().toString())
                +"' WHERE user_id='"+textEmail.getText().toString()+"';";
        Call<ResponseBody> updateData=api.getResponse("insert_update_delete.php",query);
        updateData.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().string());

                    if (jsonObject.getString("success").equals("1")) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Intent intent=new Intent(ResetPassword.this,StartingActivity.class);
                        startActivity(intent);
                        Toast.makeText(ResetPassword.this, "Password has been reset successfully!", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ResetPassword.this, "Internal Error: "+response.body().string(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
