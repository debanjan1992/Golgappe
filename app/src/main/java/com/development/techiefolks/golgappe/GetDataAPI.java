package com.development.techiefolks.golgappe;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by AR on 2/26/2016.
 */
public interface GetDataAPI {
    @FormUrlEncoded
    @POST("/golgappe/getData.php")
    Call<ResponseBody> getData(@Field("query")String query,@Field("colnum") String colnum);
}
