package control;

/**
 * Enumerates the types of enemies available in the game.
 * Adding a new enemy type requires:
 *   1. Adding a constant here
 *   2. Adding the creation logic in {@link SpawnManager#createEnemy}
 */
public enum EnemyType {
    GOBLIN,
    WOLF
}
