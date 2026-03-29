package entity;

public class DefendEffect extends StatusEffect{

    private final int defBonus = 10;

    public DefendEffect(){ super(2); }

    @Override
    public void apply(Combatant target) {
     // Defence Up is a passive, no per turn effect
    }

    @Override
    public void expire(Combatant target) {
        target.adjustDef(-defBonus);
        System.out.println(target.getName() + " is not in defensive stance anymore.");
    }

    @Override
    public String getName(){ return "Defend"; }


}
