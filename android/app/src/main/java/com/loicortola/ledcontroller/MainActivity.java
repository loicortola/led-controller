package com.loicortola.ledcontroller;

import android.app.DialogFragment;
import android.graphics.Color;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.ValueBar;

/**
 * Created by Loïc Ortola on 23/01/2016.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, LedController.LedControllerListener, ChangeSettingsDialogFragment.OnSettingsChangedListener {

    private ColorPicker picker;
    private ValueBar redBar;
    private ValueBar greenBar;
    private ValueBar blueBar;
    private SeekBar timeBar;
    private ImageButton powerBtn;
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

        powerBtn = (ImageButton) findViewById(R.id.btn_power);

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
        powerBtn.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        ledController.cancelTasks();
        super.onPause();
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
        if (id == R.id.action_sync) {
            // Trigger service resolution.
            new MulticastDNSResolver(getApplicationContext(), "_http._tcp.", "led-", new MulticastDNSResolver.OnServiceResolved() {
                @Override
                public void onServiceResolved(NsdServiceInfo info) {
                    // FIXME dirty
                    ledController.setHost("http://" + info.getHost().getHostAddress() + ":" + info.getPort());
                    DialogFragment dialog = new ChangeSettingsDialogFragment();
                    dialog.show(getFragmentManager(), "ChangeSettingsDialog");
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change:
                ledController.changeColor(picker.getColor());
                break;
            case R.id.btn_animate:
                int color = redBar.getColor() + greenBar.getColor() + blueBar.getColor();
                ledController.animate(color, timeBar.getProgress() + 8);
                break;
            case R.id.btn_power:
                ledController.switchOnOff();
                break;
        }
    }

    @Override
    public void onSwitched(Boolean success) {
        Toast.makeText(this, "Switched: " + success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onColorChange(Boolean success) {
        Toast.makeText(this, "Color changed: " + success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAnimate(Boolean success) {
        Toast.makeText(this, "Animation: " + success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckOnline(Boolean success) {
        if (!success) {
            // Maybe IP Has changed. Try to resolve.
            new MulticastDNSResolver(getApplicationContext(), "_http._tcp.", "led-", new MulticastDNSResolver.OnServiceResolved() {
                @Override
                public void onServiceResolved(NsdServiceInfo info) {
                    // FIXME dirty
                    ledController.setHost("http://" + info.getHost().getHostAddress() + ":" + info.getPort());
                }
            });
        }
        Toast.makeText(this, "Online: " + success, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSettingsChanged(String key) {
        ledController.setKey(key);
        Toast.makeText(this, "Key Updated successfully", Toast.LENGTH_LONG).show();
    }
}
