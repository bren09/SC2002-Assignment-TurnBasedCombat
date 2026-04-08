package boundary;

import entity.*;
import java.util.List;

public class GameUI {

    // LOADING SCREEN
    public void showLoadingScreen(){
        System.out.println("======================================");
        System.out.println("   TURN-BASED COMBAT ARENA GAME");
        System.out.println("======================================\n");

        System.out.println("Classes:");
        System.out.println("Warrior -> HP:260 ATK:40 DEF:20 SPD:30");
        System.out.println("Wizard  -> HP:200 ATK:50 DEF:10 SPD:20");

        System.out.println("\nEnemies:");
        System.out.println("Goblin -> HP:55 ATK:35 DEF:15 SPD:25");
        System.out.println("Wolf   -> HP:40 ATK:45 DEF:5  SPD:35");

        System.out.println("\nItems:");
        System.out.println("Potion      -> Heal 100HP");
        System.out.println("Power Stone -> Free extra use of skill");
        System.out.println("Smoke Bomb  -> Enemy does 0 damage for current & next turn.\n");

        System.out.println("\nDifficulty Levels:");
        System.out.println("Easy    - 3 Goblins");
        System.out.println("Medium  - 1 Goblin 1 Wolf | Backup: 2 Wolves");
        System.out.println("Hard    - 2 Goblins | Backup: 1 Goblin + 2 Wolves");



    }

    // SETUP
    public void providePlayerName(){
        System.out.println("\n=== PLAYER SETUP ===");
        System.out.print("Enter your name: ");
    }

    public void provideClassChoice(){
        System.out.println("\nChoose your class:");
        System.out.println(" 1. Warrior");
        System.out.println(" 2. Wizard");
    }

    public void showChosenClass(Player p){
        System.out.println("You are " + p.getName() + " the " + p.getClass().getSimpleName());
    }

    public void provideItemChoice(int itemNumber){
        System.out.println("\nChoose item no." + itemNumber + " (duplicates allowed):");
        System.out.println(" 1. Potion");
        System.out.println(" 2. Power Stone");
        System.out.println(" 3. Smoke Bomb");
    }

    public void provideDifficultyChoice(){
        System.out.println("\nSelect difficulty level:");
        System.out.println(" 1. Easy");
        System.out.println(" 2. Medium");
        System.out.println(" 3. Hard");
    }

    // ROUND DISPLAY
    public void showRoundHeader(int round){
        System.out.println("\n======================================");
        System.out.println("              ROUND " + round);
        System.out.println("======================================");
    }

    public void showStunMessage(Combatant c){
        System.out.println("\n" + c.getName() + " is stunned and unable to act.");
    }

    public void showTurnorder(List<Combatant> o){
        System.out.print("Turn Order: ");
        for (int i = 0; i < o.size(); i++){
            Combatant c = o.get(i);
            System.out.print(c.getName() + " (SPD:" + c.getSpeed() +")");
            if (i<o.size() - 1){
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    public void showPlayerStatus(Player p){
        System.out.println("\nPlayer Status:");
        System.out.println(p.getName() +
                " | HP: " + p.getHp() + "/" + p.getMaxHp() +
                " | ATK: " + p.getAttack() +
                " | DEF: " + p.getDefense() +
                " | Skill CD: " + (p.isSkillReady() ? "Ready" : p.getSkillCooldown() + " turn(s)"));

        if (!p.getActiveEffects().isEmpty()){
            System.out.print("Effects: ");
            for (StatusEffect e : p.getActiveEffects()){
                System.out.print(e.getName() + "(" + e.getRemainingDuration() + ") ");
            }
            System.out.println();
        }

        if (p.hasItems()){
            System.out.print("Items: ");
            p.getInventory().forEach(item ->  System.out.print(item.getName() + " | "));
        }
    }

    public void showEnemyStatus(List<Enemy> enemies){
        System.out.println("\nEnemies:");
        for (Enemy e : enemies){
            if (e.isAlive()){
                System.out.print(e.getName() +
                        " | HP: " + e.getHp());
                
                if (e.isStunned())  System.out.print(" STUNNED");
                if (!e.getActiveEffects().isEmpty()){
                    System.out.print(" | Effects: ");
                    for (StatusEffect ef : e.getActiveEffects()){
                        System.out.print(ef.getName() + "(" + ef.getRemainingDuration() + ") ");
                    }
                }

                System.out.println();
            }
        }
    }

    public void showRoundEndStatus(Player p, List<Enemy> enemies, int round){
        System.out.println("\n--- End of Round " + round + " ---");
        System.out.printf("%-12s HP: %d/%d  |  Skill CD: %d  | Items: %d%n",
            p.getName(), p.getHp(), p.getMaxHp(), 
            p.getSkillCooldown(), p.getInventory().size());
        
        for (Enemy e : enemies){
            if(e.isAlive()){
                 System.out.printf(" %-12s HP: %d/%d%s%n",
                 e.getName(), e.getHp(), e.getMaxHp(),
                 e.isStunned() ? " Stunned " : "");
            } else {
                 System.out.println(" " + e.getName() + " - Dead");
            }
        }
    }

    // SPAWN MESSAGES
    public void showInitialWave(List<Enemy> enemies){
        System.out.println("\nEnemies have spawned!");
        for (Enemy e : enemies) {
            System.out.printf("  - %-10s HP:%-4d ATK:%-4d DEF:%-4d SPD:%d%n",
            e.getName(), e.getHp(), e.getAttack(), e.getDefense(), e.getSpeed());
        }
    }

    public void showBackupWave(List<Enemy> enemies){
        System.out.println("\n Backup has arrived!!!");
        for (Enemy e : enemies){
             System.out.println("  - " + e.getName() + " (HP: " + e.getHp() + ")");
        }
    }

    // SCREEN MENUS
    public void showVictoryScreen(Player p, int rounds){
        System.out.println("\n======================================");
        System.out.println("VICTORY!");
        System.out.println("Remaining HP    : " + p.getHp() + "/" + p.getMaxHp());
        System.out.println("Total Rounds    : " + rounds);
        System.out.println("Items Left      : " + p.getInventory().size());
        System.out.println("======================================");
    }

    public void showDefeatScreen(int enemiesRemaining, int rounds){
        System.out.println("\n======================================");
        System.out.println("DEFEAT!");
        System.out.println("Enemies Remaining: " + enemiesRemaining);
        System.out.println("Rounds Survived: " + rounds);
        System.out.println("======================================");
    }

    public void showReplayMenu() {
        System.out.println("\nReplay or Restart?");
        System.out.println(" 1. Replay");
        System.out.println(" 2. Restart");
        System.out.println(" 3. Exit");
    }

    public void showExitScreen() {
        System.out.println("Game Exited - Thank You!");
    }

    public void showSkillCDMessage(int turnsLeft){
        System.out.println("Skill is still on cooldown for " + turnsLeft + " more turn(s), pick another action.");
    }

    public void showNoItemMessage(){
        System.out.println("You have ran out of items.....");
    }

    // ACTION MENUS
    public void showActionMenu(Player p){
        System.out.println("\nActions:");
        System.out.println("1. Basic Attack");
        System.out.println("2. Defend");
        System.out.printf("3. Special Skill%s%n", 
        p.isSkillReady() ? " [Ready]" : " [Cooldown: " + p.getSkillCooldown() + " turn(s)]");
        if (p.hasItems()){
            System.out.println("4. Use Item (" + p.getInventory().size() + " available items)");
        }
    }

    public void showItemMenu(List<Item> items){
        System.out.println("\nItems:");
        for (int i = 0; i < items.size(); i++){
            System.out.println(" " + (i+1) + ". " + items.get(i).getName());
        }
    }

    public void showEnemyList(List<Enemy> enemies){
        System.out.println("\nTargets:");
        int i = 1;
        for (Enemy e : enemies){
            if (e.isAlive()){
                System.out.printf(" %d. %-10s HP: %d/%d%s%n",
                    i++, e.getName(), e.getHp(), e.getMaxHp(),
                    e.isStunned() ? " (Stunned)" : "");
            }
        }
    }
}