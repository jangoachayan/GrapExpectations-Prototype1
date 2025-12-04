# GrapExpectation Studio — Complete Product & Technical Specification
**Version:** v1.0 — Consolidated Development Document
**Platforms:** Android App + Web App
**Backend:** Firebase + Google Cloud Platform
**Sensors:** RAPT Pill (priority), future IoT/ESP32 devices

## 1. PROJECT OVERVIEW
### 1.1 Product Summary
GrapExpectation Studio is a cross-platform fermentation management system for makers of wine, mead, beer, cider, kombucha, and other fermented beverages.

The system consists of:
*   A native Android app (brew-day logging, live monitoring, offline support)
*   A web application (recipe creation, analytics, batch comparison, admin)
*   A shared Firebase + Google Cloud backend
*   Automated IoT sensor ingestion (starting with RAPT Pill)
*   Automated Google Cloud backups

Both the Android and Web apps operate on exactly the same data model and share all Firestore documents, schemas, and structure.

## 2. TARGET USERS & USE CASES
*   **Hobbyists / Home Brewers:** Track simple batches, view fermentation curves, get alerts. Log measurements, tasks, tasting notes.
*   **Craft Producers:** Multiple batches at once. Multi-user organization (Owner, Brewer, Assistant). Advanced analytics.
*   **IoT Enthusiasts:** Use RAPT Pill and other sensors. Send real-time gravity and temperature data.
*   **Educators:** Demonstrate fermentation curves. Use printable batch and recipe reports.

## 3. CORE FEATURES
### 3.1 Shared Features (Android + Web)
*   Unified login (Firebase Auth: Email, Google, Apple)
*   Recipe builder (ingredients, yeast, nutrients, target OG/FG/ABV)
*   Batch creation and lifecycle (planned → active → conditioning → bottled)
*   Real-time SG & Temp charts
*   Task scheduling (racking, stabilizing, cold crash, bottling)
*   Notes, photos, attachments
*   Alerts for fermentation abnormalities
*   Cloud-synced data across devices
*   Export options (PDF, CSV, Google Drive)
*   Multi-user organization support

## 4. PLATFORM-SPECIFIC FEATURES
### 4.1 Android App (Kotlin + Jetpack Compose)
*   Offline-first logging (writes queue until connected)
*   Real-time UI updates with Firestore listeners
*   Push notifications via FCM
*   Device binding wizard (RAPT)
*   Brew-day experience: large buttons, quick actions
*   Chart rendering (MPAndroidChart)
*   Background services for monitoring

### 4.2 Web App (React or Flutter Web)
*   Recipe builder with drag-and-drop
*   Batch comparison and overlays
*   Administrative dashboards (user roles, sensor status)
*   Printable batch logs and bottle labels
*   Big-screen analytics
*   Data table views (all readings, ingredients, tasks)

## 5. SYSTEM ARCHITECTURE
### 5.1 High-Level Architecture
*   **Android App** → Firebase SDKs → Firestore/BigQuery
*   **Web App** → Firebase SDKs → Firestore/BigQuery
*   **RAPT Pill** → RAPT Cloud Webhook → Cloud Run → Pub/Sub → Cloud Functions → Firestore/BigQuery

### 5.2 Backend Services
*   **Firebase Authentication:** User identity, roles via Firestore lookup
*   **Firestore:** Primary operational DB
*   **Cloud Storage:** Images, PDFs, labels
*   **Cloud Functions:** Alert generation, derived calculations, task reminders
*   **BigQuery:** Long-term time-series for SG/Temp readings
*   **Cloud Run:** RAPT Pill webhook ingest endpoint
*   **Cloud Scheduler:** Daily tasks, backups
*   **Google Cloud Storage:** Backup Bucket

## 6. DATA SCHEMA (FINAL)
### 6.1 Collections
*   `/organizations/{orgId}`
    *   `name`, `createdAt`, `planTier`, `ownerUid`
*   `/organizations/{orgId}/members/{uid}`
    *   `role`: owner | brewer | assistant | viewer
    *   `invitedAt`
*   `/recipes/{recipeId}`
    *   `orgId`, `visibility` (private | org | public)
    *   `name`, `category`
    *   `target`: { og, fg, abv, volumeL, tempRange, pHRange }
    *   `ingredients[]`, `steps[]`, `yeast profile`, `versions[]`
*   `/batches/{batchId}`
    *   `orgId`, `recipeId`, `startAt`, `endAt`, `status`
    *   `sensors`: { rapt_pill: deviceId }
    *   `labels[]`
    *   `summary`: { og, fg, abv, peakTemp, minTemp }
*   `/batches/{batchId}/readings_daily/{YYYYMMDD}`
    *   `sgMin`, `sgMax`, `tempAvg`, `lastSampleAt`
    *   `points`: [ { t, sg, tempC } ]
*   `/devices/{deviceId}`
    *   `type`: "rapt_pill"
    *   `name`, `orgId`, `lastSeenAt`, `lastSg`, `lastTempC`, `batteryPct`, `rssi`
*   `/alerts/{alertId}`
    *   `batchId`, `type`, `threshold`, `triggeredAt`, `resolvedAt`
*   `/tasks/{taskId}` (sub-collection under batches)
    *   `title`, `dueAt`, `doneAt`, `assigneeUid`, `auto` (true|false), `condition{}`

## 7. RAPT PILL INTEGRATION
### 7.1 Data Ingest Flow
1.  RAPT Cloud sends webhook → Cloud Run endpoint
2.  Cloud Run validates signature
3.  Pushes payload to Pub/Sub
4.  Function writes small last-known snapshot to Firestore
5.  Full reading stored in BigQuery

### 7.2 Expected Payload (POST JSON)
```json
{
  "device_id": "@device_id",
  "device_name": "@device_name",
  "temperature_c": "@temperature",
  "gravity_sg": "@gravity",
  "battery_pct": "@battery",
  "rssi": "@rssi",
  "created_at": "@created_date"
}
```

### 7.3 Device Binding
*   Android & Web apps allow user to bind a RAPT device to a batch: `batches/{id}.sensors.rapt_pill = deviceId`

## 8. AUTOMATED BACKUPS
### 8.1 Firestore → Google Cloud Storage (Daily)
*   Cloud Scheduler (03:00 daily) triggers Cloud Function
*   Cloud Function executes: `gcloud firestore export gs://grapexpectation-backups/YYYY-MM-DD`

### 8.2 Storage Backups
*   GCS Lifecycle: retain for 180 days

### 8.3 BigQuery Snapshots
*   Automatic table snapshots
*   Optional CSV/Parquet export to GCS

## 9. SECURITY, ROLES & ACCESS CONTROL
### 9.1 Authentication
*   Firebase Auth (Email, Google, Apple)
*   App Check enabled (Play Integrity)
*   Session persistence across Android + Web

### 9.2 Firestore Rules (Role-Based)
*   User must exist under `/organizations/{orgId}/members/{uid}`
*   Only owners & brewers can mutate recipes
*   Assistants can log readings
*   Viewers read-only

### 9.3 Storage Rules
*   Limit uploads to images/PDF
*   10MB max per file

## 10. ALERT LOGIC
### 10.1 Types
*   SG stall
*   Temperature out of range
*   Target FG reached
*   Sensor offline (no data > N minutes)
*   Battery low

### 10.2 Thresholds (Configurable)
*   SG change < 0.001 over 12 hours
*   Temp outside yeast specs
*   No reading for 1 hour

## 11. UI/UX EXPECTATIONS
### 11.1 Android App
*   Jetpack Compose
*   Clean Material 3
*   Brew-day mode (large buttons, minimal controls)
*   Charts in dark & light modes
*   Smooth real-time updates

### 11.2 Web App
*   Responsive layout (desktop first)
*   Drag-and-drop recipe builder
*   Multi-batch comparison dashboard
*   Print-friendly pages

## 12. INFRASTRUCTURE
### 12.1 Deployment
*   Android via Google Play
*   Web app hosted on Firebase Hosting
*   Cloud Run ingest endpoint
*   Daily scheduled tasks

### 12.2 Environments
*   dev
*   staging
*   production

## 13. ROADMAP
### Phase 1 — MVP
*   Android app core logging + charts
*   Web app basic recipe + batch view
*   RAPT Pill webhook ingest
*   Firestore + Storage + Auth
*   Daily backups

### Phase 2 — Extended
*   Alerts
*   Batch analytics
*   Organization roles
*   PDF exports
*   Label creator

### Phase 3 — Advanced
*   Multiple sensor support (iSpindel, Tilt, ESP32)
*   Temperature control automation
*   AI-based fermentation prediction
*   Marketplace

## 14. APP STORE & WEBSITE COPY
**Short Description:** Smart fermentation studio for wine, beer, mead & kombucha with live sensor tracking.
**Tagline:** Craft. Track. Perfect.

## 15. DEVELOPER CHECKLIST
### Android Team
*   [ ] Implement screens: Login, Home, Batch Detail, Chart, Device Binding
*   [ ] Firestore listeners for: batches, readings_daily, devices
*   [ ] Write Flow/Coroutine-based repositories
*   [ ] Add FCM push notifications

### Web Team
*   [ ] Build recipe builder
*   [ ] Charting via Chart.js or Recharts
*   [ ] Admin panel for org/role management

### Backend Team
*   [ ] Cloud Run ingest endpoint
*   [ ] Pub/Sub + Function
*   [ ] Firestore security rules
*   [ ] Backup scheduler
*   [ ] Alerts engine
*   [ ] BigQuery schema
