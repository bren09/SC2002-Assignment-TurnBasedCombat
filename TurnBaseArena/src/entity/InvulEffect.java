package entity;

public class InvulEffect extends StatusEffect{

    public InvulEffect(int duration){
        super(duration);
    }

    @Override
    public void apply(Combatant target){
        //Invul is passive, no per turn effect
    }

    @Override
    public void expire(Combatant target){
        System.out.println(target.getName() + " is no longer invulnerable.");
    }

    @Override
    public String getName() { return "Invulnerable"; }

}
