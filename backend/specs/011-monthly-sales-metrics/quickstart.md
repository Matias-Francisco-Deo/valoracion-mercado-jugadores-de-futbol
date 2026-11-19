# Quickstart & Validation Guide: Monthly Sales Metrics

## Prerequisites

- JDK 21+ installed
- Docker running (required for Testcontainers PostgreSQL during integration tests)
- Local backend build tools (`mvnw`)

---

## 1. Running Automated Tests

### Run Repository & DAO Integration Tests
Verifies that the `@Query` on `TransactionDAOJPA` and `TransactionRepository.contarVentasEntre(...)` executes properly against real PostgreSQL data:
```bash
./mvnw test -Dtest=TransactionRepositoryTest
```

### Run Service Unit Tests
Verifies month defaulting and boundary calculation:
```bash
./mvnw test -Dtest=SalesMetricsServiceImplTest
```

### Run End-to-End API Tests
Verifies HTTP contract, API Key protection, defaulting, and return codes:
```bash
./mvnw test -Dtest=SalesMetricsE2eTest
```

### Run Architecture Integrity Tests
Verifies adherence to project Constitution and package/naming rules:
```bash
./mvnw test -Dtest=ArchitectureTest
```

---

## 2. Manual API Verification (Local Server)

### Start the Local Application
```bash
./mvnw spring-boot:run
```

### A. Successful Request with Explicit Month/Year
```bash
curl -X GET "http://localhost:8080/api/admin/metrics/sales?year=2026&month=11" \
  -H "X-API-KEY: your-configured-superuser-api-key"
```
**Expected Response (200 OK)**:
```json
{
  "year": 2026,
  "month": 11,
  "totalSales": 15
}
```

### B. Successful Request with Defaulted Month/Year
```bash
curl -X GET "http://localhost:8080/api/admin/metrics/sales" \
  -H "X-API-KEY: your-configured-superuser-api-key"
```
**Expected Response (200 OK)**:
```json
{
  "year": 2026,
  "month": 11,
  "totalSales": 15
}
```

### C. Unauthorized Request (Missing or Wrong API Key)
```bash
curl -X GET "http://localhost:8080/api/admin/metrics/sales"
```
**Expected Response (401 Unauthorized)**

### D. Invalid Query Parameters (Out-of-bounds Month)
```bash
curl -X GET "http://localhost:8080/api/admin/metrics/sales?year=2026&month=13" \
  -H "X-API-KEY: your-configured-superuser-api-key"
```
**Expected Response (400 Bad Request)**
