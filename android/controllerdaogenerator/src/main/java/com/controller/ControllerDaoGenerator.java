package com.controller;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class ControllerDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(2, "com.loicortola.controller.persistence");

        addDevice(schema);

        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    private static void addDevice(Schema schema) {
        Entity device = schema.addEntity("Device");
        device.addIdProperty();
        device.addStringProperty("deviceId").unique().notNull();
        device.addStringProperty("deviceResolver").notNull();
        device.addStringProperty("name").notNull();
        device.addLongProperty("icon").notNull();
        device.addStringProperty("host").notNull();
        device.addStringProperty("key");
    }
}
