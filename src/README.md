# Cloud Guardian

A serverless IoT Security Monitoring system built with **Java** on AWS.

## Project Overview

Cloud Guardian receives sensor data (temperature + motion), detects anomalies in real time, stores events in DynamoDB, and displays them on a simple web dashboard.

When motion is detected or the temperature goes above 35°C, the system marks the event as an **ALERT**.

## Architecture

Sensor / Simulator
│
▼
API Gateway
│
├──► Lambda (IngestSensorHandler) ──► DynamoDB
│                                      │
│                                      └──► (Optional SNS Alerts)
│
└──► Lambda (GetEventsHandler) ◄── DynamoDB
│
▼
Frontend Dashboard


## Features

- Real-time sensor data ingestion
- Anomaly detection (motion or high temperature)
- Event storage in DynamoDB
- Live web dashboard
- Fully serverless architecture
- Infrastructure as Code with AWS SAM

## Tech Stack

- **Language:** Java 17
- **Compute:** AWS Lambda
- **API:** Amazon API Gateway
- **Database:** Amazon DynamoDB
- **IaC:** AWS SAM
- **Build Tool:** Maven
- **Frontend:** HTML, CSS, JavaScript

## Project Structure

Cloud-Guardian/
├── src/main/java/com/cloudguardian/
│   ├── model/
│   │   └── SensorEvent.java
│   ├── handler/
│   │   ├── IngestSensorHandler.java
│   │   └── GetEventsHandler.java
│   └── service/
│       ├── EventService.java
│       ├── DynamoDbService.java
│       └── NotificationService.java
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── template.yml
├── pom.xml
└── README.mdtext


## Live API

**Base URL:**
https://91binh3b98.execute-api.eu-west-1.amazonaws.com/Prod


| Method | Endpoint   | Description                |
|--------|------------|----------------------------|
| POST   | `/sensor`  | Ingest sensor data         |
| GET    | `/events`  | Retrieve recent events     |

## How to Run Locally

### 1. Build the project
```bash
mvn clean package

sam deploy --guided

###  Open the dashboard
Open frontend/index.html in your browser.

### How to Test
```Send sensor data:

curl.exe -X POST "https://91binh3b98.execute-api.eu-west-1.amazonaws.com/Prod/sensor" -H "Content-Type: application/json" -d "{\"deviceId\":\"sensor-01\",\"temperature\":38.5,\"motion\":true}"

### Get events:
curl.exe "https://91binh3b98.execute-api.eu-west-1.amazonaws.com/Prod/events"

