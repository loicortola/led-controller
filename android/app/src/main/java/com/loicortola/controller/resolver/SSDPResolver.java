package com.loicortola.controller.resolver;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.loicortola.controller.device.DeviceTypeResolver;
import com.loicortola.controller.device.DeviceTypeResolverService;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.service.DeviceService;

import io.resourcepool.jarpic.client.SsdpClient;
import io.resourcepool.jarpic.client.SsdpClientImpl;
import io.resourcepool.jarpic.model.DiscoveryListener;
import io.resourcepool.jarpic.model.SsdpRequest;
import io.resourcepool.jarpic.model.SsdpService;
import io.resourcepool.jarpic.model.SsdpServiceAnnouncement;

/**
 * Created by loic on 06/08/2017.
 */
public class SSDPResolver implements Runnable {

    public interface OnServiceResolvedListener {
        void onServiceResolved(SsdpService service);
        void onStopped();
    }

    private static final String TAG = SSDPResolver.class.getName();
    private static final long TIMEOUT_MILLIS = 30000;

    private final DeviceService.OnDeviceResolvedListener listener;
    private DiscoveryListener discoveryListener;
    private final DeviceTypeResolverService deviceTypeResolverService;
    private final SsdpClient resolver;
    private Runnable stopper;
    private Handler handler;

    public SSDPResolver(Context ctx, Handler handler, DeviceTypeResolverService deviceTypResolverService, DeviceService.OnDeviceResolvedListener listener) {
        this.listener = listener;
        this.handler = handler;
        this.deviceTypeResolverService = deviceTypResolverService;
        this.resolver = new SsdpClientImpl();
        this.stopper = new Runnable() {
            @Override
            public void run() {
                // Stop after cycle is finished
                stop();
            }
        };
        // Stop after 30 seconds
        handler.postDelayed(stopper, TIMEOUT_MILLIS);
    }

    @Override
    public void run() {
        initializeDiscoveryListener();
    }

    public void stop() {
        resolver.stopDiscovery();
        handler.removeCallbacks(stopper);
        listener.onStopped();
    }

    private void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        discoveryListener = new DiscoveryListener() {
            @Override
            public void onServiceDiscovered(SsdpService service) {
                DeviceTypeResolver resolver = deviceTypeResolverService.get(service);
                if (resolver != null) {
                    Device newDevice = resolver.resolve(service);
                    listener.onDeviceResolved(newDevice);
                }
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                // Do nothing
            }

            @Override
            public void onFailed(Exception ex) {
                Log.w(TAG, "Something bad happened: " + ex.getMessage());
            }
        };

        resolver.discoverServices(SsdpRequest.discoverAll(), discoveryListener);
    }
}
