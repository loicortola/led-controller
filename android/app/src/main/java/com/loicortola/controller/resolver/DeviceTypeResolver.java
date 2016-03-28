package com.loicortola.controller.resolver;

import android.net.nsd.NsdServiceInfo;

import com.loicortola.controller.command.Command;
import com.loicortola.controller.model.Device;

/**
 * Created by loic on 28/03/2016.
 */
public interface DeviceTypeResolver<T> {
    boolean isSecure();
    boolean supports(NsdServiceInfo info);
    boolean supports(Class<? extends Command> clazz);
    Device resolve(NsdServiceInfo info);
    <T> T getRemoteControl(Device d);


}
