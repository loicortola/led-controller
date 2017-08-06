package com.loicortola.controller.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.loicortola.controller.command.AnimateCommand;
import com.loicortola.controller.command.ChangeColorCommand;
import com.loicortola.controller.command.LoadPresetCommand;
import com.loicortola.controller.command.SwitchCommand;
import com.loicortola.controller.command.behavior.Animable;
import com.loicortola.controller.command.behavior.Colorable;
import com.loicortola.controller.command.behavior.PresetAware;
import com.loicortola.controller.command.behavior.Switchable;
import com.loicortola.controller.model.Animation;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.model.Preset;
import com.loicortola.controller.service.DeviceService;
import com.loicortola.controller.ui.adapter.PresetAdapter;
import com.loicortola.ledcontroller.R;

/**
 * Created by loic on 28/03/2016.
 */
public class RemoteControlFragment extends Fragment implements View.OnClickListener, Animable.OnAnimationSetListener, Switchable.OnSwitchListener, Colorable.OnColorChangedListener, PresetAdapter.PresetClickListener, PresetAware.OnPresetChangedListener {

    private static final String ARG_DEVICE_ID = "device_id";

    private ColorPicker picker;
    private PresetAdapter adapter;
    private RecyclerView presetsView;
    private ValueBar redBar;
    private ValueBar greenBar;
    private ValueBar blueBar;
    private SeekBar timeBar;
    private ImageButton powerBtn;
    private FloatingActionButton changeColorBtn;
    private FloatingActionButton animateBtn;
    private DeviceService deviceService;
    private Device currentDevice;
    private ChangeColorCommand changeColorCommand;

    public static RemoteControlFragment newInstance(String deviceId) {
        RemoteControlFragment f = new RemoteControlFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DEVICE_ID, deviceId);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_remote_control, container, false);

        deviceService = new DeviceService(getActivity());

        currentDevice = deviceService.get(getArguments().getString(ARG_DEVICE_ID));

        // Put available commands on remote control
        setupPresetCommand(rootView, currentDevice.supports(LoadPresetCommand.class));
        setupAnimateCommand(rootView, currentDevice.supports(AnimateCommand.class));
        setupColorChangeCommand(rootView, currentDevice.supports(ChangeColorCommand.class));
        setupSwitchCommand(rootView, currentDevice.supports(SwitchCommand.class));

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_animate:
                int color = redBar.getColor() + greenBar.getColor() + blueBar.getColor();
                new AnimateCommand((Animable)currentDevice.getRemoteControl(), new Animation(color, (timeBar.getProgress() + 8) * 1000), this).execute();
                break;
            case R.id.btn_power:
                new SwitchCommand((Switchable) currentDevice.getRemoteControl(), this).execute();
                break;
        }
    }

    @Override
    public void onPresetClicked(Preset preset) {
        new LoadPresetCommand((PresetAware) currentDevice.getRemoteControl(), preset, this).execute();
    }


    private void setupColorChangeCommand(View rootView, boolean enabled) {
        if (enabled) {
            picker = (ColorPicker) rootView.findViewById(R.id.picker);
            SVBar svBar = (SVBar) rootView.findViewById(R.id.svbar);
            // Add SVBar to Picker
            picker.addSVBar(svBar);
            // Turn off showing old color
            picker.setShowOldCenterColor(false);
            picker.setColor(Color.rgb(128, 0, 255));
            // Set listeners
            picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                @Override
                public void onColorChanged(int color) {
                    if (changeColorCommand == null) {
                        changeColorCommand = new ChangeColorCommand((Colorable)currentDevice.getRemoteControl(), picker.getColor(), true, RemoteControlFragment.this);
                        changeColorCommand.execute();
                    }
                }
            });
            picker.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN) {
                        changeColorCommand = new ChangeColorCommand((Colorable)currentDevice.getRemoteControl(), picker.getColor(), RemoteControlFragment.this);
                        changeColorCommand.execute();
                    }
                    return false;
                }
            });
            svBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN) {
                        changeColorCommand = new ChangeColorCommand((Colorable)currentDevice.getRemoteControl(), picker.getColor(), RemoteControlFragment.this);
                        changeColorCommand.execute();
                    }
                    return false;
                }
            });
        } else {
            rootView.findViewById(R.id.widget_color).setVisibility(View.GONE);
        }
    }

    public void setupAnimateCommand(View rootView, boolean enabled) {
        if (enabled) {
            redBar = (ValueBar) rootView.findViewById(R.id.slide_red);
            greenBar = (ValueBar) rootView.findViewById(R.id.slide_green);
            blueBar = (ValueBar) rootView.findViewById(R.id.slide_blue);
            timeBar = (SeekBar) rootView.findViewById(R.id.slide_time);
            animateBtn = (FloatingActionButton) rootView.findViewById(R.id.btn_animate);
            // Set default valueBars values
            redBar.setColor(Color.RED);
            greenBar.setColor(Color.GREEN);
            blueBar.setColor(Color.BLUE);
            // Set listeners
            animateBtn.setOnClickListener(this);
        } else {
            rootView.findViewById(R.id.widget_anim).setVisibility(View.GONE);
        }
    }


    private void setupPresetCommand(View rootView, boolean enabled) {
        if (enabled) {
            presetsView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            presetsView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            adapter = new PresetAdapter(getActivity(), ((PresetAware)currentDevice.getRemoteControl()).getPresets(), RemoteControlFragment.this);
            presetsView.setAdapter(adapter);
        } else {
            rootView.findViewById(R.id.widget_presets).setVisibility(View.GONE);
        }
    }

    private void setupSwitchCommand(View rootView, boolean enabled) {
        if (enabled) {
            powerBtn = (ImageButton) rootView.findViewById(R.id.btn_power);
            powerBtn.setOnClickListener(this);
        } else {
            rootView.findViewById(R.id.widget_switch).setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationSet(boolean success) {
        Toast.makeText(getActivity(), "Animation sent " + (success ? "successfully" : "failed"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActiveResult(Boolean isActive) {
        Toast.makeText(getActivity(), "Led Controller is turned " + (isActive ? "on" : "off"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onColorSet(boolean success) {
        changeColorCommand = null;
    }


    @Override
    public void onPresetChanged(boolean success) {
        Toast.makeText(getActivity(), "Preset changed " + (success ? "successfully" : "did not work"), Toast.LENGTH_LONG).show();
    }
}
