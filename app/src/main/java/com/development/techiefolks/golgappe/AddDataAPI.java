package com.development.techiefolks.golgappe;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by AR on 2/26/2016.
 */
public interface AddDataAPI {
    @FormUrlEncoded
    @POST("/golgappe/{phpFile}")
    Call<ResponseBody> getResponse(@Path("phpFile") String file,
                                   @Field("name") String name,
                                   @Field("email") String email,
                                   @Field("mobile") String mobile,
                                   @Field("gender") String gender,
                                   @Field("password") String password,
                                   @Field("userdp") String userdp);
    @FormUrlEncoded
    @POST("/golgappe/{phpFile}")
    Call<ResponseBody> getResponse(@Path("phpFile") String file,
                                   @Field("query") String query);
}
