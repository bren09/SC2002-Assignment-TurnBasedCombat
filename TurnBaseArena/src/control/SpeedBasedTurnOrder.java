package control;

import entity.Combatant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpeedBasedTurnOrder implements TurnOrderMngr {

    @Override
    public List<Combatant> determineTurnOrder(List<Combatant> combatants) {
        List<Combatant> ordered = new ArrayList<>(combatants);
        ordered.sort(Comparator.comparingInt(Combatant::getSpeed).reversed());
        return ordered;
    }
}
