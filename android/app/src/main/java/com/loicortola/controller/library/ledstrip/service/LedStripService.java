package com.loicortola.controller.library.ledstrip.service;

import com.loicortola.controller.library.ledstrip.model.Color;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by loic on 28/03/2016.
 */
public interface LedStripService {
    @GET("api/color")
    Call<Color> getColor(@Header("Authorization") String key);

    @POST("api/color")
    Call<ResponseBody> setColor(@Header("Authorization") String key, @Query("red") int red, @Query("green") int green, @Query("blue") int blue);

    @POST("api/animate")
    Call<ResponseBody> setAnimation(@Header("Authorization") String key, @Query("red") int red, @Query("green") int green, @Query("blue") int blue, @Query("looptime") long loopTime);

    @GET("api/status")
    Call<Boolean> isActive(@Header("Authorization") String key);

    @POST("api/switch")
    Call<ResponseBody> switchOnOff(@Header("Authorization") String key);

    @GET("health")
    Call<ResponseBody> checkHealth(@Header("Authorization") String key);


}
