# Quickstart: validation for the targeted feature

## Prerequisites

- Java 21
- Docker available for Testcontainers database tests
- Maven wrapper available in the backend project root

## Run the focused validation suite

From the backend folder:

```bash
./mvnw test -Dtest='*ExternalPlayerControllerE2eTest,*PlayerGameDataRepositoryTest,*ApiKeyAuthFilterTest,*JsonExtractorUtilTest'
```

## Expected outcomes

- Controller E2E tests pass with a mocked async scraper call and an accepted `202` response.
- Repository tests pass for save and retrieve behavior using a real JPA DAO and Testcontainers.
- API-key tests pass for missing, empty, invalid, and valid key scenarios.
- JSON extraction tests pass for sample HTML payloads and malformed or empty HTML cases.

## If a test fails

- Check the exact layer: controller, repository, security filter, or parser utility.
- Keep the failing test focused on one boundary; do not broaden the scope to unrelated modules.
- Preserve the requirement that the expensive scraper job is mocked in the controller integration path.
