package control;

import boundary.*;
import entity.*;
import java.util.*;

public class BattleManager {

    // Collaborators
    private final ActionMngr actionMngr = new ActionMngr();
    private final TurnOrderMngr turnOrderMngr = new SpeedBasedTurnOrder();
    private final SpawnManager spawnManager   = new SpawnManager();
    private final LevelManager levelManager   = new LevelManager(spawnManager);
    private final GameUI ui = new GameUI();
    private final InputManager input = new InputManager();

    // States
    private Player player;
    private final List<Enemy> currentEnemies = new ArrayList<>();
    private final Set<Combatant> actedThisRound = new HashSet<>();
    private final List<Item> saveItems = new ArrayList<>();
    private int roundNumber = 0;

    // Start Game
    public void startGame() {
        ui.showLoadingScreen();
        setupPlayer();
        chooseDifficulty();
        spawnInitialEnemies();
        runBattle();
    }

    // Setup
    private void setupPlayer() {
        ui.providePlayerName();
        String name = input.getPlayerName();

        ui.provideClassChoice();
        int classChoice = input.getClassChoice();
        player = (classChoice == 1) ? new Warrior(name) : new Wizard(name);
        ui.showChosenClass(player);

        saveItems.clear();
        for (int i = 1; i<=2; i++){
            ui.provideItemChoice(i);
            int itemChoice = input.getItemChoice();
            Item item = switch (itemChoice) {
                case 1 -> new Potion();
                case 2 -> new PowerStone();
                case 3 -> new SmokeBomb();
                default -> null;
            };
            player.addItem(item);
            saveItems.add(item);
        }
    }

    private void chooseDifficulty() {
        ui.provideDifficultyChoice();
        int choice = input.getDifficultyChoice();
        levelManager.setDifficulty(Difficulty.fromChoice(choice));
    }

    private void spawnInitialEnemies() {
        currentEnemies.clear();
        List<Enemy> wave = levelManager.spawnInitialWave();
        currentEnemies.addAll(wave);
        ui.showInitialWave(wave);
    }

    // Main battle loop
    private void runBattle() {
        while (player.isAlive() && !currentEnemies.isEmpty()){
            roundNumber++;
            ui.showRoundHeader(roundNumber);

            List<Combatant> order = turnOrderMngr.determineTurnOrder(getAllCombatants());
            actedThisRound.clear();

            for (Combatant c : order){
                if (!c.isAlive()) continue;
                if (currentEnemies.stream().noneMatch(Combatant::isAlive)) break;
                if (c.isStunned()){
                    ui.showStunMessage(c);
                    c.updateEffects();
                    continue;
                }

                switch (c) {
                    case Player p -> runPlayerTurn(p);
                    case Enemy e -> runEnemyTurn(e);
                    default -> {
                    }
                }

                c.updateEffects();
                actedThisRound.add(c);

                if (!player.isAlive()) break;
            }

            ui.showRoundEndStatus(player, currentEnemies, roundNumber);

            // remove dead enemies so game can end correctly
            currentEnemies.removeIf(e -> !e.isAlive());

            checkEnemyWave();
        }
        showCompletionScreen();
    }

    // Player turn
    private void runPlayerTurn(Player p){

        p.reduceCooldown();

        boolean takenAction = false;

        while(!takenAction){
            ui.showPlayerStatus(p);
            ui.showEnemyStatus(currentEnemies);
            ui.showActionMenu(p);
        

            int choice = input.getActionChoice(p);

            ActionType type = switch(choice){
                case 1 -> ActionType.BASIC_ATTACK;
                case 2 -> ActionType.DEFEND;
                case 3 -> ActionType.SPECIAL_SKILL;
                case 4 -> ActionType.ITEM;
                default -> null;
            };

            if (type == ActionType.SPECIAL_SKILL && !p.isSkillReady()){
                ui.showSkillCDMessage(p.getSkillCooldown());
                continue;
            }
            if (type == ActionType.ITEM && !p.hasItems()) {
                ui.showNoItemMessage();
                continue;
            }

            List<Combatant> targets = createTargets(type, p);
            int itemIndex = ( type == ActionType.ITEM) ? sortItemIndex(p) : -1;

            if( type == ActionType.ITEM){
                targets = createItemTargets(p, itemIndex);
            }

            actionMngr.executeAction(type, p, targets, itemIndex, actedThisRound);
            takenAction = true;
        }
    }


    private List<Combatant> createTargets(ActionType type, Player p){
        // build the list of targets for non-item actions
        List<Enemy> aliveEnemies = currentEnemies.stream()
        .filter(Enemy::isAlive)
        .toList();

        List<Combatant> targets = new ArrayList<>();

        if(type == ActionType.BASIC_ATTACK){
            ui.showEnemyList(aliveEnemies);
            targets.add(input.getTargetChoice(aliveEnemies));
        }
        else if ( type == ActionType.SPECIAL_SKILL){
            if (p instanceof Warrior){
            ui.showEnemyList(aliveEnemies);
            targets.add(input.getTargetChoice(aliveEnemies));
        }
            else if (p instanceof Wizard){
            targets.addAll(aliveEnemies);
        }
      }
    return targets;
    }
    private int sortItemIndex(Player p){
        ui.showItemMenu(p.getInventory());
        return input.getItemIndex(p.getInventory());
    }

    private List<Combatant> createItemTargets(Player p, int itemIndex){
        List<Enemy> aliveEnemies = currentEnemies.stream()
        .filter(Enemy::isAlive)
        .toList();

        Item selected = p.getInventory().get(itemIndex);
        List<Combatant> targets = new ArrayList<>();

        if (selected instanceof PowerStone && p instanceof Warrior){
            ui.showEnemyList(aliveEnemies);
            targets.add(input.getTargetChoice(aliveEnemies));
        } else if (selected instanceof PowerStone && p instanceof Wizard){
            targets.addAll(aliveEnemies);
        } else {
            targets.add(p);
        }
        return targets;
    }

    // Enemy turn
    private void runEnemyTurn(Enemy e){
        actionMngr.executeEnemyMove(e, player);
    }

    // Backup spawn
    private void checkEnemyWave(){
        if(currentEnemies.isEmpty() && levelManager.hasBackupWave()){
            List<Enemy> backup = levelManager.spawnBackupWave();
            currentEnemies.addAll(backup);
            ui.showBackupWave(backup);
        }
    }

    // Helpers
    private List<Combatant> getAllCombatants(){
        List<Combatant> all = new ArrayList<>();
        all.add(player);
        all.addAll(currentEnemies);
        return all;
    }

    // End
    private void showCompletionScreen(){
        if(player.isAlive()){
            if (player instanceof Wizard w) w.attackReset();
            ui.showVictoryScreen(player, roundNumber);
        } else {
            int leftoverEnemies = (int) currentEnemies.stream().filter(Combatant::isAlive).count();
            ui.showDefeatScreen(leftoverEnemies, roundNumber);
            ui.showReplayMenu();
            int choice = input.getReplayInput();
            switch (choice){
                case 1 -> replayBattle();
                case 2 -> startGame();
                case 3 -> ui.showExitScreen();
            }
        }
    }

    // Replay
    private void replayBattle(){
        roundNumber = 0;
        String saveName = player.getName();
        player = (player instanceof Warrior) ? new Warrior(saveName) : new Wizard(saveName);
        for (Item item : saveItems) player.addItem(item);
        levelManager.reset();
        spawnInitialEnemies();
        runBattle();
    }
}