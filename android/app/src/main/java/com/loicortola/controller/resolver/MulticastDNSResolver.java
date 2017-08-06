package com.loicortola.controller.resolver;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;

/**
 * Created by loic on 27/03/2016.
 */
public class MulticastDNSResolver implements Runnable {

    public interface OnServiceResolvedListener {
        void onServiceResolved(NsdServiceInfo info);
        void onStopped();
    }

    private static final String TAG = MulticastDNSResolver.class.getName();
    private static final long TIMEOUT_MILLIS = 30000;

    private final String serviceType;
    private final OnServiceResolvedListener listener;
    private final NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener;
    private Runnable stopper;
    private Handler handler;

    public MulticastDNSResolver(Context ctx, Handler handler, String serviceType, OnServiceResolvedListener listener) {
        this.serviceType = serviceType;
        this.nsdManager = (NsdManager) ctx.getSystemService(Context.NSD_SERVICE);
        this.listener = listener;
        this.handler = handler;
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
        nsdManager.stopServiceDiscovery(discoveryListener);
        handler.removeCallbacks(stopper);
        listener.onStopped();
    }

    private void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        discoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success " + service);
                if (service.getServiceType().equals(serviceType)) {
                    Log.d(TAG, "Found one!: " + service.getServiceName());
                    nsdManager.resolveService(service, new NsdManager.ResolveListener() {
                        @Override
                        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                            if (errorCode != 3) {
                                Log.w(TAG, "Fail!: " + serviceInfo.toString() + " : " + errorCode);
                            } else {
                                listener.onServiceResolved(serviceInfo);
                            }
                        }

                        @Override
                        public void onServiceResolved(NsdServiceInfo serviceInfo) {
                            listener.onServiceResolved(serviceInfo);
                        }
                    });
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };

        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }
}
