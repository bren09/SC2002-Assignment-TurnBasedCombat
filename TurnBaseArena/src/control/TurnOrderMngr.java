package control;

import entity.Combatant;
import java.util.List;

/**
 * Strategy interface for determining turn order in battle.
 * Implement this interface to plug in a new turn priority rule
 * without modifying BattleManager (Open/Closed Principle).
 */
public interface TurnOrderMngr {
    /**
     * Returns a new list of combatants ordered for this round.
     * Only living combatants need to be considered.
     */
    List<Combatant> determineTurnOrder(List<Combatant> combatants);
}
