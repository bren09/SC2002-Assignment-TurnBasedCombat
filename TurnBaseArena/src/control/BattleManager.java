package control;

import entity.*;
import boundary.*;
import java.util.*;

public class BattleManager {

    private final ActionMngr actionMngr = new ActionMngr();
    private final TurnOrderMngr turnOrderMngr = new SpeedBasedTurnOrder();
    private final SpawnManager spawnManager   = new SpawnManager();
    private final LevelManager levelManager   = new LevelManager(spawnManager);

    private final GameUI ui = new GameUI();
    private final InputManager input = new InputManager();

    private Player player;
    private final List<Enemy> currentEnemies = new ArrayList<>();
    private final Set<Combatant> actedThisRound = new HashSet<>();
    private int roundNumber = 0;

    // ================= START GAME =================
    public void startGame() {
        ui.showLoadingScreen();
        setupPlayer();
        chooseDifficulty();
        spawnInitialEnemies();
        runBattle();
    }

    // ================= SETUP =================
    private void setupPlayer() {
        String name = input.getPlayerName();
        int classChoice = input.getClassChoice();

        if (classChoice == 1){
            player = new Warrior(name);
        } else {
            player = new Wizard(name);
        }

        // choose 2 items
        for (int i = 0; i < 2; i++){
            int itemChoice = input.getItemChoice();
            switch(itemChoice){
                case 1 -> player.addItem(new Potion());
                case 2 -> player.addItem(new PowerStone());
                case 3 -> player.addItem(new SmokeBomb());
            }
        }
    }

    private void chooseDifficulty() {
        int choice = input.getDifficultyChoice();
        Difficulty difficulty = Difficulty.fromChoice(choice);
        levelManager.setDifficulty(difficulty);
    }

    private void spawnInitialEnemies() {
        currentEnemies.clear();
        currentEnemies.addAll(levelManager.spawnInitialWave());
    }

    // ================= MAIN LOOP =================
    private void runBattle() {
        while (player.isAlive() && !currentEnemies.isEmpty()){
            roundNumber++;
            ui.showRoundHeader(roundNumber);

            List<Combatant> order = turnOrderMngr.determineTurnOrder(getAllCombatants());
            actedThisRound.clear();

            for (Combatant c : order){
                if (!c.isAlive() || currentEnemies.isEmpty()) continue;

                if (c instanceof Player p){
                    runPlayerTurn(p);
                }
                else if (c instanceof Enemy e){
                    runEnemyTurn(e);
                }

                actedThisRound.add(c);
                c.updateEffects();
            }

            ui.showRoundEndStatus(player, currentEnemies, roundNumber);

            // ✅ FIX: remove dead enemies so game can end correctly
            currentEnemies.removeIf(e -> !e.isAlive());

            checkEnemyWave();
        }

        showCompletionScreen();
    }

    // ================= PLAYER TURN =================
    private void runPlayerTurn(Player p){
        if (!p.isStunned()){
            p.reduceCooldown();
        } else {
            System.out.println(p.getName() + " is stunned and skips turn!");
            return;
        }

        ui.showPlayerStatus(p);
        ui.showEnemyStatus(currentEnemies);

        int choice = input.getActionChoice(p);

        ActionType type = switch(choice){
            case 1 -> ActionType.BASIC_ATTACK;
            case 2 -> ActionType.DEFEND;
            case 3 -> ActionType.SPECIAL_SKILL;
            case 4 -> ActionType.ITEM;
            default -> null;
        };

        List<Combatant> targets = new ArrayList<>();
        int itemIndex = -1;

        // only alive enemies
        List<Enemy> aliveEnemies = currentEnemies.stream()
                .filter(Enemy::isAlive)
                .toList();

        if (type == ActionType.BASIC_ATTACK){
            Enemy target = input.getTargetChoice(aliveEnemies);
            targets.add(target);
        }
        else if (type == ActionType.SPECIAL_SKILL){
            if (p instanceof Warrior){
                Enemy target = input.getTargetChoice(aliveEnemies);
                targets.add(target);
            }
            else if (p instanceof Wizard){
                targets.addAll(aliveEnemies);
            }
        }
        else if (type == ActionType.ITEM){
            ui.showItemMenu(p.getInventory());
            itemIndex = input.getItemIndex(p.getInventory());
            targets.add(p);
        }

        actionMngr.executeAction(type, p, targets, itemIndex, actedThisRound);
    }

    // ================= ENEMY TURN =================
    private void runEnemyTurn(Enemy e){
        if (e.isStunned()){
            System.out.println(e.getName() + " is stunned and skips turn!");
            return;
        }

        actionMngr.executeEnemyMove(e, List.of(player));
    }

    // ================= BACKUP SPAWN =================
    private void checkEnemyWave(){
        if(currentEnemies.isEmpty() && levelManager.hasBackupWave()){
            System.out.println("\n⚠ Backup wave incoming!");
            currentEnemies.addAll(levelManager.spawnBackupWave());
        }
    }

    // ================= HELPERS =================
    private List<Combatant> getAllCombatants(){
        List<Combatant> all = new ArrayList<>();
        all.add(player);
        all.addAll(currentEnemies);
        return all;
    }

    // ================= END =================
    private void showCompletionScreen(){
        if(player.isAlive()){
            ui.showVictoryScreen(player, roundNumber);
        } else {
            ui.showDefeatScreen(currentEnemies.size(), roundNumber);
        }
    }
}