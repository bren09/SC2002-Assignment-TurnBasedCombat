import java.util.ArrayList;
import java.util.List;

public abstract class Combatant {
    // Protected access so that child classes can access these attributes directly but they are not accessible from outside the class hierarchy

    // Common attributes for all combatants
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected int speed;

    // Status effects
    protected boolean isStunned;
    protected List<StatusEffect> activeEffects;

    // Constructor for combatant with all attributes initialized
    // 27.03.2026: Take note that StatusEffect is not created yet, so code will not compile, hence the red underline.
    public Combatant(String name, int hp, int attack, int defense, int speed) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp; // MaxHP is the starting Hp of the combatant
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.isStunned = false;
        this.activeEffects = new ArrayList<>();
    }

    // Logic for taking damage, applicable to all combatants, so it is implemented in the grandparent class.
    public void takeDamage(int rawDamage){
        int actualDamage = Math.max(0, rawDamage - this.defense); // Damage cannot be negative
        this.hp = Math.max(0, this.hp - actualDamage); // HP cannot go below 0

        // Output current hp -> new hp, and damage taken
        System.out.println(name + " takes " + actualDamage + " damage! (HP: " + (hp + actualDamage) + " -> " + hp + ")");   
    }

    // Boolean method to check if the combatant is still alive
    public boolean isAlive() {
        return this.hp > 0;
    }

    // Getter methods for BattleManager and TurnOrderManager
    public int getSpeed() {return this.speed;}
    public String getName() {return this.name;}
    public int getHp() {return this.hp;}
    public boolean isStunned() {return this.isStunned;}

    // Setter method for stunned status effect, used by StatusEffect class
    public void setStunned(boolean stunned) {this.isStunned = stunned;}

    // Abstract method for taking a turn, to be implemented by child class of Player and Enemy
    public abstract void takeTurn();

    // To be updated later if there is any changes down the line
}
