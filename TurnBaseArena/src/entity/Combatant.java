package entity;

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
    protected  boolean isStunned = false;
    protected List<StatusEffect> activeEffects;

    // Constructor for combatant with all attributes initialized
    // Take note that StatusEffect is not created yet, so code will not compile, hence the red underline.
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
        // Checks smoke bomb item
        if (this.isInvul()){
            System.out.println(name + " is invulnerable and takes no damage!");
            return; // No damage taken
        }

        int actualDamage = Math.max(0, rawDamage - this.defense); // Damage cannot be negative
        this.hp = Math.max(0, this.hp - actualDamage); // HP cannot go below 0

        // Output current hp -> new hp, and damage taken
        System.out.println(name + " takes " + actualDamage + " damage! (HP: " + (hp + actualDamage) + " -> " + hp + ")");   
    }

    // Logic for healing
    public void heal(int healAmount){
        int oldHP = this.hp;
        this.hp = Math.min(this.maxHp, this.hp + healAmount);
        System.out.println(name + " heals for " + (this.hp - oldHP) + " HP! (HP: " + oldHP + " -> " + this.hp + ")");
    }

    // Boolean method to check if the combatant is still alive
    public boolean isAlive() {
        return this.hp > 0;
    }

    // Basic Attack Method
    public void basicAttack(Combatant target){
        System.out.println(this.name + " attacks " + target.getName() + ".");
        target.takeDamage(this.attack);
    }

    // Status-related Methods
    public void addEffect(StatusEffect effect){
        // If duplicate effect, overwrites the existing one
        activeEffects.removeIf(e -> e.getName().equals(effect.getName()));
        activeEffects.add(effect);

        if (effect instanceof DefendEffect){
            adjustDef(10);
            System.out.println(name + " takes a defensive stance, raising defence.");
        } else if (effect instanceof StunEffect) {
            this.isStunned = true;
            System.out.println(name + " is now stunned.");
        }
    }

    public void updateEffects(){
        if (activeEffects.isEmpty()) return;
        List<StatusEffect> statusToRemove = new ArrayList<>();
        for (StatusEffect effect : activeEffects){
            effect.apply(this);
            effect.update(this);
            if (effect.isExpired()){
                statusToRemove.add(effect);
            }
        }
        activeEffects.removeAll(statusToRemove);
    }

    public void setStunned(boolean stunned) { this.isStunned = stunned;}
    public void adjustDef(int amount) { this.defense += amount; }
    public boolean isInvul() { return activeEffects.stream().anyMatch(e -> e instanceof InvulEffect); }
    public boolean isDefending() { return activeEffects.stream().anyMatch(e -> e instanceof DefendEffect); }

    // Abstract method for taking a turn, to be implemented by child class of Player and Enemy
    public abstract void takeTurn();

    // Getter methods for BattleManager and TurnOrderManager
    public int getSpeed() {return this.speed;}
    public String getName() {return this.name;}
    public int getHp() {return this.hp;}
    public int getAttack() { return this.attack; }
    public int getDefense() { return this.defense; }
    public int getMaxHp() { return this.maxHp; }
    public List<StatusEffect> getActiveEffects() { return activeEffects;}
    public boolean isStunned() {return this.isStunned;}
    
}
