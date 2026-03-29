package entity;

public class StunEffect extends StatusEffect {

    public StunEffect(int duration){ super(duration); }

    @Override
    public void apply(Combatant target){ 
        // Stun is a passive, no application per turn  
        }
    
    @Override
    public void expire(Combatant target){
        target.setStunned(false);
        System.out.println(target.getName() + " is not stunned anymore.");
    }

    @Override
    public String getName(){ return "Stun"; }

}
