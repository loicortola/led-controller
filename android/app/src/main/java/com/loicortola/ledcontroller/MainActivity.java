package com.loicortola.ledcontroller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Loïc Ortola on 23/01/2016.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, LedController.LedControllerListener {

    private ColorPicker picker;
    private ValueBar redBar;
    private ValueBar greenBar;
    private ValueBar blueBar;
    private SeekBar timeBar;
    private Button changeColorBtn;
    private Button animateBtn;
    private LedController ledController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);

        redBar = (ValueBar) findViewById(R.id.slide_red);
        greenBar = (ValueBar) findViewById(R.id.slide_green);
        blueBar = (ValueBar) findViewById(R.id.slide_blue);
        timeBar = (SeekBar) findViewById(R.id.slide_time);

        changeColorBtn = (Button) findViewById(R.id.btn_change);
        animateBtn = (Button) findViewById(R.id.btn_animate);

        ledController = new LedController(this, this);

        // Add SVBar to Picker
        picker.addSVBar(svBar);

        // Set default valueBars values
        redBar.setColor(Color.RED);
        greenBar.setColor(Color.GREEN);
        blueBar.setColor(Color.BLUE);
        // Turn off showing old color
        picker.setShowOldCenterColor(false);
        picker.setColor(Color.rgb(128, 0, 255));
        // Set listeners
        changeColorBtn.setOnClickListener(this);
        animateBtn.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_change:
                ledController.changeColor(picker.getColor());
                break;
            case R.id.btn_animate:
                int color = redBar.getColor() + greenBar.getColor() + blueBar.getColor();
                ledController.animate(color, timeBar.getProgress() + 8);
                Toast.makeText(this, "(" + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onColorChange(Boolean success) {
        Toast.makeText(this, "Color changed: " + success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimate(Boolean success) {
        Toast.makeText(this, "Animate: " + success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckOnline(Boolean success) {
        Toast.makeText(this, "Online: " + success, Toast.LENGTH_LONG).show();
    }
}
