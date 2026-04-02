package control;

import entity.*;
import java.util.*;

/**
 * Controls the full game loop:
 *   loading screen → player setup → difficulty selection → battle rounds → end screen.
 * Turn / effect timing rules:
 *   - Cooldown reduces at the START of the player's turn (skipped if stunned).
 *   - updateEffects() is called at the END of each combatant's turn (even when stunned).
 *   - This gives StunEffect(2) exactly 2 missed turns when applied before the target acts,
 *     or 1 missed turn (via StunEffect(1) overwrite) when applied after the target already acted.
 */
public class BattleManager {
    private Player player;
    private final List<Enemy> currentEnemies = new ArrayList<>();
    private final TurnOrderMngr turnOrderMngr = new SpeedBasedTurnOrder();
    private final SpawnManager spawnManager   = new SpawnManager();
    private final LevelManager levelManager   = new LevelManager(spawnManager);
    private final Scanner scanner = new Scanner(System.in);

    private int roundNumber = 0;

    // Tracks which combatants have already taken their turn this round.
    // Used to fix Shield Bash stun duration.
    private final Set<Combatant> actedThisRound = new HashSet<>();

    // =========================================================
    //  Entry point
    // =========================================================

    public void startGame() {
        showLoadingScreen();
        setupPlayer();
        chooseDifficulty();
        runBattle();
    }

    // =========================================================
    //  Loading / Setup
    // =========================================================

    private void showLoadingScreen() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         TURN-BASED ARENA COMBAT          ║");
        System.out.println("╚══════════════════════════════════════════╝");

        System.out.println("\n[ PLAYER CLASSES ]");
        System.out.println("  1. Warrior  | HP:260 | ATK:40 | DEF:20 | SPD:30");
        System.out.println("     Special: Shield Bash - Basic attack + stun target for current & next turn (CD: 3)");
        System.out.println("  2. Wizard   | HP:200 | ATK:50 | DEF:10 | SPD:20");
        System.out.println("     Special: Arcane Blast - Hit ALL enemies; each kill grants +10 ATK until end of level (CD: 3)");

        System.out.println("\n[ ENEMIES ]");
        System.out.println("  Goblin | HP:55  | ATK:35 | DEF:15 | SPD:25");
        System.out.println("  Wolf   | HP:40  | ATK:45 | DEF:5  | SPD:35");

        System.out.println("\n[ ITEMS ]");
        System.out.println("  1. Potion      - Restore 100 HP (capped at max HP)");
        System.out.println("  2. Power Stone - Trigger your special skill once for free (no cooldown change)");
        System.out.println("  3. Smoke Bomb  - Become invulnerable for current and next turn");

        System.out.println("\n[ DIFFICULTY ]");
        System.out.println("  1. Easy   - 3 Goblins");
        System.out.println("  2. Medium - 1 Goblin + 1 Wolf  |  Backup: 2 Wolves");
        System.out.println("  3. Hard   - 2 Goblins           |  Backup: 1 Goblin + 2 Wolves");
        System.out.println();
    }

    private void setupPlayer() {
        System.out.println("=== PLAYER SETUP ===");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = "Hero";

        System.out.println("Choose your class:  1. Warrior   2. Wizard");
        int classChoice = getIntInput(1, 2);
        player = (classChoice == 1) ? new Warrior(name) : new Wizard(name);
        System.out.println("You chose: " + player.getName() + " the " + player.getClass().getSimpleName());

        System.out.println("\nChoose 2 items (duplicates allowed):");
        for (int i = 1; i <= 2; i++) {
            System.out.println("Item " + i + ":  1. Potion   2. Power Stone   3. Smoke Bomb");
            int itemChoice = getIntInput(1, 3);
            switch (itemChoice) {
                case 1 -> player.addItem(new Potion());
                case 2 -> player.addItem(new PowerStone());
                case 3 -> player.addItem(new SmokeBomb());
            }
        }
    }

    private void chooseDifficulty() {
        System.out.println("\nSelect difficulty:  1. Easy   2. Medium   3. Hard");
        int choice = getIntInput(1, 3);
        levelManager.setDifficulty(Difficulty.fromChoice(choice));
        spawnInitialEnemies();
    }

    private void spawnInitialEnemies() {
        currentEnemies.clear();
        levelManager.reset();

        currentEnemies.addAll(levelManager.spawnInitialWave());

        System.out.println("\nEnemies appear!");
        for (Enemy e : currentEnemies) {
            System.out.printf("  - %-10s HP:%-4d ATK:%-4d DEF:%-4d SPD:%d%n",
                    e.getName(), e.getHp(), e.getAttack(), e.getDefense(), e.getSpeed());
        }
    }

    // =========================================================
    //  Battle loop
    // =========================================================

    private void runBattle() {
        System.out.println("\n=== BATTLE BEGIN ===");

        while (player.isAlive()) {
            // Backup spawn: triggered at the start of a round once the initial wave is cleared
            if (currentEnemies.isEmpty()) {
                List<Enemy> backup = levelManager.spawnBackupWave();
                if (!backup.isEmpty()) {
                    System.out.println("\n!!! BACKUP ENEMIES ARRIVE !!!");
                    currentEnemies.addAll(backup);
                    for (Enemy e : currentEnemies) {
                        System.out.println("  - " + e.getName() + " (HP: " + e.getHp() + ")");
                    }
                } else {
                    break; // no more enemies → victory
                }
            }

            roundNumber++;
            System.out.println("\n══════════  ROUND " + roundNumber + "  ══════════");
            runRound();

            // Remove eliminated enemies
            currentEnemies.removeIf(e -> !e.isAlive());
        }

        // Final result
        if (player.isAlive()) {
            showVictoryScreen();
        } else {
            showDefeatScreen();
        }
    }

    // =========================================================
    //  Round execution
    // =========================================================

    private void runRound() {
        actedThisRound.clear();

        List<Combatant> allCombatants = new ArrayList<>();
        allCombatants.add(player);
        allCombatants.addAll(currentEnemies);

        List<Combatant> turnOrder = turnOrderMngr.determineTurnOrder(allCombatants);

        for (Combatant combatant : turnOrder) {
            if (!combatant.isAlive()) continue;

            if (combatant.isStunned()) {
                System.out.println("\n" + combatant.getName() + " is STUNNED and cannot act this turn!");
                combatant.updateEffects(); // tick effects even on a skipped turn
                actedThisRound.add(combatant);
                continue;
            }

            if (combatant instanceof Player p) {
                runPlayerTurn(p);
            } else if (combatant instanceof Enemy e) {
                runEnemyTurn(e);
            }

            combatant.updateEffects(); // tick effects at end of turn
            actedThisRound.add(combatant);

            // Stop round early if player is eliminated
            if (!player.isAlive()) {
                System.out.println(player.getName() + " has been eliminated!");
                break;
            }
        }

        printRoundEndStatus();
    }

    // =========================================================
    //  Player turn
    // =========================================================

    private void runPlayerTurn(Player p) {
        boolean actionTaken = false;        

        while (!actionTaken) {
            System.out.println("\n[" + p.getName() + "'s Turn]  HP: " + p.getHp() + "/" + p.getMaxHp());
            printPlayerStatus(p);
            printEnemyStatus();

            System.out.println("Choose action:");
            System.out.println("  1. Basic Attack");
            System.out.printf ("  2. Special Skill%s%n",
                    p.isSkillReady() ? " (READY)" : " [Cooldown: " + p.getSkillCooldown() + " turn(s)]");
            System.out.printf ("  3. Use Item%s%n",
                    p.hasItems() ? " (" + p.getInventory().size() + " available)" : " (none)");
            System.out.println("  4. Defend  (+10 DEF for this round and the next)");

            int choice = getIntInput(1, 4);

            switch (choice) {
                case 1 -> {
                    Enemy target = selectEnemy("Select target:");
                    p.basicAttack(target);
                    actionTaken = true;
                }
                case 2 -> {
                    if (!p.isSkillReady()) {
                        System.out.println("Skill is on cooldown! Choose a different action.");
                    } else {
                        executePlayerSkill(p);
                        actionTaken = true;
                    }
                }
                case 3 -> {
                    if (!p.hasItems()) {
                        System.out.println("No items in inventory! Choose a different action.");
                    } else {
                        useItemMenu(p);
                        actionTaken = true;
                    }
                }
                case 4 -> {
                    p.addEffect(new DefendEffect());
                    actionTaken = true;
                }
            }
        }
        p.reduceCooldown(); // cooldown only ticks on turns the player actually takes

    }

    private void executePlayerSkill(Player p) {
        if (p instanceof Warrior warrior) {
            Enemy target = selectEnemy("Select target for Shield Bash:");
            warrior.useSkill(List.of(target));
            // If the target already took their turn this round, overwrite with 1-turn stun
            // (spec: stun covers current turn AND next turn only if target hasn't acted yet)
            if (target.isAlive() && actedThisRound.contains(target)) {
                target.addEffect(new StunEffect(1));
            }
        } else if (p instanceof Wizard wizard) {
            List<Combatant> targets = currentEnemies.stream()
                    .filter(Combatant::isAlive)
                    .map(e -> (Combatant) e)
                    .collect(java.util.stream.Collectors.toList());
            wizard.useSkill(targets);
        }
    }

    // =========================================================
    //  Enemy turn
    // =========================================================

    private void runEnemyTurn(Enemy e) {
        System.out.println("\n[" + e.getName() + "'s Turn]");
        if (player.isAlive()) {
            e.enemyAI(player);
        }
    }

    // =========================================================
    //  Item menu
    // =========================================================

    private void useItemMenu(Player p) {
        List<Item> inv = p.getInventory();
        System.out.println("Choose item:");
        for (int i = 0; i < inv.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + inv.get(i).getName());
        }
        int idx = getIntInput(1, inv.size()) - 1;
        Item item = inv.get(idx);

        if (item instanceof Potion) {
            p.useItem(idx, List.of(p)); // heals self

        } else if (item instanceof SmokeBomb) {
            p.useItem(idx, Collections.emptyList()); // no targets needed

        } else if (item instanceof PowerStone) {
            // Free skill use — pass the same targets the skill would normally receive
            if (p instanceof Warrior) {
                Enemy target = selectEnemy("Select target for Power Stone (Shield Bash):");
                p.useItem(idx, List.of(target));
                // Apply same stun-duration fix as the regular skill
                if (target.isAlive() && actedThisRound.contains(target)) {
                    target.addEffect(new StunEffect(1));
                }
            } else if (p instanceof Wizard) {
                List<Combatant> targets = currentEnemies.stream()
                        .filter(Combatant::isAlive)
                        .map(e -> (Combatant) e)
                        .collect(java.util.stream.Collectors.toList());
                p.useItem(idx, targets);
            }
        }
    }

    // =========================================================
    //  Target selection helper
    // =========================================================

    private Enemy selectEnemy(String prompt) {
        List<Enemy> alive = currentEnemies.stream().filter(Combatant::isAlive).toList();
        System.out.println(prompt);
        for (int i = 0; i < alive.size(); i++) {
            Enemy e = alive.get(i);
            System.out.printf("  %d. %-10s HP: %d/%d%s%n",
                    i + 1, e.getName(), e.getHp(), e.getMaxHp(),
                    e.isStunned() ? " [STUNNED]" : "");
        }
        return alive.get(getIntInput(1, alive.size()) - 1);
    }

    // =========================================================
    //  Display helpers
    // =========================================================

    private void printPlayerStatus(Player p) {
        List<String> tags = new ArrayList<>();
        if (p.isDefending()) tags.add("DEFENDING");
        if (p.isInvul())     tags.add("INVULNERABLE");
        if (!tags.isEmpty()) System.out.println("  Status: " + String.join(" | ", tags));

        if (p.hasItems()) {
            StringJoiner sj = new StringJoiner(", ");
            p.getInventory().forEach(item -> sj.add(item.getName()));
            System.out.println("  Items : " + sj);
        }
    }

    private void printEnemyStatus() {
        System.out.println("  Enemies:");
        for (Enemy e : currentEnemies) {
            if (e.isAlive()) {
                System.out.printf("    - %-10s HP: %d/%d%s%n",
                        e.getName(), e.getHp(), e.getMaxHp(),
                        e.isStunned() ? " [STUNNED]" : "");
            }
        }
    }

    private void printRoundEndStatus() {
        System.out.println("\n--- End of Round " + roundNumber + " ---");
        System.out.printf("%-12s HP: %d/%d  |  Skill CD: %d  |  Items: %d%n",
                player.getName(), player.getHp(), player.getMaxHp(),
                player.getSkillCooldown(), player.getInventory().size());
        for (Enemy e : currentEnemies) {
            if (e.isAlive()) {
                System.out.printf("%-12s HP: %d/%d%s%n",
                        e.getName(), e.getHp(), e.getMaxHp(),
                        e.isStunned() ? " [STUNNED]" : "");
            } else {
                System.out.println(e.getName() + " - ELIMINATED");
            }
        }
    }

    // =========================================================
    //  End screens
    // =========================================================

    private void showVictoryScreen() {
        if (player instanceof Wizard w) w.attackReset(); // reset Arcane Blast ATK buff

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   CONGRATULATIONS! YOU HAVE WON!         ║");
        System.out.println("║   You have defeated all your enemies.    ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("  Remaining HP  : " + player.getHp() + "/" + player.getMaxHp());
        System.out.println("  Total Rounds  : " + roundNumber);
    }

    private void showDefeatScreen() {
        long remaining = currentEnemies.stream().filter(Combatant::isAlive).count();

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   DEFEATED. Don't give up, try again!    ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("  Enemies Remaining   : " + remaining);
        System.out.println("  Total Rounds Survived: " + roundNumber);

        System.out.println("\nOptions:");
        System.out.println("  1. Replay with same settings");
        System.out.println("  2. Start new game");
        System.out.println("  3. Exit");

        switch (getIntInput(1, 3)) {
            case 1 -> {
                roundNumber = 0;
                String savedName = player.getName();
                player = (player instanceof Warrior) ? new Warrior(savedName) : new Wizard(savedName);
                spawnInitialEnemies(); // uses LevelManager with same difficulty
                runBattle();
            }
            case 2 -> startGame();
            case 3 -> System.out.println("Thanks for playing!");
        }
    }
    // =========================================================
    //  Input utility
    // =========================================================
    private int getIntInput(int min, int max) {
        while (true) {
            System.out.print("> ");
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // consume trailing newline
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }
}
