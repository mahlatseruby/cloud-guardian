package com.cloudguardian.service;

import com.cloudguardian.model.SensorEvent;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class DynamoDbService {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbService() {
        this.dynamoDbClient = DynamoDbClient.create();
        this.tableName = System.getenv().getOrDefault("TABLE_NAME", "CloudGuardianEvents");
    }

    public void saveEvent(SensorEvent event) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("deviceId", AttributeValue.builder().s(event.getDeviceId()).build());
        item.put("timestamp", AttributeValue.builder().s(event.getTimestamp()).build());
        item.put("status", AttributeValue.builder().s(event.getStatus()).build());
        item.put("motion", AttributeValue.builder().bool(event.isMotion()).build());

        if (event.getTemperature() != null) {
            item.put("temperature", AttributeValue.builder().n(event.getTemperature().toString()).build());
        }

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }
}