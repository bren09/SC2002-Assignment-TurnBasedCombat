package entity;

import java.util.List;


public class Warrior extends Player {
    
    public Warrior(String name){
        super(name, 260, 40, 20, 30);
    }

    @Override
    public void executeSkillLogic(List<Combatant> targets) {
        // Warrior Special Skill: Shield Bash
        // Deal BA damage to selected target and stuns them for current and next turn
        // Target skips turn if they have not acted yet, if they have already acted, they will be stunned for the next turn only
        Combatant target = targets.get(0);
        System.out.println(this.getName() + " uses Shield Bash on " + target.getName() + "!");
        target.takeDamage(this.attack);
        // hasActedThisRound is passed in by BattleManager for setStun
        // Using placeholder 'false' for now
        target.setStun(false);
    }

    @Override
    public void takeTurn(){ 
        // Logic for taking turns will be implemented in Control/Boundary classes
        }
}
