// Live API URL from my deployment
const API_BASE_URL = "https://91binh3b98.execute-api.eu-west-1.amazonaws.com/Prod";

function setStatus(message, isError = false) {
    const status = document.getElementById("status");
    status.textContent = message;
    status.style.color = isError ? "#ef4444" : "#e2e8f0";
}

async function loadEvents() {
    const eventsList = document.getElementById("eventsList");
    setStatus("Loading events...");

    try {
        const response = await fetch(`${API_BASE_URL}/events`);

        if (!response.ok) {
            throw new Error("API returned status " + response.status);
        }

        const data = await response.json();

        if (!data.events || data.events.length === 0) {
            eventsList.innerHTML = "<p>No events found.</p>";
            setStatus("No events available");
            return;
        }

        eventsList.innerHTML = "";

        // Sort by newest first
        const sortedEvents = data.events.sort((a, b) =>
            new Date(b.timestamp) - new Date(a.timestamp)
        );

        sortedEvents.forEach(event => {
            const card = document.createElement("div");
            card.className = `event-card ${event.status === "ALERT" ? "alert" : ""}`;

            card.innerHTML = `
                <h3>${event.deviceId}
                    <span style="float:right; font-size:0.85rem; color:${event.status === "ALERT" ? "#ef4444" : "#22c55e"}">
                        ${event.status}
                    </span>
                </h3>
                <p><strong>Temperature:</strong> ${event.temperature ?? "N/A"}°C</p>
                <p><strong>Motion:</strong> ${event.motion ? "Detected 🚨" : "None"}</p>
                <p><strong>Time:</strong> ${new Date(event.timestamp).toLocaleString()}</p>
            `;

            eventsList.appendChild(card);
        });

        setStatus(`Loaded ${data.events.length} events`);
    } catch (error) {
        setStatus("Error loading events", true);
        eventsList.innerHTML = `<p style="color: #ef4444;">Failed to load events. Check the console for details.</p>`;
        console.error(error);
    }
}

async function simulateSensor() {
    setStatus("Sending simulated sensor data...");

    const payload = {
        deviceId: "sensor-" + Math.floor(Math.random() * 100),
        temperature: parseFloat((20 + Math.random() * 25).toFixed(1)),
        motion: Math.random() > 0.65
    };

    try {
        const response = await fetch(`${API_BASE_URL}/sensor`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            setStatus("Sensor data sent successfully!");
            setTimeout(loadEvents, 1000);
        } else {
            const errorText = await response.text();
            setStatus("Failed to send sensor data: " + response.status, true);
            console.error(errorText);
        }
    } catch (error) {
        setStatus("Error sending data", true);
        console.error(error);
    }
}

// Load events when the page opens
loadEvents();