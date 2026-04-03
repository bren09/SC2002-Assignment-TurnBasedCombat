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
                choice = Integer.parseInt(sc.nextLine());

                if (choice >= min && choice <= max){
                    return choice;
                }

                System.out.println("Invalid choice. Enter between " + min + " and " + max + ".");
            } catch (Exception e){
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public String getPlayerName(){
        System.out.print("Enter your name: ");
        return sc.nextLine();
    }

    public int getClassChoice(){
        System.out.println("\nChoose your class:");
        System.out.println("1. Warrior");
        System.out.println("2. Wizard");
        return getIntInput(1,2);
    }

    public int getItemChoice(){
        System.out.println("\nChoose an item:");
        System.out.println("1. Potion");
        System.out.println("2. Power Stone");
        System.out.println("3. Smoke Bomb");
        return getIntInput(1,3);
    }

    public int getDifficultyChoice(){
        System.out.println("\nChoose difficulty:");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        return getIntInput(1,3);
    }

    public int getActionChoice(Player p){
        System.out.println("\nChoose action:");
        System.out.println("1. Basic Attack");
        System.out.println("2. Defend");
        System.out.println("3. Special Skill");

        if (p.hasItems()){
            System.out.println("4. Use Item");
            return getIntInput(1,4);
        }

        return getIntInput(1,3);
    }

    public int getItemIndex(List<Item> items){
        System.out.println("\nChoose item:");
        for (int i = 0; i < items.size(); i++){
            System.out.println((i+1) + ". " + items.get(i).getName());
        }
        return getIntInput(1, items.size()) - 1;
    }

    public Enemy getTargetChoice(List<Enemy> enemies){
        System.out.println("\nChoose target:");

        int count = 1;
        for (Enemy e : enemies){
            if (e.isAlive()){
                System.out.println(count + ". " + e.getName() + " (HP: " + e.getHp() + ")");
                count++;
            }
        }

        int choice = getIntInput(1, count - 1);

        count = 1;
        for (Enemy e : enemies){
            if (e.isAlive()){
                if (count == choice){
                    return e;
                }
                count++;
            }
        }

        return null; // should never reach
    }
}