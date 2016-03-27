package com.loicortola.ledcontroller;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

/**
 * Created by loic on 27/03/2016.
 */
public class MulticastDNSResolver {

    public interface OnServiceResolved {
        void onServiceResolved(NsdServiceInfo info);
    }

    private static final String TAG = MulticastDNSResolver.class.getName();

    private final String serviceType;
    private final String serviceName;
    private final OnServiceResolved listener;
    private final NsdManager nsdManager;

    public MulticastDNSResolver(Context ctx, String serviceType, String searchString, OnServiceResolved listener) {
        this.serviceType = serviceType;
        this.serviceName = searchString;
        this.nsdManager = (NsdManager) ctx.getSystemService(Context.NSD_SERVICE);
        this.listener = listener;
        initializeDiscoveryListener();
    }

    public void initializeDiscoveryListener() {
        final NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.w(TAG, "Fail!: " + serviceInfo.toString() + " : " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Win!: " + serviceInfo);
                listener.onServiceResolved(serviceInfo);
            }
        };

        // Instantiate a new DiscoveryListener
        NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found!  Do something with it.
                Log.d(TAG, "Service discovery success " + service);
                if (!service.getServiceType().equals(serviceType)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().contains(serviceName)) {
                    Log.d(TAG, "Found one!: " + service.getServiceName());
                    nsdManager.resolveService(service, resolveListener);
                    nsdManager.stopServiceDiscovery(this);
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
