package com.loicortola.controller.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.loicortola.controller.R;

/**
 * Created by loic on 27/03/2016.
 */
public class DeviceSetupDialogFragment extends DialogFragment {

    public static final String ARG_DEVICE_ID = "device_id";

    public static DeviceSetupDialogFragment newInstance(String deviceId) {
        DeviceSetupDialogFragment d = new DeviceSetupDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DEVICE_ID, deviceId);
        d.setArguments(bundle);
        return d;
    }

    private OnSettingsChangedListener l;

    public interface OnSettingsChangedListener {
        void onSecretKeySubmit(String deviceId, String name, String key);
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
        l = (OnSettingsChangedListener) fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final String deviceId = getArguments().getString(ARG_DEVICE_ID);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View rootView = inflater.inflate(R.layout.dialog_sync, null);
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        l.onSecretKeySubmit(deviceId, ((TextView)rootView.findViewById(R.id.device_name)).getText().toString(), ((TextView)rootView.findViewById(R.id.key)).getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeviceSetupDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
