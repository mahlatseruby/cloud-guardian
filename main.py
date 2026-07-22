from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from supabase import create_client, Client
import os
from dotenv import load_dotenv
import resend
from datetime import datetime

load_dotenv()

app = FastAPI(title="Cloud Guardian")


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


supabase: Client = create_client(
    os.getenv("SUPABASE_URL"),
    os.getenv("SUPABASE_KEY")
)

resend.api_key = os.getenv("RESEND_API_KEY")

@app.post("/sensor")
async def ingest_sensor(data: dict):
    try:
        device_id = data.get("device_id", "unknown")
        temperature = data.get("temperature")
        motion = data.get("motion", False)
        
        status = "ALERT" if (motion or (temperature and temperature > 35)) else "normal"
        
        
        event = {
            "device_id": device_id,
            "temperature": temperature,
            "motion": motion,
            "status": status
        }
        
        supabase.table("sensor_events").insert(event).execute()
        
        # Send Email Alert
        if status == "ALERT":
            resend.emails.send({
                "from": "Cloud Guardian <alert@resend.dev>",
                "to": os.getenv("mahlatseruby@gmail.com"),  
                "subject": f"🚨 Security Alert - {device_id}",
                "html": f"""
                    <h2>Security Alert!</h2>
                    <p><strong>Device:</strong> {device_id}</p>
                    <p><strong>Temperature:</strong> {temperature}°C</p>
                    <p><strong>Motion Detected:</strong> {motion}</p>
                    <p>Time: {datetime.now()}</p>
                """
            })
        
        return {"status": "success", "event": event}
        
    except Exception as e:
        return {"status": "error", "message": str(e)}