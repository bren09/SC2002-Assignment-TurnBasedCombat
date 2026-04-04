package boundary;

import entity.*;
import java.util.List;
import java.util.Scanner;

public class InputManager {

    private final Scanner sc = new Scanner(System.in);

    // Generic input with validation
    public int getIntInput(int min, int max){
        int choice;
        while (true){
            try{
                System.out.print("Enter choice: ");
                choice = Integer.parseInt(sc.nextLine().trim());

                if (choice >= min && choice <= max){
                    return choice;
                }

                System.out.println("Invalid choice. Enter between " + min + " and " + max + ".");
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Setup Input
    public String getPlayerName(){
        String name = sc.nextLine().trim();
        return name.isEmpty() ? "Hero" : name;
    }

    public int getClassChoice(){
        return getIntInput(1, 2);
    }

    public int getItemChoice(){
        return getIntInput(1,3);
    }

    public int getDifficultyChoice(){
        return getIntInput(1,3);
    }

    // Battle Input
    public int getActionChoice(Player p){
        return p.hasItems() ? getIntInput(1, 4) : getIntInput(1, 3);
    }

    public int getItemIndex(List<Item> items){
        return getIntInput(1, items.size()) -1;
    }

    public Enemy getTargetChoice(List<Enemy> enemies){
        List<Enemy> alive = enemies.stream().filter(Enemy::isAlive).toList();
        if (alive.isEmpty()) throw new IllegalStateException("No enemies to target.");

        int choice = getIntInput(1, alive.size());
        return alive.get(choice-1);
    }

    // Replay Input
    public int getReplayInput(){
        return getIntInput(1, 3);
    }
}