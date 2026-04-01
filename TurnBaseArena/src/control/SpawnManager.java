package control;

import entity.*;

/**
 * Factory (Creator) responsible for instantiating {@link Enemy} objects.
 * <p>
 * Applies the <b>Factory Method</b> pattern: callers request an enemy by
 * {@link EnemyType} and a name, and receive a fully-constructed concrete
 * {@code Enemy} without depending on any concrete subclass directly.
 * <p>
 * <b>Extensibility:</b> to add a new enemy type, add an {@link EnemyType}
 * constant and a corresponding case in {@link #createEnemy}.
 */
public class SpawnManager {

    /**
     * Creates a single {@link Enemy} instance of the specified type.
     *
     * @param type the type of enemy to create
     * @param name the display name for the enemy (e.g. "Goblin A")
     * @return a new {@code Enemy} instance
     * @throws IllegalArgumentException if the type is not recognised
     */
    public Enemy createEnemy(EnemyType type, String name) {
        return switch (type) {
            case GOBLIN -> new Goblin(name);
            case WOLF   -> new Wolf(name);
        };
    }
}
