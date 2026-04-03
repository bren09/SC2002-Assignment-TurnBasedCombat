package boundary;

import entity.*;
import java.util.List;

public class GameUI {

    public void showLoadingScreen(){
        System.out.println("======================================");
        System.out.println("   TURN-BASED COMBAT ARENA GAME");
        System.out.println("======================================\n");

        System.out.println("Classes:");
        System.out.println("Warrior -> HP:260 ATK:40 DEF:20 SPD:30");
        System.out.println("Wizard  -> HP:200 ATK:50 DEF:10 SPD:20");

        System.out.println("\nEnemies:");
        System.out.println("Goblin -> HP:55 ATK:35 DEF:15 SPD:25");
        System.out.println("Wolf   -> HP:40 ATK:45 DEF:5  SPD:35\n");
    }

    public void showRoundHeader(int round){
        System.out.println("\n======================================");
        System.out.println("              ROUND " + round);
        System.out.println("======================================");
    }

    public void showPlayerStatus(Player p){
        System.out.println("\nPlayer Status:");
        System.out.println(p.getName() +
                " | HP: " + p.getHp() + "/" + p.getMaxHp() +
                " | ATK: " + p.getAttack() +
                " | DEF: " + p.getDefense());

        if (!p.getActiveEffects().isEmpty()){
            System.out.print("Effects: ");
            for (StatusEffect e : p.getActiveEffects()){
                System.out.print(e.getName() + "(" + e.getRemainingDuration() + ") ");
            }
            System.out.println();
        }
    }

    public void showEnemyStatus(List<Enemy> enemies){
        System.out.println("\nEnemies:");
        for (Enemy e : enemies){
            if (e.isAlive()){
                System.out.print(e.getName() +
                        " | HP: " + e.getHp());

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
        showPlayerStatus(p);
        showEnemyStatus(enemies);
    }

    public void showVictoryScreen(Player p, int rounds){
        System.out.println("\n======================================");
        System.out.println("🎉 VICTORY!");
        System.out.println("Remaining HP: " + p.getHp());
        System.out.println("Total Rounds: " + rounds);
        System.out.println("======================================");
    }

    public void showDefeatScreen(int enemiesRemaining, int rounds){
        System.out.println("\n======================================");
        System.out.println("💀 DEFEAT!");
        System.out.println("Enemies Remaining: " + enemiesRemaining);
        System.out.println("Rounds Survived: " + rounds);
        System.out.println("======================================");
    }

    public void showActionMenu(Player p){
        System.out.println("\nActions:");
        System.out.println("1. Basic Attack");
        System.out.println("2. Defend");
        System.out.println("3. Special Skill");
        if (p.hasItems()){
            System.out.println("4. Use Item");
        }
    }

    public void showItemMenu(List<Item> items){
        System.out.println("\nItems:");
        for (int i = 0; i < items.size(); i++){
            System.out.println((i+1) + ". " + items.get(i).getName());
        }
    }

    public void showEnemyList(List<Enemy> enemies){
        System.out.println("\nTargets:");
        int i = 1;
        for (Enemy e : enemies){
            if (e.isAlive()){
                System.out.println(i + ". " + e.getName() + " (HP: " + e.getHp() + ")");
                i++;
            }
        }
    }
}