package com.cloudguardian.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cloudguardian.model.SensorEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetEventsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson gson = new GsonBuilder().create();
    private final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
    private final String tableName = System.getenv().getOrDefault("TABLE_NAME", "CloudGuardianEvents");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);

        try {
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .limit(20)  // Get latest 20 events
                    .build();

            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

            List<SensorEvent> events = new ArrayList<>();

            for (Map<String, AttributeValue> item : scanResponse.items()) {
                SensorEvent event = new SensorEvent();
                event.setDeviceId(item.get("deviceId").s());
                event.setTimestamp(item.get("timestamp").s());
                event.setStatus(item.get("status").s());
                event.setMotion(item.get("motion").bool());

                if (item.containsKey("temperature") && item.get("temperature").n() != null) {
                    event.setTemperature(Double.parseDouble(item.get("temperature").n()));
                }

                events.add(event);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Events retrieved successfully");
            result.put("count", events.size());
            result.put("events", events);

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