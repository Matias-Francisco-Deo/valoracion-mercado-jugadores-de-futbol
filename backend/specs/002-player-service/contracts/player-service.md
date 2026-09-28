# Internal Service Contract: PlayerService

This feature is service-only; there is no public HTTP contract in this phase.

## Interface

```java
public interface PlayerService {
    Player create(Player player);
    Player getById(Long id);
    List<Player> getAll();
}
```

## Responsibilities

- Validate new player data before persisting.
- Persist the player using the existing repository abstraction.
- Retrieve a single player by id and raise a not-found outcome when absent.
- Return the complete collection of players in a consistent order.

## Error behavior

- Missing player id: service raises a not-found exception.
- Invalid player payload: service rejects invalid input before persistence.
- Empty catalog: list operation returns an empty collection.
