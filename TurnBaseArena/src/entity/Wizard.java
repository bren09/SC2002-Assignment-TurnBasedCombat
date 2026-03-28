package entity;

import java.util.List;


public class Wizard extends Player {
    private final int baseAttack;

    public Wizard(String name){
        super(name, 200, 50, 10, 20);
        this.baseAttack = 50;
    }

    @Override
    public void executeSkillLogic(List<Combatant> targets) {
        // Wizard Special Skill: Arcane Blast
        // Deal BA damage to all enemies currently in combat, each enemy defeated by
        // Arcane Blast adds 10 to the Wizard's Attack, lasting until end of the level
        System.out.println(this.getName() + " uses Arcane Blast!");
        for (Combatant target : targets){
            boolean wasAlive = target.isAlive();
            target.takeDamage(this.attack);

            if (wasAlive && !target.isAlive()){
                this.attack += 10;
                System.out.println(this.getName() + "'s power increases! Attack is now " + this.attack);
            }
        }
    }

    // Called by Level or Battle Mngr for resetting attack at the end of level
    public void attackReset(){
        this.attack = baseAttack;
    }

    @Override
    public void takeTurn(){
        // Logic for taking turns will be implemented in Control/Boundary classes
    }
}
