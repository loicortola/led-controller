package com.loicortola.controller.ui.adapter;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loicortola.controller.command.ChangeColorCommand;
import com.loicortola.controller.command.CheckHealthCommand;
import com.loicortola.controller.command.CheckSecretKeyCommand;
import com.loicortola.controller.command.SwitchCommand;
import com.loicortola.controller.command.behavior.Colorable;
import com.loicortola.controller.command.behavior.HealthAware;
import com.loicortola.controller.command.behavior.Secured;
import com.loicortola.controller.command.behavior.Switchable;
import com.loicortola.ledcontroller.R;
import com.loicortola.controller.model.Device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by loic on 28/03/2016.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private List<Device> mDataset;
    private DeviceCardClickListener l;

    public interface DeviceCardClickListener {
        void onSetupBtnClicked(String deviceId);
        void onCardClicked(String deviceId);
        void onCardLongClicked(String deviceId);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Secured.OnValidityCheckedListener, Switchable.OnSwitchListener, Colorable.OnColorGetListener, HealthAware.OnHealthCheckListener, View.OnLongClickListener {

        public String deviceId;
        public View rootView;
        public ImageView mDeviceIcon;
        public TextView mDeviceName;
        public ImageView mWidgetSwitch;
        public CircleImageView mWidgetColor;
        public ImageView mWidgetHealthy;
        public FloatingActionButton mSetupBtn;
        public DeviceCardClickListener l;

        public ViewHolder(View v, DeviceCardClickListener l) {
            super(v);
            this.l = l;
            rootView = v.findViewById(R.id.wrapper);
            mDeviceIcon = (ImageView) v.findViewById(R.id.device_icon);
            mWidgetColor = (CircleImageView) v.findViewById(R.id.widget_color);
            mWidgetSwitch = (ImageView) v.findViewById(R.id.widget_switch);
            mWidgetHealthy = (ImageView) v.findViewById(R.id.widget_healthy);
            mDeviceName = (TextView) v.findViewById(R.id.device_name);
            mSetupBtn = (FloatingActionButton) v.findViewById(R.id.btn_setup);
            mSetupBtn.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_setup:
                    if (l != null) {
                        l.onSetupBtnClicked(deviceId);
                    }
                    break;
                default:
                    if (l != null) {
                        l.onCardClicked(deviceId);
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            l.onCardLongClicked(deviceId);
            return false;
        }

        @Override
        public void onColorGet(Integer c) {
            if (c != null) {
                mWidgetColor.setColorFilter(c);
            }
        }

        @Override
        public void onHealthCheck(boolean isHealthy) {
            mWidgetHealthy.setImageResource(isHealthy ? android.R.drawable.presence_online : android.R.drawable.presence_offline);
        }

        @Override
        public void onActiveResult(Boolean isActive) {
            if (isActive != null) {
                mWidgetSwitch.setImageResource(isActive ? R.drawable.power_on : R.drawable.power_off);
            }
        }

        @Override
        public void onValidityChecked(boolean valid) {
            mSetupBtn.setVisibility(valid ? View.GONE : View.VISIBLE);
            if (valid) {
                rootView.setAlpha(1);
                rootView.setOnClickListener(this);
            } else {
                rootView.setAlpha(0.5f);
                rootView.setOnClickListener(null);
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceAdapter(List<Device> dataset, DeviceCardClickListener l) {
        this.mDataset = new ArrayList<>();
        if (dataset != null) {
            mDataset.addAll(dataset);
        }
        this.l = l;
    }

    public void setItems(Collection<Device> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_device, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v, l);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Device d = mDataset.get(position);
        holder.deviceId = d.getId();
        holder.mDeviceName.setText(d.getName());
        holder.mDeviceIcon.setImageResource(d.getIconDrawable());

        // Switch Widget
        holder.mWidgetColor.setImageResource(R.drawable.power_off);
        holder.mWidgetSwitch.setVisibility(d.supports(SwitchCommand.class) ? View.VISIBLE : View.GONE);

        // Health Widget
        holder.mWidgetColor.setBackgroundResource(android.R.drawable.presence_offline);
        holder.mWidgetHealthy.setVisibility(d.supports(CheckHealthCommand.class) ? View.VISIBLE : View.GONE);

        // Color Widget
        holder.mWidgetColor.setColorFilter(Color.GRAY);
        holder.mWidgetColor.setVisibility(d.supports(ChangeColorCommand.class) ? View.VISIBLE : View.GONE);

        // Secured Device
        if (d.supports(CheckSecretKeyCommand.class)) {
            if (d.getKey() == null || d.getKey().trim().isEmpty()) {
                // Assuming check was not done
                holder.onValidityChecked(false);
                ((Secured)d.getRemoteControl()).isValid(d.getKey(), holder);
                return;
            }
        }

        holder.onValidityChecked(true);

        // Switch Widget
        if (d.supports(SwitchCommand.class)) {
            ((Switchable)d.getRemoteControl()).isActive(holder);
        }

        // Health Widget
        if (d.supports(CheckHealthCommand.class)) {
            ((HealthAware)d.getRemoteControl()).isHealthy(holder);
        }

        // Color Widget
        if (d.supports(ChangeColorCommand.class)) {
            ((Colorable)d.getRemoteControl()).getColor(holder);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
