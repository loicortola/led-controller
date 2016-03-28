package com.loicortola.controller.service;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Loïc Ortola on 23/01/2016.
 */
public class LedController {

    public static final String PREF_KEY = "pref_key";
    public static final String PREF_HOST = "pref_host";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private Context ctx;
    private CheckOnlineTask checkOnlineTask;
    private ChangeColorTask changeColorTask;
    private AnimateTask animateTask;
    private SwitchTask switchTask;
    private LedControllerListener l;

    public interface LedControllerListener {

        void onSwitched(Boolean success);

        void onColorChange(Boolean success);

        void onAnimate(Boolean success);

        void onCheckOnline(Boolean success);
    }

    public LedController(Context ctx, LedControllerListener l) {
        this.ctx = ctx.getApplicationContext();
        this.l = l;
        checkOnlineTask = new CheckOnlineTask();
        checkOnlineTask.execute();
    }

    public void setHost(String host) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(PREF_HOST, host).commit();
    }

    public void setKey(String key) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(PREF_KEY, key).commit();
    }

    public void changeColor(int color) {
        if (changeColorTask != null && changeColorTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            changeColorTask.cancel(true);
        }
        changeColorTask = new ChangeColorTask(color);
        changeColorTask.execute();
    }

    public void animate(int color, int time) {
        if (animateTask != null && animateTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            animateTask.cancel(true);
        }
        animateTask = new AnimateTask(color, time);
        animateTask.execute();
    }

    public void switchOnOff() {
        if (switchTask != null && switchTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            switchTask.cancel(true);
        }
        switchTask = new SwitchTask();
        switchTask.execute();
    }

    public void cancelTasks() {
        if (checkOnlineTask != null) {
            checkOnlineTask.cancel(true);
        }
        if (changeColorTask != null) {
            changeColorTask.cancel(true);
        }
        if (animateTask != null) {
            animateTask.cancel(true);
        }
        if (switchTask != null) {
            switchTask.cancel(true);
        }
    }

    private class CheckOnlineTask extends AsyncTask<Void, Void, Boolean> {


        public CheckOnlineTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = new StringBuilder(PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_HOST, "http://localhost"))
                    .append("/health")
                    .toString();
            Log.w("LedController", url);
            // Body is empty
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.w("LedController", "Response code: " + response.code());
                return response.code() == 200;
            } catch (IOException e) {
                Log.w("LedController", "Exception occured: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            l.onCheckOnline(success);
        }
    }

    private class SwitchTask extends AsyncTask<Void, Void, Boolean> {

        public SwitchTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = new StringBuilder(PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_HOST, "http://localhost"))
                    .append("/switch")
                    .toString();
            // Body is empty
            RequestBody body = RequestBody.create(JSON, "{}");
            Request request = new Request.Builder()
                    .header("x-api-key", PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_KEY, ""))
                    .url(url)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.code() == 200;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            l.onSwitched(success);
        }
    }

    private class ChangeColorTask extends AsyncTask<Void, Void, Boolean> {

        int red;
        int green;
        int blue;

        public ChangeColorTask(int color) {
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = new StringBuilder(PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_HOST, "http://localhost"))
                    .append("/color?red=")
                    .append(red)
                    .append("&green=")
                    .append(green)
                    .append("&blue=")
                    .append(blue)
                    .toString();
            // Body is empty
            RequestBody body = RequestBody.create(JSON, "{}");
            Request request = new Request.Builder()
                    .header("x-api-key", PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_KEY, ""))
                    .url(url)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.code() == 200;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            l.onColorChange(success);
        }
    }

    private class AnimateTask extends AsyncTask<Void, Void, Boolean> {

        int red;
        int green;
        int blue;
        int time;

        public AnimateTask(int color, int time) {
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);
            this.time = time;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = new StringBuilder(PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_HOST, "http://localhost"))
                    .append("/animate?red=")
                    .append(red)
                    .append("&green=")
                    .append(green)
                    .append("&blue=")
                    .append(blue)
                    .append("&time=")
                    .append(time)
                    .toString();
            // Body is empty
            RequestBody body = RequestBody.create(JSON, "{}");
            Request request = new Request.Builder()
                    .header("x-api-key", PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_KEY, ""))
                    .url(url)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.code() == 200;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            l.onAnimate(success);
        }
    }

}
