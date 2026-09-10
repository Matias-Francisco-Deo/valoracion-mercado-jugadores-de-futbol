# Quickstart: Player Service Validation

## Prerequisites

- PostgreSQL is running locally on `localhost:5432`.
- The `overcode` database already exists.
- The backend project is available at `backend/`.

## Validation scenarios

1. Start the application:
   - `cd backend`
   - `./mvnw spring-boot:run`

2. Verify repository-backed player creation:
   - Use the service method that creates a valid player record.
   - Expected outcome: a persisted `Player` is returned with an id and valid default values.

3. Verify lookup by id:
   - Fetch the previously created player by id.
   - Expected outcome: the exact stored player is returned.

4. Verify missing-record handling:
   - Request a player id that does not exist.
   - Expected outcome: a clear not-found exception or equivalent service-level failure is raised.

5. Verify listing all players:
   - Create two or more players and call the list method.
   - Expected outcome: all persisted players are returned without omissions.

6. Run targeted validation:
   - `cd backend`
   - `./mvnw test`
   - Expected outcome: relevant service and repository tests pass, validating create/get-by-id/get-all behavior.

## Expected result

The service supports the full player lifecycle required by this feature without exposing endpoints or controller-level logic.
