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
    @GET("color")
    Call<Color> getColor(@Header("X-Api-Key") String key);

    @POST("color")
    Call<ResponseBody> setColor(@Header("X-Api-Key") String key, @Query("red") int red, @Query("green") int green, @Query("blue") int blue);

    @POST("animate")
    Call<ResponseBody> setAnimation(@Header("X-Api-Key") String key, @Query("red") int red, @Query("green") int green, @Query("blue") int blue, @Query("loopTime") long loopTime);

    @GET("switch")
    Call<Boolean> isActive(@Header("X-Api-Key") String key);

    @POST("switch")
    Call<ResponseBody> switchOnOff(@Header("X-Api-Key") String key);

    @GET("health")
    Call<ResponseBody> checkHealth(@Header("X-Api-Key") String key);


}
