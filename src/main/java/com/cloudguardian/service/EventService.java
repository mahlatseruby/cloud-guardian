package com.cloudguardian.service;

import com.cloudguardian.model.SensorEvent;

public class EventService {

    public SensorEvent processEvent(SensorEvent event) {
        if (event.getDeviceId() == null || event.getDeviceId().isBlank()) {
            event.setDeviceId("sensor-unknown");
        }

        boolean isAlert = event.isMotion() ||
                (event.getTemperature() != null && event.getTemperature() > 35.0);

        event.setStatus(isAlert ? "ALERT" : "normal");
        return event;
    }

    public boolean isAlert(SensorEvent event) {
        return "ALERT".equalsIgnoreCase(event.getStatus());
    }
}