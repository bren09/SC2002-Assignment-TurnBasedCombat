package control;

import entity.*;


public class SpawnManager {

    public Enemy createEnemy(EnemyType type, String name) {
        return switch (type) {
            case GOBLIN -> new Goblin(name);
            case WOLF   -> new Wolf(name);
        };
    }
}
