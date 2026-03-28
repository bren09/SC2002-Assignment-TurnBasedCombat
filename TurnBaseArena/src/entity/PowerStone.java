package entity;

import java.util.List;

public class PowerStone implements Item {
    @Override
    public String getName() { return "Power Stone"; }

    @Override
    public void use(Player user, List<Combatant> targets) {
        System.out.println("Power Stone triggers a free skill use!");
        user.executeSkillLogic(targets); 
    }
}