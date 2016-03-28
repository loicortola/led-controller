package com.loicortola.controller.library.ledstrip.controller;

import android.graphics.Color;

import com.loicortola.controller.command.behavior.Animable;
import com.loicortola.controller.command.behavior.Colorable;
import com.loicortola.controller.command.behavior.HealthAware;
import com.loicortola.controller.command.behavior.Secured;
import com.loicortola.controller.command.behavior.Switchable;
import com.loicortola.controller.library.ledstrip.service.LedStripService;
import com.loicortola.controller.model.Animation;
import com.loicortola.controller.model.Device;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by loic on 28/03/2016.
 */
public class LedStripRemoteControl implements Switchable, Colorable, Secured, Animable, HealthAware {

    private Device d;
    private LedStripService service;

    public LedStripRemoteControl(Device d) {
        this.d = d;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(d.getHost() + "/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        service = retrofit.create(LedStripService.class);
    }

    @Override
    public void setColor(int c, final OnColorSetListener l) {
        service.setColor(d.getKey(), Color.red(c), Color.green(c), Color.blue(c)).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onColorSet(true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onColorSet(false);
            }
        });
    }

    @Override
    public void getColor(final OnColorGetListener l) {
        service.getColor(d.getKey()).enqueue(new Callback<com.loicortola.controller.library.ledstrip.model.Color>() {
            @Override
            public void onResponse(Call<com.loicortola.controller.library.ledstrip.model.Color> call, Response<com.loicortola.controller.library.ledstrip.model.Color> response) {
                com.loicortola.controller.library.ledstrip.model.Color c = response.body();
                if (c == null) {
                    l.onColorGet(null);
                } else {
                    l.onColorGet(Color.rgb(c.red, c.green, c.blue));
                }
            }

            @Override
            public void onFailure(Call<com.loicortola.controller.library.ledstrip.model.Color> call, Throwable t) {
                l.onColorGet(null);
            }
        });
    }

    @Override
    public void isActive(final OnSwitchListener l) {
        Call<Boolean> call = service.isActive(d.getKey());

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                l.onActiveResult(response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                l.onActiveResult(null);
            }
        });
    }

    @Override
    public void switchOn(final OnSwitchListener l) {
        service.switchOnOff(d.getKey()).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onActiveResult(true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onActiveResult(false);
            }
        });
    }

    @Override
    public void switchOff(final OnSwitchListener l) {
        service.switchOnOff(d.getKey()).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onActiveResult(false);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onActiveResult(true);
            }
        });
    }

    @Override
    public void isValid(String key, final OnValidityCheckedListener l) {
        service.getColor(key).enqueue(new Callback<com.loicortola.controller.library.ledstrip.model.Color>() {
            @Override
            public void onResponse(Call<com.loicortola.controller.library.ledstrip.model.Color> call, Response<com.loicortola.controller.library.ledstrip.model.Color> response) {
                l.onValidityChecked(response.code() == 200);
            }

            @Override
            public void onFailure(Call<com.loicortola.controller.library.ledstrip.model.Color> call, Throwable t) {
                l.onValidityChecked(false);
            }
        });
    }


    @Override
    public void animate(Animation a, final OnAnimationSetListener l) {
        service.setAnimation(d.getKey(), Color.red(a.c), Color.green(a.c), Color.blue(a.c), a.animationTime).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onAnimationSet(true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onAnimationSet(false);
            }
        });
    }

    @Override
    public void isHealthy(final OnHealthCheckListener l) {
        service.checkHealth(d.getKey()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                l.onHealthCheck(response.code() == 200);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                l.onHealthCheck(false);
            }
        });
    }
}
