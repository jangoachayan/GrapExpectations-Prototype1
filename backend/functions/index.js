const functions = require("firebase-functions");
const admin = require("firebase-admin");
const {BigQuery} = require("@google-cloud/bigquery");

admin.initializeApp();
const db = admin.firestore();
const bigquery = new BigQuery();

exports.processSensorData = functions.pubsub.topic("rapt-pill-ingest").onPublish(async (message) => {
  try {
    const dataJson = message.json;
    console.log("Processing message:", dataJson);

    const deviceId = dataJson.device_id;
    const timestamp = new Date(dataJson.ingested_at || Date.now());
    const dateStr = timestamp.toISOString().split("T")[0].replace(/-/g, ""); // YYYYMMDD

    // 1. Update Device Status
    await db.collection("devices").doc(deviceId).set({
      lastSeenAt: timestamp,
      lastSg: dataJson.gravity_sg,
      lastTempC: dataJson.temperature_c,
      batteryPct: dataJson.battery_pct,
      rssi: dataJson.rssi,
      name: dataJson.device_name || deviceId,
      type: "rapt_pill"
    }, {merge: true});

    // 2. Find Active Batch for this Device
    // In a real app, we'd query for a batch where 'sensors.rapt_pill' == deviceId AND status == 'active'
    // For MVP, let's assume we write to a 'readings_daily' collection under the device or a known batch if linked.
    // To keep it simple for now, we'll just log it. 
    // TODO: Implement batch lookup.

    // 3. Insert into BigQuery
    const datasetId = "sensor_data";
    const tableId = "rapt_pill_readings";
    const row = {
      device_id: deviceId,
      timestamp: bigquery.datetime(timestamp.toISOString()),
      gravity: dataJson.gravity_sg,
      temperature_c: dataJson.temperature_c,
      battery_pct: dataJson.battery_pct,
      rssi: dataJson.rssi,
      raw_payload: JSON.stringify(dataJson)
    };

    await bigquery.dataset(datasetId).table(tableId).insert([row]);
    console.log(`Inserted row into BigQuery: ${deviceId}`);

  } catch (error) {
    console.error("Error processing sensor data:", error);
    throw error; // Retry on failure
  }
});
