package control;

import entity.Enemy;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private final SpawnManager spawnManager;
    private Difficulty difficulty;
    private boolean backupSpawned;

    public LevelManager(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
        this.backupSpawned = false;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.backupSpawned = false;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

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

    public boolean hasBackupWave() {
        return !backupSpawned && difficulty != Difficulty.EASY;
    }

    public void reset() {
        this.backupSpawned = false;
    }
}
