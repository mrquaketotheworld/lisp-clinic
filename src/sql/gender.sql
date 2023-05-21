CREATE TABLE IF NOT EXISTS gender (
  gender_type VARCHAR(16) PRIMARY KEY,
  created_at timestamptz DEFAULT NOW() NOT NULL
)
