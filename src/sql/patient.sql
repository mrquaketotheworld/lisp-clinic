CREATE TABLE IF NOT EXISTS patient (
  mid VARCHAR(64) PRIMARY KEY,
  first_name VARCHAR(128) NOT NULL,
  last_name VARCHAR(128) NOT NULL,
  gender_type VARCHAR(16) NOT NULL REFERENCES gender(gender_type),
  birth DATE NOT NULL,
  created_at timestamptz DEFAULT NOW() NOT NULL,
  updated_at timestamptz DEFAULT NOW() NOT NULL
)

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER set_timestamp
BEFORE UPDATE ON patient
FOR EACH ROW
  EXECUTE PROCEDURE trigger_set_timestamp()
