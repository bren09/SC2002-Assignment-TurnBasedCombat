package entity;

public abstract class Enemy extends Combatant{
    public Enemy (String name, int hp, int attack, int defense, int speed){
        super(name, hp, attack, defense, speed);
    }

    // Enemy AI will be defined in child class, Goblic & Wolf
    public abstract void enemyAI(Combatant target);

    @Override
    public void takeTurn(){
        // // Logic for taking turns will be implemented in Control/Boundary classes
    }
}
