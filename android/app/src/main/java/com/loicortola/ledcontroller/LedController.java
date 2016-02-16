package com.loicortola.ledcontroller;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
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
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private Context ctx;
    private CheckOnlineTask checkOnlineTask;
    private ChangeColorTask changeColorTask;
    private AnimateTask animateTask;
    private LedControllerListener l;

    public interface LedControllerListener {
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
    }

    private class CheckOnlineTask extends AsyncTask<Void, Void, Boolean> {


        public CheckOnlineTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = new StringBuilder(ctx.getString(R.string.controller_endpoint))
                    .append("/color")
                    .toString();
            Log.w("LedController", url);
            // Body is empty
            Request request = new Request.Builder()
                    .header("api-key", ctx.getString(R.string.api_key))
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
            String url = new StringBuilder(ctx.getString(R.string.controller_endpoint))
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
                    .header("api-key", ctx.getString(R.string.api_key))
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
            String url = new StringBuilder(ctx.getString(R.string.controller_endpoint))
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
                    .header("api-key", ctx.getString(R.string.api_key))
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
