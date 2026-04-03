package control;

import entity.*;
import java.util.List;
import java.util.Set;


public class ActionMngr{
    // main method used by the Battle Engine when player takes a turn
    public void executeAction(ActionType type, Player player, List<Combatant> targets, int itemIndex, Set<Combatant> actedThisRound){
        if (player==null || !player.isAlive()) return;

        switch(type){
            case BASIC_ATTACK -> player.basicAttack(targets.get(0));
            case DEFEND -> player.addEffect(new DefendEffect());
            case SPECIAL_SKILL -> executeSkill(player, targets, actedThisRound);
            case ITEM -> player.useItem(itemIndex, targets);
        }
    }

    private void executeSkill(Player player, List<Combatant> targets, Set<Combatant> actedThisRound){
        player.useSkill(targets);
        if (player instanceof Warrior){
            Combatant target = targets.get(0);
            if (actedThisRound.contains(target)){
                target.getActiveEffects().stream()
                .filter(e -> e instanceof StunEffect)
                .forEach(e -> ((StunEffect) e).setDuration(1));
            }
        }
    }

    public void executeEnemyMove(Enemy enemy, List<Player> party){
        if (enemy.isStunned() || !enemy.isAlive()) return;

        Player target = party.get((int) (Math.random()*party.size()));
        if (target.isAlive()){ enemy.enemyAI(target); }
    }
}
