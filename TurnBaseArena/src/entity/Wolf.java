package entity;

public class Wolf extends Enemy{
    public Wolf (String name){
        super(name, 40, 45, 5, 35);
    }

    @Override
    public void enemyAI(Combatant target){
        basicAttack(target);
    }
}