package entity;

import java.util.List;

public interface Item {
    String getName();
    void use(Player user, List<Combatant> targets);
    
}
