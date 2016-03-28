package com.loicortola.controller.command;

import com.loicortola.controller.command.behavior.HealthAware;

/**
 * Created by loic on 28/03/2016.
 */
public class CheckHealthCommand implements Command {

    private HealthAware h;
    private HealthAware.OnHealthCheckListener l;

    public CheckHealthCommand(HealthAware h, HealthAware.OnHealthCheckListener l) {
        this.h = h;
        this.l = l;
    }

    @Override
    public void execute() {
        h.isHealthy(l);
    }
}
