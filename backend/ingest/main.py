import os
import json
from datetime import datetime
from fastapi import FastAPI, Request, HTTPException
from google.cloud import pubsub_v1

app = FastAPI()

# Configuration
PROJECT_ID = os.getenv("PROJECT_ID", "grapexpectations-p1-dev")
TOPIC_ID = os.getenv("TOPIC_ID", "rapt-pill-ingest")

publisher = pubsub_v1.PublisherClient()
topic_path = publisher.topic_path(PROJECT_ID, TOPIC_ID)

@app.post("/webhook/rapt")
async def ingest_rapt_pill(request: Request):
    try:
        payload = await request.json()
        print(f"Received payload: {payload}")

        # Basic validation
        if "device_id" not in payload:
            raise HTTPException(status_code=400, detail="Missing device_id")

        # Add server timestamp
        payload["ingested_at"] = datetime.utcnow().isoformat()

        # Publish to Pub/Sub
        data_str = json.dumps(payload)
        data = data_str.encode("utf-8")
        
        future = publisher.publish(topic_path, data)
        message_id = future.result()
        
        return {"status": "success", "message_id": message_id}

    except Exception as e:
        print(f"Error processing webhook: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/")
def health_check():
    return {"status": "ok", "service": "rapt-ingest"}
