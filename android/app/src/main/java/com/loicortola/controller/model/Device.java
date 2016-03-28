package com.loicortola.controller.model;

import com.loicortola.controller.command.Command;
import com.loicortola.controller.resolver.DeviceTypeResolver;
import com.loicortola.ledcontroller.R;

/**
 * Created by loic on 28/03/2016.
 */
public class Device {

    protected Long dbId;
    protected String id;
    protected int iconDrawable;
    protected String name;
    protected String host;
    protected String key;
    private DeviceTypeResolver resolver;

    public Device() {

    }

    public boolean supports(Class<? extends Command> clazz) {
        return resolver.supports(clazz);
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public Long getDbId() {
        return dbId;
    }

    public String getId() {
        return id;
    }

    public int getIconDrawable() {
        return iconDrawable;
    }

    public DeviceTypeResolver getResolver() {
        return resolver;
    }

    public <T> T getRemoteControl() {
        return (T) getResolver().getRemoteControl(this);
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public String getKey() {
        return key;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIconDrawable(int iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Device{" +
                "dbId=" + dbId +
                ", id='" + id + '\'' +
                ", iconDrawable=" + iconDrawable +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", key='" + key + '\'' +
                ", resolver=" + resolver +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Device d;

        private Builder() {
            d = new Device();
            d.iconDrawable = R.drawable.default_bulb;
            d.name = "Device X";
        }

        public Builder id(String id) {
            d.id = id;
            return this;
        }

        public Builder dbId(Long id) {
            d.dbId = id;
            return this;
        }

        public Builder icon(int iconDrawable) {
            d.iconDrawable = iconDrawable;
            return this;
        }

        public Builder name(String name) {
            d.name = name;
            return this;
        }

        public Builder host(String host) {
            d.host = host;
            return this;
        }

        public Builder key(String key) {
            d.key = key;
            return this;
        }

        public Builder resolver(DeviceTypeResolver resolver) {
            d.resolver = resolver;
            return this;
        }

        public Device build() {
            return d;
        }
    }

}
