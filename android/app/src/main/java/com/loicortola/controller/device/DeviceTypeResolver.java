package com.loicortola.controller.device;

import android.net.nsd.NsdServiceInfo;

import com.loicortola.controller.command.Command;
import com.loicortola.controller.model.Device;

import io.resourcepool.jarpic.model.SsdpService;

/**
 * Created by loic on 28/03/2016.
 */
public interface DeviceTypeResolver<T> {
    boolean isSecure();
    boolean supports(NsdServiceInfo info);
    boolean supports(SsdpService service);
    boolean supports(Class<? extends Command> clazz);
    Device resolve(NsdServiceInfo info);
    Device resolve(SsdpService service);
    <T> T getRemoteControl(Device d);
}
