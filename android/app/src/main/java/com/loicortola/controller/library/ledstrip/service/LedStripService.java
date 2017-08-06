package com.loicortola.controller.library.ledstrip.service;

import com.loicortola.controller.library.ledstrip.model.Color;
import com.loicortola.controller.library.ledstrip.model.Status;

import java.util.List;

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
    @GET("api/status")
    Call<Status> getStatus(@Header("Authorization") String key);

    @POST("api/color")
    Call<ResponseBody> setColor(@Header("Authorization") String key, @Query("red") int red, @Query("green") int green, @Query("blue") int blue);

    @POST("api/color")
    Call<ResponseBody> changeColor(@Header("Authorization") String key, @Query("red") int red, @Query("green") int green, @Query("blue") int blue, @Query("hover") boolean hover);

    @POST("api/animate")
    Call<ResponseBody> setAnimation(@Header("Authorization") String key, @Query("red") int red, @Query("green") int green, @Query("blue") int blue, @Query("looptime") long loopTime);

    @POST("api/animate?type=2")
    Call<ResponseBody> setAnimation(@Header("Authorization") String key, @Query("step") List<String> steps);

    @POST("api/power")
    Call<ResponseBody> switchOnOff(@Header("Authorization") String key, @Query("power") int power);

    @GET("health")
    Call<ResponseBody> checkHealth(@Header("Authorization") String key);


}
