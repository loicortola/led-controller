package com.loicortola.controller.service;

import android.net.nsd.NsdServiceInfo;

import com.loicortola.controller.resolver.DeviceTypeResolver;
import com.loicortola.controller.library.ledstrip.resolver.LedStripTypeResolver;

import java.util.ArrayList;
import java.util.List;

import io.resourcepool.jarpic.model.SsdpService;

/**
 * Created by loic on 28/03/2016.
 */
public class DeviceTypeResolverService {

    private List<DeviceTypeResolver> typeResolvers;

    public DeviceTypeResolverService() {
        typeResolvers = new ArrayList<>();
        typeResolvers.add(new LedStripTypeResolver());
    }

    public void addTypeResolver(DeviceTypeResolver r) {
        typeResolvers.add(r);
    }

    public DeviceTypeResolver get(String name) {
        for (DeviceTypeResolver r : typeResolvers) {
            if (r.getClass().getSimpleName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public DeviceTypeResolver get(NsdServiceInfo info) {
        for (DeviceTypeResolver r : typeResolvers) {
            if (r.supports(info)) {
                return r;
            }
        }
        return null;
    }

    public DeviceTypeResolver get(SsdpService info) {
        for (DeviceTypeResolver r : typeResolvers) {
            if (r.supports(info)) {
                return r;
            }
        }
        return null;
    }
}
