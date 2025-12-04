resource "google_storage_bucket" "backups" {
  name          = "${var.project_id}-backups"
  location      = var.region
  force_destroy = false

  uniform_bucket_level_access = true

  lifecycle_rule {
    condition {
      age = 180
    }
    action {
      type = "Delete"
    }
  }
}

resource "google_storage_bucket" "user_content" {
  name          = "${var.project_id}-user-content"
  location      = var.region
  force_destroy = false

  uniform_bucket_level_access = true
  
  cors {
    origin          = ["*"]
    method          = ["GET", "HEAD", "PUT", "POST", "DELETE"]
    response_header = ["*"]
    max_age_seconds = 3600
  }
}
