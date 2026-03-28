package entity;

public class Goblin extends Enemy{
    public Goblin(String name){
        super(name, 55, 35, 15, 25);
    }

    @Override
    public void enemyAI(Combatant target){
        basicAttack(target);
    }
}
