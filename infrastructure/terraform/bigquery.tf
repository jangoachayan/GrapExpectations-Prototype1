resource "google_bigquery_dataset" "sensor_data" {
  dataset_id                  = "sensor_data"
  friendly_name               = "Sensor Data"
  description                 = "Time-series data from RAPT Pill and other sensors"
  location                    = var.region
  default_table_expiration_ms = null

  depends_on = [google_project_service.bigquery]
}

resource "google_bigquery_table" "rapt_pill_readings" {
  dataset_id = google_bigquery_dataset.sensor_data.dataset_id
  table_id   = "rapt_pill_readings"

  schema = <<EOF
[
  {
    "name": "device_id",
    "type": "STRING",
    "mode": "REQUIRED"
  },
  {
    "name": "timestamp",
    "type": "TIMESTAMP",
    "mode": "REQUIRED"
  },
  {
    "name": "gravity",
    "type": "FLOAT",
    "mode": "NULLABLE"
  },
  {
    "name": "temperature_c",
    "type": "FLOAT",
    "mode": "NULLABLE"
  },
  {
    "name": "battery_pct",
    "type": "FLOAT",
    "mode": "NULLABLE"
  },
  {
    "name": "rssi",
    "type": "INTEGER",
    "mode": "NULLABLE"
  },
  {
    "name": "raw_payload",
    "type": "JSON",
    "mode": "NULLABLE"
  }
]
EOF
}
