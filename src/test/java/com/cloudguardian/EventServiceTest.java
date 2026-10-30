package com.cloudguardian;

import com.cloudguardian.model.SensorEvent;
import com.cloudguardian.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService();
    }

    @Test
    @DisplayName("Should mark event as ALERT when motion is detected")
    void testProcessEvent_WithMotion_ShouldBeAlert() {
        SensorEvent event = new SensorEvent();
        event.setDeviceId("sensor-01");
        event.setMotion(true);
        event.setTemperature(22.0);

        SensorEvent result = eventService.processEvent(event);

        assertEquals("ALERT", result.getStatus());
        assertTrue(eventService.isAlert(result));
    }

    @Test
    @DisplayName("Should mark event as ALERT when temperature is above 35")
    void testProcessEvent_HighTemperature_ShouldBeAlert() {
        SensorEvent event = new SensorEvent();
        event.setDeviceId("sensor-02");
        event.setMotion(false);
        event.setTemperature(38.5);

        SensorEvent result = eventService.processEvent(event);

        assertEquals("ALERT", result.getStatus());
        assertTrue(eventService.isAlert(result));
    }

    @Test
    @DisplayName("Should mark event as normal when conditions are safe")
    void testProcessEvent_NormalConditions_ShouldBeNormal() {
        SensorEvent event = new SensorEvent();
        event.setDeviceId("sensor-03");
        event.setMotion(false);
        event.setTemperature(24.0);

        SensorEvent result = eventService.processEvent(event);

        assertEquals("normal", result.getStatus());
        assertFalse(eventService.isAlert(result));
    }

    @Test
    @DisplayName("Should set default deviceId when it is missing")
    void testProcessEvent_MissingDeviceId_ShouldSetDefault() {
        SensorEvent event = new SensorEvent();
        event.setMotion(false);
        event.setTemperature(20.0);

        SensorEvent result = eventService.processEvent(event);

        assertNotNull(result.getDeviceId());
        assertFalse(result.getDeviceId().isBlank());
    }
}