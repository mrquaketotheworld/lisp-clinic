CREATE TABLE IF NOT EXISTS patient_address (
  patient_mid VARCHAR(64) NOT NULL REFERENCES patient(mid),
  address_id INT NOT NULL REFERENCES address(id)
)
