package com.loicortola.ledcontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by loic on 27/03/2016.
 */
public class ChangeSettingsDialogFragment extends DialogFragment {

    private OnSettingsChangedListener l;

    public interface OnSettingsChangedListener {
        void onSettingsChanged(String key);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        l = (OnSettingsChangedListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View rootView = inflater.inflate(R.layout.dialog_sync, null);
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        l.onSettingsChanged(((TextView)rootView.findViewById(R.id.key)).getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangeSettingsDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
