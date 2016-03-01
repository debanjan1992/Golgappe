package com.development.techiefolks.golgappe;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by deban on 29-02-2016.
 */
public class AddReviewDialog extends DialogFragment {
    ProgressBar progressBar;
    Dialog dialog;
    String id = "";
    Button submit_review;
    EditText review;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            id = getArguments().getString("movie_id");
        } catch (Exception e) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_review_dialog_layout, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        review= (EditText) view.findViewById(R.id.review_text_2);
        submit_review = (Button) view.findViewById(R.id.submit_review);
        submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateReviewID();
            }
        });
    }

    private void generateReviewID() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(Common.BASE_URL)
                .build();
        GetDataAPI api = adapter.create(GetDataAPI.class);
        String query = "SELECT review_id FROM review ORDER BY review_id LIMIT 1;";
        Call<ResponseBody> getReviewId = api.getData(query, "1");
        getReviewId.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.getString("success").equals("0")) {
                        saveReview("REV0000000001");
                    } else if (object.getString("success").equals("1")) {
                        String id = object.getJSONArray("output").getJSONObject(0).getString("arg0");
                        String generatedID = "REV";
                        String new_id = Long.toString(Long.parseLong(id.substring(3)) + 1);
                        int zeros_to_add = 7 - new_id.length();
                        for (int i = 1; i <= zeros_to_add; i++)
                            generatedID = generatedID + "0";
                        generatedID = generatedID + new_id;
                        saveReview(generatedID);

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

    private String saveReview(String review_id) {
        return null;
    }
}
