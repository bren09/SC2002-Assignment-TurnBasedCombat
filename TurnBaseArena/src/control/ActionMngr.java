package control;

import java.util.List;

import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;
public class ActionMngr{
    // main method used by the Battle Engine when player takes a turn
    public void executeAction(ActionType actionType, Player player, Combatant target, Item item, List<Combatant> enemies){
        // if enemy is dead, do nothing
        if (player == null || !player.isAlive()){
            return;
        }
        //a swutch is used to determine what to do based on the players selected action
        switch(ActionType){
            case BASIC_ATTACK:
                basicAttack(player, target);
                break;

            case DEFEND:
                defend(player);
                break;

            case ITEM:
                useItem(player, item);
                break;

            case SPECIAL_SKILLS:
                useSpecialSkills(player, target, enemies, true);
                break;

            // if it is an invalid action type, throw an exception
            default:
                throw new IllegalArgumentException("Invalid player action.");

            

        }
    }
    //Enemies can only perform basic attacks
    public void executeEnemyAction(Enemy enemy, Player player){
        // this checks if both the enemy and player are alive before the enemy can attack
        if(enemy == null || !player.isAlive()){
            return;
        }
        if(enemy.isAlive() || player.isAlive()){
            return;
        }
        basicAttack(enemy, player);
    }

    // basic attacks 
    public void basicAttack(Combatant attacker, Combatant target){
        if(attacker == null || target == null){
        return;
        } 
        if(!attacker.isAlive || !target.isAlive){
            return;
        }  
        
        int damage = Math.max(0, attacker.getAttack() - target.getDefence());
        target.takeDamage(damage);
        
        System.out.println(attacker.getName() + " used basic attack on " + target.getName() + "for" + damage + "damage.");

    }

    // Defend increases defence by 10 for this round and next round
    // so i make a defendeffect for which lasts 2 rounds
    public void defend(Combatant combatant){
        if(combatant == null || !combatant.isAlive){
            return;
        }
        combatant.addStatusEffect(new DefendEffect(2, 10));
        System.out.println(combatant.getName() + " used Defend and gained 10 defence ");

    }
    //used an item
    public void useItem(Player player, Item item,  Combatant target, List<Combatant> enemies){
        if(player == null || !player.isAlive){
            return;
        }
        if(item == null){
            throw new IllegalArgumentException("No item selected");
        }
        if(!player.hasItem(item)){
            throw new IllegalArgumentException("Player does not have this item");

        }
        //let the items applay their own effects
        item.use(player, target, enemies, this);
        //remove items after use because they are single used
        player.removeItem(item);
        System.out.println(player.gertName() + "used item: " + item.getName());


     }
     
     //normal special skill usage
     public void useSpecialSkill(Player player, Combatant combatant, List<Combatant> enemies, boolean startCooldown){
        if(player == null || !player.isAlive()){
            return;
        }

        //only block usage when this is a normal special skill actioon
        if(startCooldown && player.getSpecialSkillCooldown() > 0){
            throw new IllegalArgumentException("Special skill is on cooldown for " + player.getSpecialSkillCooldown() + "more turn(s). ");

        }
        //let the player class decide what its own special skill does
        //ie warrior ->shield bash
        //Wizars -> arcane blast
        player.useSpecialSkill(this, target, enemies);
        //normal special skill  cooldown 3 turns
        //power stone use should not start or chnage cooldown
        if(startCooldown){
            player.setSpecialSkillCooldown(3);

        }
     }
     //  called ath the end of the combatant turn
     public void updateCooldownAfterTurn(Combatant combatant){
        if(combatant == null){
            return;
        }
        if(combatant instanceof Player){
            Player player = (player)combatant;
            if (player.getSpecialSkillCooldown() > 0){
                player.getSpecialSkillCooldown(getSpecialSkillCooldown() -1);

            }
        }
     }
     //helper method  for warriros shieldbash
     public void applyShieldBash(Player warrior, Combatant target){
        if(warrior == null || target == null || !target.isAlive() || !warrior.isAlive()){
            return;
        }
     

        int damage = Math.max(0, warrior.getAttack() - target.getDefence());
        target.takeDamage(damage);
        target.addStatusEffect(new StunEffect(2));

        System.out.println(warrior.getName() + " used Shield Bash on " + target.getName() + " for " + damage +" damaged and stunned the target. ");
    }

    //helper method for wizards arcane blast
    public void applyArcaneBlast(Player wizard, List<Combatant> enemies){
        if(wizard == null || enemies == null || !wizard.isAlive()){
            return;
        }

        for(combatant enemy: enemies){
            if(enemy == null || !enemy.isAlive()){
                continue;
            }
            int damage = Math.max(0, wizard.getAttack() - enemy.getDefence());
            boolean wasAliveBefore = enemy.isAlive();
            enemy.takeDamage(damage);
            System.out.println(wizard.getName() + " hit " +enemy.getName() + " with Arcane Blast for " + damage + " damage. ");

            //if enemy dies from this attack, wizard gets 10 attack
            if(wasAliveBefore && !enemy.isAlive()){
                wizard.increaseAttack(10);
                System.out.println(wizard.getName() + " gained 10 attack from Arcane Blast ")
            }
        }
    }



}

