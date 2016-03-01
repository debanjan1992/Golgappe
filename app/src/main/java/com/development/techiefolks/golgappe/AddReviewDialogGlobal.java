package com.development.techiefolks.golgappe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by deban on 29-02-2016.
 */
public class AddReviewDialogGlobal extends DialogFragment {
    AutoCompleteTextView movie_nameTV;
    ArrayList<MovieItem> movieList;
    ProgressBar progressBar;
    Dialog dialog;
    String m_id="";
    Button next,saveReview,shareReview;
    RelativeLayout layout1,layout2,layout3;
    EditText review;
    ArrayList<String> data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            m_id = getArguments().getString("movie_id");
        }
        catch (Exception e){}
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_review_dialog_layout_global, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Result result=new Result();
        dialog=getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressBar= (ProgressBar) view.findViewById(R.id.progressBar);
        movie_nameTV = (AutoCompleteTextView) view.findViewById(R.id.actv_movie_name);
        movie_nameTV.setThreshold(1);
        movie_nameTV.setEnabled(false);
        movie_nameTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_id = movieList.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        movieList = new ArrayList<>();
        layout1= (RelativeLayout) view.findViewById(R.id.ardlg_1);
        layout2= (RelativeLayout) view.findViewById(R.id.ardlg_2);
        layout3= (RelativeLayout) view.findViewById(R.id.ardlg_3);
        review= (EditText) view.findViewById(R.id.review_text);
        next= (Button) view.findViewById(R.id.nextDialog);
        saveReview= (Button) view.findViewById(R.id.saveReview);
        shareReview= (Button) view.findViewById(R.id.share_review);
        data=new ArrayList<String>();
        loadArray();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(movie_nameTV.getText().toString().length()==0)
                {
                    Toast.makeText(getContext(),"Invalid movie name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data.contains(movie_nameTV.getText().toString())) {
                    result.setId(m_id);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(getContext(),"Invalid movie name",Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(review.getText().toString().length()>140) {
                    result.setReview(review.getText().toString());
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                    saveReviewToDB();
                }
                else
                {
                    Toast.makeText(getContext(),"Kindly enter a minimum of 140 characters",Toast.LENGTH_SHORT).show();
                }
            }
        });
        shareReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void saveReviewToDB() {

    }


    private void loadArray() {
        movie_nameTV.setHint("Loading...");
        progressBar.setVisibility(View.VISIBLE);
        Retrofit r_adapter = new Retrofit.Builder()
                .baseUrl("http://therandomwall.com")
                .build();
        GetDataAPI api = r_adapter.create(GetDataAPI.class);
        String query = "select movie_name,movie_id from movie;";

        Call<ResponseBody> getResponse = api.getData(query, "2");
        getResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.getString("success").equals("1")) {
                        JSONArray jsonArray = object.getJSONArray("output");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String movie_name = jsonArray.getJSONObject(i).getString("arg0");
                            String movie_id = jsonArray.getJSONObject(i).getString("arg1");
                            movieList.add(new MovieItem(movie_id,movie_name));
                        }
                        //AutoCompleteViewAdapter adapter=new AutoCompleteViewAdapter(movieList,getDialog().getContext());
                        for(int i=0;i<movieList.size();i++)
                        {
                            data.add(movieList.get(i).name);
                        }
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getDialog().getContext(),
                                R.layout.autocompleteview_row_layout,R.id.arl_movie_name,data);
                        movie_nameTV.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.INVISIBLE);
                dialog.setCancelable(true);
                movie_nameTV.setHint("Select Movie");
                movie_nameTV.setEnabled(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private class MovieItem
    {
        String id,name;

        public MovieItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    private class Result{
        String id,review;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }
    }
}
