package com.cloudguardian.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cloudguardian.model.SensorEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IngestSensorHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson gson = new GsonBuilder().create();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);

        try {
            // Parse the incoming JSON body
            SensorEvent event = gson.fromJson(input.getBody(), SensorEvent.class);

            // Set default values if missing
            if (event.getDeviceId() == null || event.getDeviceId().isEmpty()) {
                event.setDeviceId("sensor-" + UUID.randomUUID().toString().substring(0, 8));
            }
            event.setTimestamp(Instant.now().toString());

            // Simple anomaly detection
            boolean isAlert = event.isMotion() || 
                              (event.getTemperature() != null && event.getTemperature() > 35.0);
            event.setStatus(isAlert ? "ALERT" : "normal");

            // For now we just return the processed event
            // Later we will save it to DynamoDB
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Event processed successfully");
            result.put("data", event);

            response.setStatusCode(200);
            response.setBody(gson.toJson(result));

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\": \"" + e.getMessage() + "\"}");
        }

        return response;
    }
}