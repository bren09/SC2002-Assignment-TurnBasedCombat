package control;

import entity.Combatant;
import java.util.List;

public interface TurnOrderMngr {
    List<Combatant> determineTurnOrder(List<Combatant> combatants);
}
