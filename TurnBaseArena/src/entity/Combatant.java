package entity;

public abstract class Combatant {
    // Protected access so that child classes can access these attributes directly but they are not accessible from outside the class hierarchy

    // Common attributes for all combatants
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int attack;
    protected int defense;
    protected int speed;

    // Additional attributes
    protected int invulTurns = 0; 
    protected int defUp = 0;
    protected int defTurns = 0;

    // Status effects
    protected boolean isStunned;
    protected int stunnedTurns = 0;
    //protected List<StatusEffect> activeEffects;

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
        this.stunnedTurns = 0;
        this.invulTurns = 0;
        this.defUp = 0;
        this.defTurns = 0;
        //this.activeEffects = new ArrayList<>();
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

    //Invulnerability related methods
    // Call this method at the end of each turn to reduce invul turns by 1
    public void reduceInvulTurns() {
        if (invulTurns > 0) {
            invulTurns--;
            System.out.println(name + " has " + invulTurns + " invulnerability turns left.");
        }
    }
    // Method to set invul turns for smoke bomb item
    public void setInvulTurns(int turns) {
        this.invulTurns = turns;
    }
    // Method to check invul status
    public boolean isInvul(){ return this.invulTurns>0;}

    // Boolean method to check if the combatant is still alive
    public boolean isAlive() {
        return this.hp > 0;
    }

    // Getter methods for BattleManager and TurnOrderManager
    public int getSpeed() {return this.speed;}
    public String getName() {return this.name;}
    public int getHp() {return this.hp;}
    public boolean isStunned() {return this.isStunned;}

    // Stun-related methods
    public void setStun(boolean hasActedThisRound){
        this.stunnedTurns = hasActedThisRound ? 1:2;
        this.isStunned = true;
        System.out.println(name + " is stunned for " + stunnedTurns + " turn(s).");
    }
    public void reduceStunTurn(){
        if (stunnedTurns>0){
            stunnedTurns--;
            if (stunnedTurns == 0){
                isStunned = false;
                System.out.println(name + " is not stunned anymore.");
            }
        }
    }

    // Defending-related methods
    public void setDefend(){
        this.defUp = 10;
        this.defTurns = 2;
        this.defense += defUp;
        System.out.println(name + " takes a defensive state.");
    }
    public void reduceDefTurns(){
        if (defTurns>0){
            defTurns--;
            if (defTurns==0){
                this.defense -= defUp;
                this.defUp = 0;
                System.out.println(name + "'s defensive state has worn off.");
            }
        }
    }
    public boolean isDefending(){
        return this.defTurns>0;
    }

    // Basic Attack Method
    public void basicAttack(Combatant target){
        System.out.println(this.name + " attacks " + target.getName() + ".");
        target.takeDamage(this.attack);
    }

    // Abstract method for taking a turn, to be implemented by child class of Player and Enemy
    public abstract void takeTurn();

    
}
