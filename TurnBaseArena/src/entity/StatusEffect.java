package entity;

public abstract class StatusEffect {
    protected int duration;

    public StatusEffect(int duration){ this.duration = duration; }


    // BattleManager calls the following 
    public abstract void apply(Combatant target); // Apply effects
    public abstract void expire(Combatant target); // Clean up effects when duration drops to 0
    public void update(Combatant target) { // Updates duration, when it hits 0, the effect expires
        duration--; 
        if (duration <= 0){
            expire(target);
        }
    }

    public boolean isExpired(){ return duration <= 0;}
    public int getRemainingDuration() { return duration;}
    public void setDuration(int duration){ this.duration = duration;}
    public abstract String getName();
}
