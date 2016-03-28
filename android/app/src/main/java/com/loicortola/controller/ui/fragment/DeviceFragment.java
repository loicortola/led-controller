package com.loicortola.controller.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loicortola.controller.ui.adapter.DeviceAdapter;
import com.loicortola.controller.command.CheckSecretKeyCommand;
import com.loicortola.controller.command.behavior.Secured;
import com.loicortola.controller.model.Device;
import com.loicortola.ledcontroller.R;
import com.loicortola.controller.service.DeviceService;

import java.util.List;


/**
 * Created by loic on 28/03/2016.
 */
public class DeviceFragment extends Fragment implements DeviceAdapter.DeviceCardClickListener, DeviceSetupDialogFragment.OnSettingsChangedListener {

    public interface NavigationListener {
        void onDeviceSelected(String deviceId);
    }

    private RecyclerView mRecyclerView;
    private DeviceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DeviceService mDeviceService;
    private NavigationListener l;
    public static DeviceFragment newInstance() {
        DeviceFragment f = new DeviceFragment();
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        l = (NavigationListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_device_overview, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDeviceService = new DeviceService(getActivity());

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Looking for nearby devices", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mDeviceService.refresh(new DeviceService.OnDeviceResolvedListener() {
                    @Override
                    public void onDeviceResolved(Device d) {
                        Snackbar.make(view, "Found device " + d.getId(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        updateItems(mDeviceService.getAll());
                    }
                });
            }
        });

        List<Device> all = mDeviceService.getAll();
        mAdapter = new DeviceAdapter(all, this);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }


    @Override
    public void onSetupBtnClicked(String deviceId) {
        Device device = mDeviceService.get(deviceId);
        if (device.getKey() == null && device.getResolver().isSecure()) {
            // Device is secured, no key was yet specified.
            DialogFragment dialog = DeviceSetupDialogFragment.newInstance(deviceId);
            dialog.setTargetFragment(this, 0x01);
            dialog.show(getActivity().getSupportFragmentManager(), "ChangeSettingsDialog");
        }
    }

    @Override
    public void onCardClicked(String deviceId) {
        l.onDeviceSelected(deviceId);
    }

    @Override
    public void onSecretKeySubmit(String deviceId, final String key) {
        final Device device = mDeviceService.get(deviceId);

        Object remoteControl = device.getResolver().getRemoteControl(device);
        new CheckSecretKeyCommand((Secured) remoteControl, key, new Secured.OnValidityCheckedListener() {
            @Override
            public void onValidityChecked(boolean valid) {
                if (valid) {
                    device.setKey(key);
                    mDeviceService.save(device);
                    Toast.makeText(getActivity(), "Secret Key Updated successfully", Toast.LENGTH_LONG).show();
                    updateItems(mDeviceService.getAll());
                } else {
                    Toast.makeText(getActivity(), "Wrong Secret Key.", Toast.LENGTH_LONG).show();
                }
            }
        }).execute();

    }

    private void updateItems(List<Device> devices) {
        mAdapter.setItems(mDeviceService.getAll());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
