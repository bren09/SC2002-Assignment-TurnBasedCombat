package entity;

import java.util.ArrayList;
import java.util.List;

public abstract class Player extends Combatant{
    //Player specific attributes
    protected int currentSkillCooldown = 0; // Track cooldown of class specific skill
    protected final int maxSkillCooldown = 3; // Class skills cooldown is 3 turns
    protected List<Item> inventory; // Inventory for holding items

    // Constructor for Player, calls the constructor of Combatant to initialize common attributes
    public Player(String name, int hp, int attack, int defense, int speed){
        super(name, hp, attack, defense, speed);
        this.inventory = new ArrayList<>();
    }

    // Skill-related Methods
    public void useSkill(List<Combatant> targets){
        if (isSkillReady()){
            executeSkillLogic(targets);
            startCooldown();
        } else {
            System.out.println("Skill is on cooldown for " + currentSkillCooldown + " more turns!");
        }
    }
    public abstract void executeSkillLogic(List<Combatant> targets); 

    // Item-related Methods
    public void useItem(int itemIndex, List<Combatant> targets){
        if (inventory.isEmpty()){
            System.out.println(name + " has no items to use.");
            return;
        }
        if (itemIndex < 0 || itemIndex >= inventory.size()){
            System.out.println("Invalid item selected.");
            return;
        }
        Item item = inventory.remove(itemIndex);
        System.out.println(name + " uses " + item.getName() + ".");
        item.use(this, targets);
    }
    public void addItem(Item item){
        if (inventory.size() < 2){ 
            inventory.add(item);
            System.out.println(item.getName() + " added to inventory.");
        } else {
            System.out.println("Inventory is full.");
        }
    }

    // Getters for Control/Boundary classes
    public List<Item> getInventory() { return inventory; }
    public boolean hasItems() { return !inventory.isEmpty(); }

    // PlayerSkill-related Methods
    public void reduceCooldown() {
        if (currentSkillCooldown > 0) { currentSkillCooldown--; }
    }
    public boolean isSkillReady() {
        return currentSkillCooldown == 0;
    }
    public void startCooldown() {
        this.currentSkillCooldown = maxSkillCooldown;
    }
    public int getSkillCooldown(){
        return currentSkillCooldown;
    }
}
