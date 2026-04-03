package control;

import entity.*;
import java.util.*;

public class BattleManager {

    // Control & Boundary dependencies
    private final ActionMngr actionMngr = new ActionMngr();
    private final TurnOrderMngr turnOrderMngr = new SpeedBasedTurnOrder();
    private final SpawnManager spawnManager   = new SpawnManager();
    private final LevelManager levelManager   = new LevelManager(spawnManager);

    //Uncomment after boundary classes are done
    //private final GameUI ui = new GameUI();
    //private final InputManager input = new InputManager();

    // State of the Game
    private Player player;
    private final List<Enemy> currentEnemies = new ArrayList<>();
    private final Set<Combatant> actedThisRound = new HashSet<>();
    private int roundNumber = 0;

    public void startGame() {
        // show loading screen
        // setupPlayer();
        // chooseDifficulty();
        // spawnInitialEnemies();
        // runBattle();
    }

    private void setupPlayer() {
        // implement when UI and input class is done
    }

    private void chooseDifficulty() {
        // implement when UI and input class is done
    }

    private void spawnInitialEnemies() {
        currentEnemies.clear();
        currentEnemies.addAll(levelManager.spawnInitialWave());
    }


    private void runBattle() {
        while (player.isAlive() && !currentEnemies.isEmpty()){
            roundNumber++;
            // implement ui.showRoundHeader

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
            checkEnemyWave();
        }
        showCompletionScreen();
    }

    private void runPlayerTurn(Player p){
        if (!p.isStunned()){
            p.reduceCooldown();
        }

        //show player status
        //show enemy status

        //get action from inputManager

        //get ActionMngr to perform the required logic
        
    }

    private void runEnemyTurn(Enemy e){
        if (e.isStunned()){
            //show ui stunned message
            return;
        }
        actionMngr.executeEnemyMove(e, List.of(player));
    }

    private void checkEnemyWave(){
        if(currentEnemies.isEmpty() && levelManager.hasBackupWave()){
            //show ui backup wave message
            currentEnemies.addAll(levelManager.spawnBackupWave());
        }
    }
    
    private List<Combatant> getAllCombatants(){
        List<Combatant> allEntities = new ArrayList<>();
        allEntities.add(player);
        allEntities.addAll(currentEnemies);
        return allEntities;
    }

    private void showCompletionScreen(){
        if(player.isAlive()){
            //implement ui show victory screen
        } else {
            //implement ui show defeat screen
        }
    }

}
