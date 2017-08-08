package com.loicortola.controller.resolver;

import com.loicortola.controller.device.DeviceTypeResolverService;
import com.loicortola.controller.service.DeviceService;

/**
 * Created by loicortola on 08/08/2017.
 */

public class MockedResolver {

    public MockedResolver(DeviceTypeResolverService deviceTypeResolverService, DeviceService.OnDeviceResolvedListener l) {
        // No Op

    }

    public void run() {
        // No Op
    }

    public void stop() {
        // No Op
    }
}
