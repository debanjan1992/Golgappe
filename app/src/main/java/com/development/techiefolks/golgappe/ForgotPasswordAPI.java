package com.development.techiefolks.golgappe;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by deban on 01-03-2016.
 */
public interface ForgotPasswordAPI {
    @FormUrlEncoded
    @POST("/golgappe/sendMailFP.php")
    Call<ResponseBody> sendPasswordResetMail(@Field("email") String email, @Field("code") String code);
}
