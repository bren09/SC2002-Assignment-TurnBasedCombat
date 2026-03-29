package entity;

import java.util.List;

public class Potion implements Item {
    @Override
    public String getName() { return "Potion"; }

    @Override
    public void use(Player user, List<Combatant> targets) {
        if (targets.isEmpty()){
            System.out.println("No heal targets.");
            return;
        }
        targets.get(0).heal(100);
    }
}
