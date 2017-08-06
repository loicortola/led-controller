package com.loicortola.controller.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.loicortola.controller.model.Device;
import com.loicortola.controller.model.Preset;
import com.loicortola.ledcontroller.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by loic on 28/03/2016.
 */
public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.ViewHolder> {

    private final Context mCtx;
    private List<Preset> mDataset;
    private PresetClickListener l;

    public interface PresetClickListener {
        void onPresetClicked(Preset preset);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Preset preset;
        public View rootView;
        public ImageView mPresetIcon;
        public TextView mPresetLabel;
        public PresetClickListener l;

        public ViewHolder(View v, PresetClickListener l) {
            super(v);
            this.l = l;
            rootView = v.findViewById(R.id.wrapper);
            mPresetIcon = (ImageView) v.findViewById(R.id.preset_icon);
            mPresetLabel = (TextView) v.findViewById(R.id.preset_label);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            rootView.setBackgroundColor(0x009999bb);
            if (l != null) {
                l.onPresetClicked(preset);
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PresetAdapter(Context ctx, List<Preset> dataset, PresetClickListener l) {
        this.mCtx = ctx;
        this.mDataset = new ArrayList<>();
        if (dataset != null) {
            mDataset.addAll(dataset);
        }
        this.l = l;
    }

    public void setItems(Collection<Preset> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PresetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_preset, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v, l);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Preset p = mDataset.get(position);
        holder.preset = p;
        holder.mPresetLabel.setText(mCtx.getString(p.stringResId));
        holder.mPresetIcon.setImageDrawable(mCtx.getResources().getDrawable(p.drawableResId, null));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
