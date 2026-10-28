# Data Model: Login Streak Bonus (Updated)

## 1. Domain Enums & Entities

### 1.1 DiaDeLaSemana (`com.overcode.model.bonus.DiaDeLaSemana`)
Enumeration representing the 7 days of the streak cycle.

| Constant | Streak Index (`numeroRacha`) |
|---|---|
| `LUNES` | 1 |
| `MARTES` | 2 |
| `MIERCOLES` | 3 |
| `JUEVES` | 4 |
| `VIERNES` | 5 |
| `SABADO` | 6 |
| `DOMINGO` | 7 |

#### Domain Methods:
- `public static DiaDeLaSemana desdeRacha(int racha)`: Returns corresponding `DiaDeLaSemana`. If `racha < 1 || racha > 7`, throws `DiaInvalidoException`.
- `public static DiaDeLaSemana desdeDayOfWeek(DayOfWeek dayOfWeek)`: Returns corresponding `DiaDeLaSemana`. If null, throws `DiaDeLaSemanaInvalidoException`.

---

### 1.2 LoginBonus (`com.overcode.model.bonus.LoginBonus`)
Encapsulates daily bonus credit calculations, daily values, and streak scaling logic.

| Field | Type | Description | Invariants / Constraints |
|---|---|---|---|
| `id` | `Long` | Unique identifier | Immutable once set |
| `nombre` | `String` | Identifier name (e.g. "Bonus Base Semanal") | Not blank, trimmed |
| `bonusRewards` | `Map<DiaDeLaSemana, Double>` | Bonus credit reward mapping for each day | Must contain all 7 `DiaDeLaSemana` keys; each value must be $> 0.0$ |

#### Domain Methods:
- `public Double getBonusParaDia(DiaDeLaSemana dia)`:
  - If `dia == null`: throws `DiaDeLaSemanaInvalidoException`
  - Returns `bonusRewards.get(dia)`.
- `public Double getBonusParaRacha(int racha)`:
  - Calls `getBonusParaDia(DiaDeLaSemana.desdeRacha(racha))`.
- `public int calcularSiguienteRacha(LocalDate ultimoReclamo, LocalDate fechaActual, int rachaActual)`:
  - If `fechaActual == null`: throws `FechaReclamoBonusLoginInvalidaException`
  - If `ultimoReclamo == null`: returns `1`
  - If `fechaActual.isEqual(ultimoReclamo)`: throws `BonusYaReclamadoHoyException`
  - If `fechaActual.isBefore(ultimoReclamo)`: throws `FechaReclamoBonusLoginInvalidaException`
  - If `ChronoUnit.DAYS.between(ultimoReclamo, fechaActual) == 1`:
    - If `rachaActual >= 7`: returns `1` (cycle restart on Day 8)
    - Else: returns `rachaActual + 1`
  - If `ChronoUnit.DAYS.between(ultimoReclamo, fechaActual) > 1`: returns `1` (broken streak reset)
- `public boolean puedeReclamarHoy(LocalDate ultimoReclamo, LocalDate fechaActual)`:
  - Returns `ultimoReclamo == null || fechaActual.isAfter(ultimoReclamo)`
- `public Map<DiaDeLaSemana, Double> getBonusesDeLaSemana()`: Returns unmodifiable view of `bonusRewards`.

---

### 1.3 ConfiguracionCotizaciones (`com.overcode.model.cotizacion.ConfiguracionCotizaciones`)
Modified to reference the currently selected `LoginBonus`.

| Field | Type | Description | Invariants / Constraints |
|---|---|---|---|
| `id` | `Long` | Singleton configuration ID | Non-null |
| `estrategiaCotizacion` | `EstrategiaCotizacion` | Active price quotation strategy | Non-null |
| `loginBonus` | `LoginBonus` | Currently selected active login bonus calculation | Non-null once initialized |

#### Domain Methods:
- `public void cambiarLoginBonusPor(LoginBonus nuevoBonus)`: Sets new active `loginBonus`. Throws `LoginBonusInvalidoException` if null.

---

### 1.4 User (`com.overcode.model.User`)
Modified with streak tracking and explicit claim domain method.

| Field | Type | Description | Invariants / Constraints |
|---|---|---|---|
| `creditBalance` | `Double` | User credit balance | Value $\ge 0.0$ |
| `streakCount` | `Integer` | Current streak day count (0 to 7) | $0 \le \text{streakCount} \le 7$ |
| `lastBonusClaimDate` | `LocalDate` | Date of most recent claimed daily bonus (UTC) | Nullable (null if never claimed) |

#### Domain Methods:
- `public boolean puedeReclamarBonusDiarioHoy(LocalDate fechaActual, LoginBonus loginBonus)`:
  - Returns `loginBonus.puedeReclamarHoy(this.lastBonusClaimDate, fechaActual)`
- `public Double reclamarBonusDiario(LocalDate fechaActual, LoginBonus loginBonus)`:
  - If `loginBonus == null`: throws `LoginBonusInvalidoException`
  - Calculates new streak: `int nuevaRacha = loginBonus.calcularSiguienteRacha(this.lastBonusClaimDate, fechaActual, this.streakCount)`
  - Retrieves bonus: `Double bonus = loginBonus.getBonusParaRacha(nuevaRacha)`
  - Adds credits: `creditCredits(bonus)`
  - Updates state: `this.streakCount = nuevaRacha`, `this.lastBonusClaimDate = fechaActual`
  - Returns `bonus`.

---

## 2. Custom Exception Hierarchy (No Java Default Exceptions)

### 2.1 Model Exceptions (`com.overcode.model.bonus.exception`)
- `BonusYaReclamadoHoyException`: Thrown when a user attempts to claim the bonus for the second time on the same UTC calendar day.
- `DiaInvalidoException`: Thrown when a streak value is outside 1..7 or invalid.
- `DiaDeLaSemanaInvalidoException`: Thrown when a `DiaDeLaSemana` argument is null or cannot be resolved.
- `ValorBonusInvalidoException`: Thrown when a reward amount in the bonus map is null or $\le 0.0$.
- `FechaReclamoBonusLoginInvalidaException`: Thrown when a claim date is null or chronologically inconsistent.
- `LoginBonusInvalidoException`: Thrown when a `LoginBonus` definition is incomplete, missing days, or null.

### 2.2 Service Exceptions (`com.overcode.service.exception`)
- `LoginBonusNoConfiguradoException`: Thrown when `ConfiguracionCotizaciones` has no active `LoginBonus` set.
- `ReclamoBonusNoPermitidoException`: Thrown when service validation denies a claim operation.

---

## 3. Persistence Entities (JPA DTOs)

### 3.1 LoginBonusJPADTO (`com.overcode.persistence.dto.jpa.bonus.LoginBonusJPADTO`)
- Table: `login_bonuses`
- Fields:
  - `id`: `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id`
  - `nombre`: `@Column(nullable = false, unique = true) String nombre`
  - `rewards`: `@ElementCollection(fetch = FetchType.EAGER) @CollectionTable(name = "login_bonus_rewards", joinColumns = @JoinColumn(name = "login_bonus_id")) @MapKeyEnumerated(EnumType.STRING) @MapKeyColumn(name = "dia_semana") @Column(name = "creditos", nullable = false) Map<DiaDeLaSemana, Double> rewards`
- Mappers:
  - `public LoginBonus aModelo()`: Converts to domain model `LoginBonus(id, nombre, rewards)`.
  - `public static LoginBonusJPADTO desdeModelo(LoginBonus modelo)`: Converts domain model to JPA entity.

### 3.2 ConfiguracionCotizacionesJPADTO (`com.overcode.persistence.dto.jpa.cotizacion.ConfiguracionCotizacionesJPADTO`)
- Adds relation:
  ```java
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "login_bonus_id")
  private LoginBonusJPADTO loginBonus;
  ```
- Mappers updated to convert `loginBonus` to/from domain model.

### 3.3 UserJPADTO (`com.overcode.persistence.dto.jpa.UserJPADTO`)
- Updates:
  - `creditBalance`: `@Column(name = "credit_balance", nullable = false) Double creditBalance = 0.0`
  - `streakCount`: `@Column(name = "streak_count", nullable = false) Integer streakCount = 0`
  - `lastBonusClaimDate`: `@Column(name = "last_bonus_claim_date") LocalDate lastBonusClaimDate`
- Mappers updated accordingly.

---

## 4. State Transitions

```
               +-------------------------------------------------+
               | User Created (streak=0, lastClaim=null, credits)|
               +-----------------------+-------------------------+
                                       |
                                       | POST /api/daily-bonus/claim (LUNES / Dia 1)
                                       v
               +-------------------------------------------------+
               |  Streak = 1, Credits +1.0 (LUNES Bonus)         |
               +-----------------------+-------------------------+
                                       |
                +----------------------+----------------------+
                |                                             |
                | Consecutive Claim (Day T+1)                 | Missed Day (>= Day T+2)
                v                                             v
+-------------------------------+           +------------------------------------+
| Streak = N + 1 (up to 7)      |           | Streak = 1, Credits +1.0 (Reset)   |
| Credits + Scheduled(Dia N+1)  |           | Credits + Scheduled(LUNES)         |
+---------------+---------------+           +------------------------------------+
                |
                | Consecutive Claim on Day 8
                v
+-------------------------------+
| Streak = 1 (Weekly Loop)      |
| Credits + Scheduled(LUNES)    |
+-------------------------------+
```
