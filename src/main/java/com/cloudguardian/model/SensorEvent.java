package com.cloudguardian.model;

public class SensorEvent {
    private String deviceId;
    private String timestamp;
    private Double temperature;
    private boolean motion;
    private String status;

    public SensorEvent() {}

    public SensorEvent(String deviceId, String timestamp, Double temperature, boolean motion, String status) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.motion = motion;
        this.status = status;
    }

    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public boolean isMotion() { return motion; }
    public void setMotion(boolean motion) { this.motion = motion; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}