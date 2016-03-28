package com.loicortola.controller.command.behavior;

/**
 * Created by loic on 28/03/2016.
 */
public interface HealthAware {

    interface OnHealthCheckListener {
        void onHealthCheck(boolean isHealthy);
    }

    void isHealthy(OnHealthCheckListener l);
}
