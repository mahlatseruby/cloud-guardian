package com.cloudguardian.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cloudguardian.model.SensorEvent;
import com.cloudguardian.service.DynamoDbService;
import com.cloudguardian.service.EventService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IngestSensorHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson gson = new GsonBuilder().create();
    private final EventService eventService = new EventService();
    private final DynamoDbService dynamoDbService = new DynamoDbService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);

        try {
            // Parse incoming JSON
            SensorEvent event = gson.fromJson(input.getBody(), SensorEvent.class);

            // Set defaults
            if (event.getDeviceId() == null || event.getDeviceId().isBlank()) {
                event.setDeviceId("sensor-" + UUID.randomUUID().toString().substring(0, 8));
            }
            event.setTimestamp(Instant.now().toString());

            // Process the event (anomaly detection)
            event = eventService.processEvent(event);

            // Save to DynamoDB
            dynamoDbService.saveEvent(event);

            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Event processed and saved successfully");
            result.put("data", event);

            response.setStatusCode(200);
            response.setBody(gson.toJson(result));

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            response.setStatusCode(500);

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            response.setBody(gson.toJson(error));
        }

        return response;
    }
}