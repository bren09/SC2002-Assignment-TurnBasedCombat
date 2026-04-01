package control;

import entity.Enemy;
import java.util.ArrayList;
import java.util.List;

/**
 * Architect that defines what enemies appear in each difficulty level
 * and delegates how they are created to the {@link SpawnManager}.
 *
 * Responsibilities:
 *   1. Define the initial wave and backup wave composition per {@link Difficulty}.
 *   2. Coordinate with {@link SpawnManager} to instantiate the required enemies.
 *   3. Track whether the backup wave has already been deployed.
 * Design pattern: separates the specification of enemy waves
 * (LevelManager) from the creation of individual enemies (SpawnManager),
 * following the Single Responsibility Principle. The LevelManager acts as the
 * Architect that orchestrates spawning, while the SpawnManager acts as the
 * Creator (Factory) that builds each enemy.
 */
public class LevelManager {
    private final SpawnManager spawnManager;
    private Difficulty difficulty;
    private boolean backupSpawned;
    /**
     * Constructs a LevelManager with the given SpawnManager.
     * @param spawnManager the factory used to create enemy instances
     */
    public LevelManager(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
        this.backupSpawned = false;
    }
    /**
     * Sets the current difficulty level and resets the backup-spawned flag.
     * @param difficulty the chosen difficulty
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.backupSpawned = false;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
    /**
     * Spawns the initial wave of enemies for the current difficulty.
     * @return a list of enemies for the first wave
     * @throws IllegalStateException if difficulty has not been set
     */
    public List<Enemy> spawnInitialWave() {
        if (difficulty == null) {
            throw new IllegalStateException("Difficulty must be set before spawning enemies.");
        }
        List<Enemy> wave = new ArrayList<>();
        switch (difficulty) {
            case EASY -> {
                wave.add(spawnManager.createEnemy(EnemyType.GOBLIN, "Goblin A"));
                wave.add(spawnManager.createEnemy(EnemyType.GOBLIN, "Goblin B"));
                wave.add(spawnManager.createEnemy(EnemyType.GOBLIN, "Goblin C"));
            }
            case MEDIUM -> {
                wave.add(spawnManager.createEnemy(EnemyType.GOBLIN, "Goblin"));
                wave.add(spawnManager.createEnemy(EnemyType.WOLF, "Wolf A"));
            }
            case HARD -> {
                wave.add(spawnManager.createEnemy(EnemyType.GOBLIN, "Goblin A"));
                wave.add(spawnManager.createEnemy(EnemyType.GOBLIN, "Goblin B"));
            }
        }

        return wave;
    }

    /**
     * Spawns the backup wave of enemies, if one exists for the current
     * difficulty and has not already been deployed.
     * Easy mode has no backup wave. Medium and Hard each have a single
     * backup wave that is triggered when the initial wave is cleared.
     * @return the backup enemies, or an empty list if none remain
     */
    public List<Enemy> spawnBackupWave() {
        if (backupSpawned || !hasBackupWave()) {
            return List.of();
        }

        backupSpawned = true;
        List<Enemy> wave = new ArrayList<>();

        switch (difficulty) {
            case MEDIUM -> {
                wave.add(spawnManager.createEnemy(EnemyType.WOLF, "Wolf B"));
                wave.add(spawnManager.createEnemy(EnemyType.WOLF, "Wolf C"));
            }
            case HARD -> {
                wave.add(spawnManager.createEnemy(EnemyType.GOBLIN, "Goblin C"));
                wave.add(spawnManager.createEnemy(EnemyType.WOLF, "Wolf A"));
                wave.add(spawnManager.createEnemy(EnemyType.WOLF, "Wolf B"));
            }
            default -> { /* EASY has no backup */ }
        }

        return wave;
    }
    /**
     * Checks whether a backup wave exists for the current difficulty
     * and has not yet been spawned.
     * @return {@code true} if a backup wave is available to deploy
     */
    public boolean hasBackupWave() {
        return !backupSpawned && difficulty != Difficulty.EASY;
    }

    /**
     * Resets the backup-spawned flag so the level can be replayed.
     */
    public void reset() {
        this.backupSpawned = false;
    }
}
