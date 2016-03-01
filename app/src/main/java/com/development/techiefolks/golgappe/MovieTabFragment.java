package com.development.techiefolks.golgappe;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

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
 * Created by deban on 28-02-2016.
 */
public class MovieTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView rv;
    LinearLayoutManager layoutManager;
    MovieListingAdapter adapter;
    ArrayList<Item> itemList;
    SwipeRefreshLayout swipeRefreshLayout;
    FragmentManager manager;
    int visibleItemCount=0,totalItemCount=0,pastVisiblesItems=0;
    boolean loading=true;
    int starting_row_num=0;

    public MovieTabFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_tab_fragment, null, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

    }

    public void init() {
        rv = (RecyclerView) getActivity().findViewById(R.id.movieListingRV);
        swipeRefreshLayout= (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        manager=getFragmentManager();
        itemList = new ArrayList<>();
        //adapter = new MovieListingAdapter(getActivity(), itemList);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new MovieListingAdapter(getActivity(), manager, itemList);
        rv.setLayoutManager(layoutManager);
        rv.addOnScrollListener(rv_scrollListener);
        rv.setAdapter(adapter);
        loadView();

    }
    RecyclerView.OnScrollListener rv_scrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            if(dy > 0) //check for scroll down
            {
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                if (loading)
                {
                    if ( ((visibleItemCount + pastVisiblesItems) >= totalItemCount))
                    {
                        //loading = false;
                        starting_row_num=starting_row_num+10;
                        loadView();
                    }
                }
                //if(!loading && (totalItemCount-visibleItemCount)<=(pastVisiblesItems+5))
                //{
                //    loading=true;
                //}
            }
        }
    };

    public void getTotalMovieCount() {
        Retrofit r_adapter = new Retrofit.Builder()
                .baseUrl("http://therandomwall.com")
                .build();
        GetDataAPI api = r_adapter.create(GetDataAPI.class);
        String query = "SELECT COUNT * as total FROM movie";

        Call<ResponseBody> getResponse = api.getData(query, "1");
        getResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (object.getString("success").equals("1")) {
                        JSONArray jsonArray = object.getJSONArray("output");
                        int count = Integer.parseInt(jsonArray.getJSONObject(0).getString("arg0"));
                        if(starting_row_num>count)
                        {
                            loading=false;
                            Toast.makeText(getActivity(),"No more movies to show",Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void loadView() {
         Retrofit r_adapter = new Retrofit.Builder()
                .baseUrl("http://therandomwall.com")
                .build();
        GetDataAPI api = r_adapter.create(GetDataAPI.class);
        String query = "SELECT movie_id,movie_name,movie_poster FROM movie ORDER BY movie_release_date DESC LIMIT "+starting_row_num+",10;";

        Call<ResponseBody> getResponse = api.getData(query, "3");
        getResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object=new JSONObject(response.body().string());
                    if(object.getString("success").equals("1"))
                    {
                        JSONArray jsonArray=object.getJSONArray("output");
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            String movie_id=jsonArray.getJSONObject(i).getString("arg0");
                            String movie_title=jsonArray.getJSONObject(i).getString("arg1");
                            String url=jsonArray.getJSONObject(i).getString("arg2");
                            Item item=new Item(movie_title,movie_id,url);
                            itemList.add(item);
                        }
                        //if(itemList.size()==0) {
                        //    adapter = new MovieListingAdapter(getActivity(), manager, itemList);
                        //    rv.setAdapter(adapter);
                        //}
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onRefresh() {
        starting_row_num=0;
        itemList.clear();
        loadView();
    }
}

class Item {
    String id,title,url;

    public Item(String title, String id,String url) {
        this.url = url;
        this.title = title;
        this.id = id;
    }
}

class MovieListingAdapter extends RecyclerView.Adapter<MovieListingAdapter.MyViewholder> {
    Context context;
    ArrayList<Item> itemList;
    FragmentManager fm;
    int pos;

    public MovieListingAdapter(Context context, FragmentManager fm,ArrayList<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.fm=fm;
    }

    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.movietab_row_layout, parent, false);
        MyViewholder vh = new MyViewholder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewholder holder, int position) {
        Picasso.with(context)
                .load(itemList.get(position).url)
                .placeholder(R.drawable.nopreview)
                .into(holder.imageView_poster);
        holder.textView_title.setText(itemList.get(position).title);
        holder.button_submit.setTag(new String(itemList.get(position).id));
        holder.button_submit.setOnClickListener(writeReviewListener);
    }
    View.OnClickListener writeReviewListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle args=new Bundle();
            args.putString("movie_id",v.getTag().toString());
            AddReviewDialog dialog=new AddReviewDialog();
            dialog.setArguments(args);
            dialog.show(fm,"review_dialog");
        }
    };


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewholder extends RecyclerView.ViewHolder {
        ImageView imageView_poster;
        TextView textView_title;
        Button button_submit;

        public MyViewholder(View itemView) {
            super(itemView);
            imageView_poster = (ImageView) itemView.findViewById(R.id.movie_poster_row);
            textView_title = (TextView) itemView.findViewById(R.id.movie_title);
            button_submit = (Button) itemView.findViewById(R.id.write_review);
        }
    }
}
