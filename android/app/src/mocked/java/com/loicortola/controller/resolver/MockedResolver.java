package com.loicortola.controller.resolver;

import com.loicortola.controller.R;
import com.loicortola.controller.device.DeviceTypeResolverService;
import com.loicortola.controller.library.ledstrip.resolver.LedStripTypeResolver;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.service.DeviceService;

/**
 * Created by loicortola on 08/08/2017.
 */

public class MockedResolver {


    private final DeviceService.OnDeviceResolvedListener l;
    private final Device device1;
    private final Device device2;
    private final Device device3;


    public MockedResolver(DeviceTypeResolverService deviceTypeResolverService, DeviceService.OnDeviceResolvedListener l) {
        this.l = l;
        device1 = Device.builder()
                .id("1234")
                .host("mock://0.0.0.0")
                .icon(R.drawable.led_strip)
                .name("Bedroom LEDs")
                .resolver(deviceTypeResolverService.get(LedStripTypeResolver.class.getSimpleName()))
                .build();
        device2 = Device.builder()
                .id("1235")
                .host("mock://0.0.0.0")
                .icon(R.drawable.led_strip)
                .name("Roundtable LEDs")
                .resolver(deviceTypeResolverService.get(LedStripTypeResolver.class.getSimpleName()))
                .build();
        device3 = Device.builder()
                .id("1236")
                .host("mock://0.0.0.0")
                .icon(R.drawable.led_strip)
                .name("Bathroom LEDs")
                .resolver(deviceTypeResolverService.get(LedStripTypeResolver.class.getSimpleName()))
                .build();
    }

    public void run() {
        l.onDeviceResolved(device1);
        l.onDeviceResolved(device2);
        l.onDeviceResolved(device3);
    }

    public void stop() {
        // No Op
    }
}
