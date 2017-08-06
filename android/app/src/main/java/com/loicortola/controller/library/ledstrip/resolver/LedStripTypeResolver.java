package com.loicortola.controller.library.ledstrip.resolver;

import android.net.nsd.NsdServiceInfo;

import com.loicortola.controller.command.AnimateCommand;
import com.loicortola.controller.command.ChangeColorCommand;
import com.loicortola.controller.command.CheckHealthCommand;
import com.loicortola.controller.command.CheckSecretKeyCommand;
import com.loicortola.controller.command.Command;
import com.loicortola.controller.command.LoadPresetCommand;
import com.loicortola.controller.command.SwitchCommand;
import com.loicortola.controller.library.ledstrip.controller.LedStripRemoteControl;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.resolver.DeviceTypeResolver;
import com.loicortola.ledcontroller.R;

import io.resourcepool.jarpic.model.SsdpService;

/**
 * Created by loic on 28/03/2016.
 */
public class LedStripTypeResolver implements DeviceTypeResolver<LedStripRemoteControl> {

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public boolean supports(NsdServiceInfo info) {
        return info.getServiceName().startsWith("led-") || info.getServiceName().toLowerCase().startsWith("resourcepool led controller");
    }

    @Override
    public boolean supports(SsdpService service) {
        return  service.getServiceType().equals("urn:schemas-upnp-org:device:DimmableRGBLight:2");
    }

    @Override
    public boolean supports(Class<? extends Command> clazz) {
        if (ChangeColorCommand.class.equals(clazz)) {
            return true;
        }

        if (CheckSecretKeyCommand.class.equals(clazz)) {
            return true;
        }

        if (SwitchCommand.class.equals(clazz)) {
            return true;
        }

        if (AnimateCommand.class.equals(clazz)) {
            return true;
        }

        if (CheckHealthCommand.class.equals(clazz)) {
            return true;
        }

        if (LoadPresetCommand.class.equals(clazz)) {
            return true;
        }

        return false;
    }

    @Override
    public Device resolve(NsdServiceInfo info) {
        return Device.builder()
                .id(info.getServiceName())
                .name(info.getServiceName())
                .icon(R.drawable.led_strip)
                .host("http://" + info.getHost().getHostAddress() + ":" + info.getPort())
                .resolver(this)
                .build();
    }

    @Override
    public Device resolve(SsdpService service) {
        return Device.builder()
                .id(service.getSerialNumber())
                .name("Led-Strip")
                .icon(R.drawable.led_strip)
                .host("http://" + service.getRemoteIp().getHostAddress() + ":80")
                .resolver(this)
                .build();
    }

    @Override
    public LedStripRemoteControl getRemoteControl(Device d) {
        return new LedStripRemoteControl(d);
    }
}
