package entity;

import java.util.List;

public class SmokeBomb implements Item {
    @Override
    public String getName() { return "Smoke Bomb"; }

    @Override
    public void use(Player user, List<Combatant> target) {
        user.addEffect(new InvulEffect(2)); // Current and next turn
    }
}