package com.loicortola.controller.library.ledstrip.controller;

import android.graphics.Color;

import com.loicortola.controller.command.behavior.Animable;
import com.loicortola.controller.command.behavior.Colorable;
import com.loicortola.controller.command.behavior.HealthAware;
import com.loicortola.controller.command.behavior.PresetAware;
import com.loicortola.controller.command.behavior.Secured;
import com.loicortola.controller.command.behavior.Switchable;
import com.loicortola.controller.library.ledstrip.model.AnimationSet;
import com.loicortola.controller.model.Preset;
import com.loicortola.controller.library.ledstrip.model.Status;
import com.loicortola.controller.library.ledstrip.service.LedStripService;
import com.loicortola.controller.model.Animation;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by loic on 28/03/2016.
 */
public class LedStripRemoteControl implements Switchable, Colorable, Secured, Animable, HealthAware, PresetAware {

    private Device d;
    private LedStripService service;
    private List<Preset> presets;

    public LedStripRemoteControl(Device d) {
        this.d = d;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(d.getHost() + "/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        service = retrofit.create(LedStripService.class);
        presets = new ArrayList<>();
        new Preset()
                .stringResId(R.string.preset_ocean)
                .drawableResId(R.drawable.preset_ocean)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 100, 255, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(100, 255, 200, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(40, 128, 100, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(80, 255, 220, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(50, 100, 80, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 255, 180, 2000)))
                .addTo(presets);
        new Preset()
                .stringResId(R.string.preset_sunset)
                .drawableResId(R.drawable.preset_sunset)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(255, 100, 0, 8000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(255, 30, 0, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(100, 10, 0, 6000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(200, 50, 5, 2000)))
                .addTo(presets);
        new Preset()
                .stringResId(R.string.preset_uv)
                .drawableResId(R.drawable.preset_uv)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 0, 255, 8000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(128, 40, 128, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 20, 200, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(128, 0, 255, 4000)))
                .addTo(presets);
        new Preset()
                .stringResId(R.string.preset_forest)
                .drawableResId(R.drawable.preset_forest)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(72, 87, 0, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(33, 38, 1, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(172, 190, 42, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(226, 242, 132, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(238, 181, 111, 4000)))
                .addTo(presets);
    }

    @Override
    public void changeColor(int c, final OnColorChangedListener l) {
        service.changeColor("ApiKey " + d.getKey(), Color.red(c), Color.green(c), Color.blue(c), true).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onColorSet(response.code() == 200);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onColorSet(false);
            }
        });
    }

    @Override
    public void setColor(int c, final OnColorChangedListener l) {
        service.setColor("ApiKey " + d.getKey(), Color.red(c), Color.green(c), Color.blue(c)).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onColorSet(response.code() == 200);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onColorSet(false);
            }
        });
    }

    @Override
    public void getColor(final OnColorGetListener l) {
        service.getStatus(d.getKey()).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<com.loicortola.controller.library.ledstrip.model.Status> call, Response<com.loicortola.controller.library.ledstrip.model.Status> response) {
                com.loicortola.controller.library.ledstrip.model.Status c = response.body();
                if (c == null) {
                    l.onColorGet(null);
                } else {
                    switch(c.mode) {
                        case 1:
                            // Color mode
                            l.onColorGet(Color.rgb(c.color.r, c.color.g, c.color.b));
                            break;
                        case 2:
                        case 3:
                            // Animation mode
                            l.onColorGet(Color.rgb(128, 128, 128));
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<com.loicortola.controller.library.ledstrip.model.Status> call, Throwable t) {
                l.onColorGet(null);
            }
        });
    }

    @Override
    public void isActive(final OnSwitchListener l) {
        Call<Status> call = service.getStatus("ApiKey " + d.getKey());

        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                l.onActiveResult(response.body().switchedOn);
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                l.onActiveResult(null);
            }
        });
    }

    @Override
    public void switchOn(final OnSwitchListener l) {
        service.switchOnOff("ApiKey " + d.getKey(), 1).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onActiveResult(true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onActiveResult(null);
            }
        });
    }

    @Override
    public void switchOff(final OnSwitchListener l) {

        service.switchOnOff("ApiKey " + d.getKey(), 0).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onActiveResult(false);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onActiveResult(null);
            }
        });
    }

    @Override
    public void isValid(String key, final OnValidityCheckedListener l) {
        service.getStatus("ApiKey " + key).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<com.loicortola.controller.library.ledstrip.model.Status> call, Response<com.loicortola.controller.library.ledstrip.model.Status> response) {
                l.onValidityChecked(response.code() == 200);
            }

            @Override
            public void onFailure(Call<com.loicortola.controller.library.ledstrip.model.Status> call, Throwable t) {
                l.onValidityChecked(false);
            }
        });
    }


    @Override
    public void animate(Animation a, final OnAnimationSetListener l) {
        service.setAnimation("ApiKey " + d.getKey(), Color.red(a.c), Color.green(a.c), Color.blue(a.c), a.animationTime).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                l.onAnimationSet(response.code() == 200);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                l.onAnimationSet(false);
            }
        });
    }

    @Override
    public void isHealthy(final OnHealthCheckListener l) {
        service.checkHealth("ApiKey " + d.getKey()).enqueue(new Callback<ResponseBody>() {
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

    @Override
    public List<Preset> getPresets() {
        return Collections.unmodifiableList(presets);
    }

    @Override
    public void setPreset(Preset p, final OnPresetChangedListener l) {
        service.setAnimation("ApiKey " + d.getKey(), ((AnimationSet)p.content).queryString()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                l.onPresetChanged(response.code() == 200);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                l.onPresetChanged(false);
            }
        });
    }

    @Override
    public void getPreset(OnPresetLoadedListener l) {

    }
}
