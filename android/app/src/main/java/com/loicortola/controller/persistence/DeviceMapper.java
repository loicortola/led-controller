package com.loicortola.controller.persistence;

import com.loicortola.controller.device.DeviceTypeResolverService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loic on 28/03/2016.
 */
public class DeviceMapper {

    private DeviceTypeResolverService resolverService;

    public DeviceMapper() {

    }

    public DeviceMapper(DeviceTypeResolverService rs) {
        this.resolverService = rs;
    }

    public com.loicortola.controller.model.Device map(Device d) {
        if (d == null) {
            return null;
        }
        return com.loicortola.controller.model.Device.builder()
                .dbId(d.getId())
                .id(d.getDeviceId())
                .icon((int) d.getIcon())
                .name(d.getName())
                .host(d.getHost())
                .key(d.getKey())
                .resolver(resolverService.get(d.getDeviceResolver()))
                .build();
    }

    public Device map(com.loicortola.controller.model.Device d) {
        if (d == null) {
            return null;
        }
        return new Device(d.getDbId(), d.getId(), d.getResolver().getClass().getSimpleName(), d.getName(), d.getIconDrawable(), d.getHost(), d.getKey());
    }

    public List<com.loicortola.controller.model.Device> mapFromEntity(List<Device> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<com.loicortola.controller.model.Device> r = new ArrayList<>(list.size());
        for (Device d : list) {
            r.add(map(d));
        }
        return r;
    }

    public List<Device> mapToEntity(List<com.loicortola.controller.model.Device> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<Device> r = new ArrayList<>(list.size());
        for (com.loicortola.controller.model.Device d : list) {
            r.add(map(d));
        }
        return r;
    }
}
