# GrapExpectations-Prototype1

This is the Prototype1 project for GrapExpectation.
Test Version for grapexpectations.

�
�
 GrapExpectation Studio — Complete 
Product & Technical Specification 
Version 
v1.0 — Consolidated Development Document 
Platforms: Android App + Web App 
Backend: Firebase + Google Cloud Platform 
Sensors: RAPT Pill (priority), future IoT/ESP32 devices 
1⃣ PROJECT OVERVIEW 
1.1 Product Summary 
GrapExpectation Studio is a cross-platform fermentation management system for makers 
of wine, mead, beer, cider, kombucha, and other fermented beverages. 
The system consists of: 
● A native Android app (brew-day logging, live monitoring, offline support) 
● A web application (recipe creation, analytics, batch comparison, admin) 
● A shared Firebase + Google Cloud backend 
● Automated IoT sensor ingestion (starting with RAPT Pill) 
● Automated Google Cloud backups 
Both the Android and Web apps operate on exactly the same data model and share all 
Firestore documents, schemas, and structure. 
2⃣ TARGET USERS & USE CASES 
Hobbyists / Home Brewers 
● Track simple batches, view fermentation curves, get alerts 
● Log measurements, tasks, tasting notes 
Craft Producers 
● Multiple batches at once 
● Multi-user organization (Owner, Brewer, Assistant) 
● Advanced analytics 
IoT Enthusiasts 
● Use RAPT Pill and other sensors 
● Send real-time gravity and temperature data 
Educators 
● Demonstrate fermentation curves 
● Use printable batch and recipe reports 
3⃣ CORE FEATURES 
3.1 Shared Features (Android + Web) 
● Unified login (Firebase Auth: Email, Google, Apple) 
● Recipe builder (ingredients, yeast, nutrients, target OG/FG/ABV) 
● Batch creation and lifecycle (planned → active → conditioning → bottled) 
● Real-time SG & Temp charts 
● Task scheduling (racking, stabilizing, cold crash, bottling) 
● Notes, photos, attachments 
● Alerts for fermentation abnormalities 
● Cloud-synced data across devices 
● Export options (PDF, CSV, Google Drive) 
● Multi-user organization support 
4⃣ PLATFORM-SPECIFIC FEATURES 
4.1 Android App (Kotlin + Jetpack Compose) 
● Offline-first logging (writes queue until connected) 
● Real-time UI updates with Firestore listeners 
● Push notifications via FCM 
● Device binding wizard (RAPT) 
● Brew-day experience: large buttons, quick actions 
● Chart rendering (MPAndroidChart) 
● Background services for monitoring 
4.2 Web App (React or Flutter Web) 
● Recipe builder with drag-and-drop 
● Batch comparison and overlays 
● Administrative dashboards (user roles, sensor status) 
● Printable batch logs and bottle labels 
● Big-screen analytics 
● Data table views (all readings, ingredients, tasks) 
5⃣ SYSTEM ARCHITECTURE 
5.1 High-Level Architecture 
Android App  →                     
Functions → Firestore/BigQuery 
Web App      
→  Cloud Run  → Pub/Sub → Cloud 
→ Firebase SDKs  →   → 
RAPT Pill    
→ RAPT Cloud Webhook → 
5.2 Backend Services 
● Firebase Authentication (user identity, roles via Firestore lookup) 
● Firestore (primary operational DB) 
● Cloud Storage (images, PDFs, labels) 
● Cloud Functions 
○ Alert generation 
○ Derived calculations 
○ Task reminders 
● BigQuery (long-term time-series for SG/Temp readings) 
● Cloud Run (RAPT Pill webhook ingest endpoint) 
● Cloud Scheduler (daily tasks, backups) 
● Google Cloud Storage Backup Bucket 
6⃣ DATA SCHEMA (FINAL) 
6.1 Collections 
/organizations/{orgId} 
● name 
● createdAt 
● planTier 
● ownerUid 
/organizations/{orgId}/members/{uid} 
● role: owner | brewer | assistant | viewer 
● invitedAt 
/recipes/{recipeId} 
● orgId 
● visibility (private | org | public) 
● name, category 
● target: { og, fg, abv, volumeL, tempRange, pHRange } 
● ingredients[] 
● steps[] 
● yeast profile 
● versions[] 
/batches/{batchId} 
● orgId 
● recipeId 
● startAt 
● endAt 
● status 
● sensors: { rapt_pill: deviceId } 
● labels[] 
● summary: { og, fg, abv, peakTemp, minTemp } 
/batches/{batchId}/readings_daily/{YYYYMMDD} 
● sgMin, sgMax 
● tempAvg 
● lastSampleAt 
● points: [ { t, sg, tempC } ] 
/devices/{deviceId} 
● type: "rapt_pill" 
● name 
● orgId 
● lastSeenAt 
● lastSg 
● lastTempC 
● batteryPct 
● rssi 
/alerts/{alertId} 
● batchId 
● type 
● threshold 
● triggeredAt 
● resolvedAt 
/tasks/{taskId} (sub-collection under batches) 
● title 
● dueAt 
● doneAt 
● assigneeUid 
● auto (true|false) 
● condition{} 
7⃣ RAPT PILL INTEGRATION 
7.1 Data Ingest Flow 
1. RAPT Cloud sends webhook → Cloud Run endpoint 
2. Cloud Run validates signature 
3. Pushes payload to Pub/Sub 
4. Function writes small last-known snapshot to Firestore 
5. Full reading stored in BigQuery 
7.2 Expected Payload (POST JSON) 
{ 
"device_id": "@device_id", 
"device_name": "@device_name", 
"temperature_c": "@temperature", 
"gravity_sg": "@gravity", 
"battery_pct": "@battery", 
"rssi": "@rssi", 
"created_at": "@created_date" 
} 
7.3 Device Binding 
● Android & Web apps allow user to bind a RAPT device to a batch: 
batches/{id}.sensors.rapt_pill = deviceId 
8⃣ AUTOMATED BACKUPS 
8.1 Firestore → Google Cloud Storage (Daily) 
● Cloud Scheduler (03:00 daily) triggers Cloud Function 
Cloud Function executes: 
gcloud firestore export gs://grapexpectation-backups/YYYY-MM-DD 
●  
8.2 Storage Backups 
● GCS Lifecycle: retain for 180 days 
8.3 BigQuery Snapshots 
● Automatic table snapshots 
● Optional CSV/Parquet export to GCS