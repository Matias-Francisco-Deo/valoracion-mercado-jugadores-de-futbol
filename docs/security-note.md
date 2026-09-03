# Security note for v1

This first release intentionally stores user credentials in plaintext and does not implement authentication or encryption. This is a deliberate tradeoff required by the feature specification and is safe only for local/internal development and testing.

Risks include:
- plain-text password exposure in the database and logs
- no protection against credential theft or replay
- no transport-layer security enforcement for unencrypted HTTP traffic

Production deployment should require:
- password hashing via BCrypt or Argon2
- authentication and authorization controls
- TLS and secret management
- structured secrets and environment-based configuration
