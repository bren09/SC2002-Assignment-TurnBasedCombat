import entity.*;
import java.util.ArrayList;
import java.util.List;

public class EntityTest {

    // ===================== TEST HELPER =====================
    static int passed = 0;
    static int failed = 0;

    static void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            passed++;
        } else {
            System.out.println("  [FAIL] " + testName);
            failed++;
        }
    }

    static void section(String title) {
        System.out.println("\n========== " + title + " ==========");
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {

        testCombatantStats();
        testBasicAttack();
        testTakeDamageDefenseReduction();
        testHeal();
        testIsAlive();
        testStunEffect();
        testDefendEffect();
        testInvulEffect();
        testUpdateEffects();
        testDuplicateEffectOverwrite();
        testWarriorShieldBash();
        testWizardArcaneBlast();
        testWizardAttackReset();
        testPotion();
        testSmokeBomb();
        testPowerStoneWarrior();
        testPowerStoneWizard();
        testInventory();
        testSkillCooldown();
        testEnemyAI();

        // ===================== SUMMARY =====================
        System.out.println("\n========================================");
        System.out.println("  RESULTS: " + passed + " passed, " + failed + " failed.");
        System.out.println("========================================");
    }

    // ===================== TESTS =====================

    static void testCombatantStats() {
        section("Combatant Stats Initialisation");
        Warrior w = new Warrior("TestWarrior");
        check("Warrior name",           w.getName().equals("TestWarrior"));
        check("Warrior HP",             w.getHp() == 260);
        check("Warrior maxHP",          w.getMaxHp() == 260);
        check("Warrior attack",         w.getAttack() == 40);
        check("Warrior defense",        w.getDefense() == 20);
        check("Warrior speed",          w.getSpeed() == 30);
        check("Warrior not stunned",    !w.isStunned());
        check("Warrior is alive",       w.isAlive());
        check("Warrior no effects",     w.getActiveEffects().isEmpty());

        Goblin g = new Goblin("TestGoblin");
        check("Goblin name",            g.getName().equals("TestGoblin"));
        check("Goblin HP",              g.getHp() == 55);
        check("Goblin attack",          g.getAttack() == 35);
        check("Goblin defense",         g.getDefense() == 15);
        check("Goblin speed",           g.getSpeed() == 25);

        Wolf wolf = new Wolf("TestWolf");
        check("Wolf HP",                wolf.getHp() == 40);
        check("Wolf attack",            wolf.getAttack() == 45);
        check("Wolf defense",           wolf.getDefense() == 5);
        check("Wolf speed",             wolf.getSpeed() == 35);

        Wizard wiz = new Wizard("TestWizard");
        check("Wizard HP",              wiz.getHp() == 200);
        check("Wizard attack",          wiz.getAttack() == 50);
        check("Wizard defense",         wiz.getDefense() == 10);
        check("Wizard speed",           wiz.getSpeed() == 20);
    }

    static void testBasicAttack() {
        section("Basic Attack");
        Warrior warrior = new Warrior("Warrior");
        Goblin goblin = new Goblin("Goblin");

        // Warrior ATK 40 vs Goblin DEF 15 = 25 damage, Goblin HP: 55 -> 30
        warrior.basicAttack(goblin);
        check("Warrior basic attack damage (40-15=25, HP 55->30)", goblin.getHp() == 30);

        // Goblin ATK 35 vs Warrior DEF 20 = 15 damage, Warrior HP: 260 -> 245
        goblin.basicAttack(warrior);
        check("Goblin basic attack damage (35-20=15, HP 260->245)", warrior.getHp() == 245);

        // Wolf ATK 45 vs Warrior DEF 20 = 25 damage
        Wolf wolf = new Wolf("Wolf");
        wolf.basicAttack(warrior);
        check("Wolf basic attack damage (45-20=25, HP 245->220)", warrior.getHp() == 220);
    }

    static void testTakeDamageDefenseReduction() {
        section("Take Damage - Defense Reduction");
        Warrior warrior = new Warrior("Warrior");

        // Raw damage less than defense = 0 damage
        warrior.takeDamage(10); // 10 - 20 defense = 0
        check("Damage cannot go below 0 (raw 10 vs def 20)", warrior.getHp() == 260);

        // Normal damage
        warrior.takeDamage(30); // 30 - 20 = 10 damage
        check("Normal damage applied correctly (30-20=10)", warrior.getHp() == 250);

        // Lethal damage clamps HP to 0
        warrior.takeDamage(9999);
        check("HP clamped to 0 after lethal damage", warrior.getHp() == 0);
        check("Warrior is dead after lethal damage", !warrior.isAlive());
    }

    static void testHeal() {
        section("Heal");
        Warrior warrior = new Warrior("Warrior");

        // Heal capped at maxHP
        warrior.takeDamage(60); // 60-20=40 damage, HP: 260->220
        warrior.heal(100);
        check("Heal capped at maxHP (220+100 capped at 260)", warrior.getHp() == 260);

        // Partial heal
        warrior.takeDamage(120); // 120-20=100 damage, HP: 260->160
        warrior.heal(50);
        check("Partial heal applied correctly (160+50=210)", warrior.getHp() == 210);

        // Heal of 0 does nothing
        warrior.heal(0);
        check("Heal of 0 does not change HP", warrior.getHp() == 210);
    }

    static void testIsAlive() {
        section("IsAlive");
        Goblin goblin = new Goblin("Goblin");
        check("Goblin starts alive", goblin.isAlive());

        goblin.takeDamage(9999);
        check("Goblin dead after lethal damage", !goblin.isAlive());
        check("Goblin HP does not go below 0", goblin.getHp() == 0);
    }

    static void testStunEffect() {
        section("StunEffect");
        Goblin goblin = new Goblin("Goblin");

        // Apply 2-turn stun (has not acted this round)
        goblin.addEffect(new StunEffect(2));
        check("Goblin is stunned after StunEffect(2)", goblin.isStunned());
        check("StunEffect in active effects", !goblin.getActiveEffects().isEmpty());
        check("StunEffect duration is 2", goblin.getActiveEffects().get(0).getRemainingDuration() == 2);

        // Tick once — still stunned
        goblin.updateEffects();
        check("Goblin still stunned after 1 updateEffects", goblin.isStunned());
        check("StunEffect duration is 1 after 1 tick", goblin.getActiveEffects().get(0).getRemainingDuration() == 1);

        // Tick again — stun expires
        goblin.updateEffects();
        check("Goblin no longer stunned after 2 updateEffects", !goblin.isStunned());
        check("StunEffect removed from active effects", goblin.getActiveEffects().isEmpty());

        // Apply 1-turn stun (has already acted this round)
        Goblin goblin2 = new Goblin("Goblin2");
        goblin2.addEffect(new StunEffect(1));
        check("Goblin2 stunned after StunEffect(1)", goblin2.isStunned());

        goblin2.updateEffects();
        check("Goblin2 no longer stunned after 1 updateEffects", !goblin2.isStunned());
    }

    static void testDefendEffect() {
        section("DefendEffect");
        Warrior warrior = new Warrior("Warrior");

        // Base defense = 20, after defend = 30
        warrior.addEffect(new DefendEffect());
        check("Warrior is defending after DefendEffect", warrior.isDefending());
        check("Defense increased to 30 after DefendEffect", warrior.getDefense() == 30);

        // With +10 defense, 35 attack - 30 defense = 5 damage
        warrior.takeDamage(35);
        check("Defend reduces damage correctly (35-30=5)", warrior.getHp() == 255);

        // Tick once — still defending (lasts 2 rounds)
        warrior.updateEffects();
        check("Still defending after 1 updateEffects", warrior.isDefending());
        check("Defense still 30 after 1 tick", warrior.getDefense() == 30);

        // Tick again — defend expires
        warrior.updateEffects();
        check("No longer defending after 2 updateEffects", !warrior.isDefending());
        check("Defense returned to base 20 after expiry", warrior.getDefense() == 20);

        // Damage now uses base defense again
        warrior.takeDamage(35); // 35 - 20 = 15 damage
        check("Damage uses base defense after defend expires (35-20=15)", warrior.getHp() == 240);
    }

    static void testInvulEffect() {
        section("InvulEffect");
        Warrior warrior = new Warrior("Warrior");

        warrior.addEffect(new InvulEffect(2));
        check("Warrior is invulnerable after InvulEffect(2)", warrior.isInvul());

        // Should take no damage while invulnerable
        warrior.takeDamage(999);
        check("Warrior takes no damage while invulnerable", warrior.getHp() == 260);

        // Tick once — still invulnerable
        warrior.updateEffects();
        check("Still invulnerable after 1 updateEffects", warrior.isInvul());

        // Tick again — invul expires
        warrior.updateEffects();
        check("No longer invulnerable after 2 updateEffects", !warrior.isInvul());

        // Should take damage now
        warrior.takeDamage(40); // 40 - 20 = 20 damage
        check("Warrior takes damage after invulnerability expires (40-20=20)", warrior.getHp() == 240);
    }

    static void testUpdateEffects() {
        section("UpdateEffects - Multiple Active Effects");
        Warrior warrior = new Warrior("Warrior");

        // Apply both stun and invul simultaneously
        warrior.addEffect(new StunEffect(1));
        warrior.addEffect(new InvulEffect(2));
        check("Warrior has 2 active effects", warrior.getActiveEffects().size() == 2);
        check("Warrior is stunned", warrior.isStunned());
        check("Warrior is invulnerable", warrior.isInvul());

        // After 1 tick — stun expires, invul remains
        warrior.updateEffects();
        check("Stun expired after 1 tick", !warrior.isStunned());
        check("Invul still active after 1 tick", warrior.isInvul());
        check("Only 1 active effect remaining", warrior.getActiveEffects().size() == 1);

        // After 2nd tick — invul expires
        warrior.updateEffects();
        check("Invul expired after 2 ticks", !warrior.isInvul());
        check("No active effects remaining", warrior.getActiveEffects().isEmpty());
    }

    static void testDuplicateEffectOverwrite() {
        section("Duplicate Effect Overwrite");
        Goblin goblin = new Goblin("Goblin");

        // Apply stun, then apply again — should overwrite not stack
        goblin.addEffect(new StunEffect(1));
        check("Goblin stunned with duration 1", goblin.getActiveEffects().get(0).getRemainingDuration() == 1);

        goblin.addEffect(new StunEffect(2));
        check("Only 1 stun effect after duplicate add", goblin.getActiveEffects().size() == 1);
        check("Stun duration overwritten to 2", goblin.getActiveEffects().get(0).getRemainingDuration() == 2);
    }

    static void testWarriorShieldBash() {
        section("Warrior - Shield Bash");
        Warrior warrior = new Warrior("Warrior");
        Goblin goblin = new Goblin("Goblin");

        // Shield Bash deals basicAttack damage (40-15=25) and stuns
        warrior.useSkill(List.of(goblin));
        check("Shield Bash deals correct damage (40-15=25, HP 55->30)", goblin.getHp() == 30);
        check("Shield Bash stuns the target", goblin.isStunned());
        check("StunEffect in goblin active effects", !goblin.getActiveEffects().isEmpty());
        check("Skill cooldown starts after use", warrior.getSkillCooldown() == 3);

        // Skill should be on cooldown — no additional damage
        int hpBeforeBlockedSkill = goblin.getHp();
        warrior.useSkill(List.of(goblin));
        check("Skill blocked when on cooldown", goblin.getHp() == hpBeforeBlockedSkill);

        // Reduce cooldown 3 times
        warrior.reduceCooldown();
        warrior.reduceCooldown();
        warrior.reduceCooldown();
        check("Skill ready after 3 reduceCooldown calls", warrior.isSkillReady());
    }

    static void testWizardArcaneBlast() {
        section("Wizard - Arcane Blast");
        Wizard wizard = new Wizard("Wizard");
        Goblin goblin = new Goblin("Goblin"); // HP 55, DEF 15 -> takes 50-15=35 dmg -> HP 20
        Wolf wolf = new Wolf("Wolf");         // HP 40, DEF 5  -> takes 50-5=45 dmg  -> HP 0 (killed)

        List<Combatant> targets = new ArrayList<>();
        targets.add(goblin);
        targets.add(wolf);

        check("Wizard base attack is 50", wizard.getAttack() == 50);

        wizard.useSkill(targets);

        check("Arcane Blast damages goblin (50-15=35, HP 55->20)", goblin.getHp() == 20);
        check("Arcane Blast kills wolf (50-5=45, HP 40->0)", !wolf.isAlive());
        check("Wizard ATK buffed by 10 for killing wolf (50->60)", wizard.getAttack() == 60);
        check("Arcane Blast cooldown starts", wizard.getSkillCooldown() == 3);
    }

    static void testWizardAttackReset() {
        section("Wizard - Attack Reset");
        Wizard wizard = new Wizard("Wizard");
        Wolf wolf1 = new Wolf("Wolf1"); // HP 40, DEF 5 -> dies to 50-5=45 dmg
        Wolf wolf2 = new Wolf("Wolf2");

        List<Combatant> targets = new ArrayList<>();
        targets.add(wolf1);
        targets.add(wolf2);

        // Both wolves die: ATK 50->60->70
        wizard.useSkill(targets);
        check("Wizard ATK buffed to 70 after 2 kills", wizard.getAttack() == 70);

        // Reset attack back to base 50
        wizard.attackReset();
        check("Wizard attack reset to base 50", wizard.getAttack() == 50);

        // Verify reset by checking damage output
        Goblin goblin = new Goblin("Goblin"); // HP 55, DEF 15
        wizard.basicAttack(goblin); // 50-15=35 damage
        check("Wizard deals base damage after reset (50-15=35, HP 55->20)", goblin.getHp() == 20);
    }

    static void testPotion() {
        section("Item - Potion");
        Warrior warrior = new Warrior("Warrior");
        warrior.addItem(new Potion());

        warrior.takeDamage(120); // 120-20=100 damage, HP: 260->160
        check("Warrior HP reduced before potion use", warrior.getHp() == 160);

        warrior.useItem(0, List.of(warrior));
        check("Potion heals 100 HP (160->260)", warrior.getHp() == 260);
        check("Potion removed from inventory after use", !warrior.hasItems());

        // Potion should cap at maxHP
        warrior.takeDamage(40); // 40-20=20 damage, HP: 260->240
        warrior.addItem(new Potion());
        warrior.useItem(0, List.of(warrior));
        check("Potion heal capped at maxHP (240+100 capped at 260)", warrior.getHp() == 260);
    }

    static void testSmokeBomb() {
        section("Item - Smoke Bomb");
        Warrior warrior = new Warrior("Warrior");
        warrior.addItem(new SmokeBomb());

        warrior.useItem(0, new ArrayList<>());
        check("Smoke Bomb applies InvulEffect", warrior.isInvul());
        check("Smoke Bomb removed from inventory after use", !warrior.hasItems());

        // Should take no damage while invulnerable
        warrior.takeDamage(999);
        check("Warrior takes no damage while smoke bomb active", warrior.getHp() == 260);

        // Tick twice — invul expires
        warrior.updateEffects();
        warrior.updateEffects();
        check("Invulnerability expires after 2 ticks", !warrior.isInvul());

        // Should take damage now
        warrior.takeDamage(40); // 40-20=20 damage
        check("Warrior takes damage after smoke bomb expires (40-20=20)", warrior.getHp() == 240);
    }

    static void testPowerStoneWarrior() {
        section("Item - Power Stone (Warrior)");
        Warrior warrior = new Warrior("Warrior");
        Goblin goblin = new Goblin("Goblin");
        warrior.addItem(new PowerStone());

        // Use skill first to put it on cooldown
        warrior.useSkill(List.of(goblin)); // Goblin HP: 55->30, cooldown=3
        check("Shield Bash deals damage on first use", goblin.getHp() == 30);
        check("Skill on cooldown after use", warrior.getSkillCooldown() == 3);

        // Power Stone triggers free skill — cooldown unchanged
        Goblin goblin2 = new Goblin("Goblin2");
        warrior.useItem(0, List.of(goblin2)); // Goblin2 HP: 55->30
        check("Power Stone triggers Shield Bash on Goblin2", goblin2.getHp() == 30);
        check("Goblin2 stunned by Power Stone triggered Shield Bash", goblin2.isStunned());
        check("Cooldown unchanged after Power Stone use (still 3)", warrior.getSkillCooldown() == 3);
        check("Power Stone removed from inventory", !warrior.hasItems());
    }

    static void testPowerStoneWizard() {
        section("Item - Power Stone (Wizard)");
        Wizard wizard = new Wizard("Wizard");
        Wolf wolf1 = new Wolf("Wolf1");
        wizard.addItem(new PowerStone());

        // Use skill first to put it on cooldown
        wizard.useSkill(List.of(wolf1)); // Wolf HP: 40->0, ATK: 50->60, cooldown=3
        check("Arcane Blast kills Wolf1", !wolf1.isAlive());
        check("Wizard ATK buffed to 60 after kill", wizard.getAttack() == 60);
        check("Skill cooldown is 3", wizard.getSkillCooldown() == 3);

        // Power Stone triggers free Arcane Blast — cooldown unchanged
        Wolf wolf2 = new Wolf("Wolf2"); // HP 40, DEF 5 -> takes 60-5=55 dmg -> dies
        wizard.useItem(0, List.of(wolf2));
        check("Power Stone triggers Arcane Blast killing Wolf2", !wolf2.isAlive());
        check("Wizard ATK buffed to 70 after second kill", wizard.getAttack() == 70);
        check("Cooldown unchanged after Power Stone use (still 3)", wizard.getSkillCooldown() == 3);
        check("Power Stone removed from inventory", !wizard.hasItems());
    }

    static void testInventory() {
        section("Inventory Management");
        Warrior warrior = new Warrior("Warrior");

        check("Inventory starts empty", !warrior.hasItems());
        check("Inventory size is 0", warrior.getInventory().size() == 0);

        warrior.addItem(new Potion());
        check("Inventory has items after addItem", warrior.hasItems());
        check("Inventory size is 1", warrior.getInventory().size() == 1);

        warrior.addItem(new SmokeBomb());
        check("Inventory size is 2 after second addItem", warrior.getInventory().size() == 2);

        // Adding a 3rd item should be rejected
        warrior.addItem(new PowerStone());
        check("Inventory capped at 2 items", warrior.getInventory().size() == 2);

        // Invalid index handled gracefully
        int hpBefore = warrior.getHp();
        warrior.useItem(99, new ArrayList<>());
        check("Invalid item index handled gracefully", warrior.getHp() == hpBefore);

        // Empty inventory handled gracefully
        warrior.useItem(0, List.of(warrior)); // use Potion
        warrior.useItem(0, new ArrayList<>()); // use SmokeBomb
        warrior.useItem(0, new ArrayList<>()); // inventory now empty — should not crash
        check("Empty inventory handled gracefully", !warrior.hasItems());
    }

    static void testSkillCooldown() {
        section("Skill Cooldown");
        Warrior warrior = new Warrior("Warrior");
        Goblin goblin = new Goblin("Goblin");

        check("Skill ready at start", warrior.isSkillReady());
        check("Cooldown is 0 at start", warrior.getSkillCooldown() == 0);

        warrior.useSkill(List.of(goblin));
        check("Cooldown is 3 after skill use", warrior.getSkillCooldown() == 3);
        check("Skill not ready when on cooldown", !warrior.isSkillReady());

        warrior.reduceCooldown();
        check("Cooldown is 2 after 1 reduceCooldown", warrior.getSkillCooldown() == 2);

        warrior.reduceCooldown();
        check("Cooldown is 1 after 2 reduceCooldowns", warrior.getSkillCooldown() == 1);

        warrior.reduceCooldown();
        check("Cooldown is 0 after 3 reduceCooldowns", warrior.getSkillCooldown() == 0);
        check("Skill ready again after cooldown expires", warrior.isSkillReady());

        // Reduce cooldown when already 0 — should not go negative
        warrior.reduceCooldown();
        check("Cooldown does not go below 0", warrior.getSkillCooldown() == 0);
    }

    static void testEnemyAI() {
        section("Enemy AI");
        Goblin goblin = new Goblin("Goblin");
        Wolf wolf = new Wolf("Wolf");
        Warrior warrior = new Warrior("Warrior");

        // Goblin ATK 35 vs Warrior DEF 20 = 15 damage
        goblin.enemyAI(warrior);
        check("Goblin enemyAI deals correct damage (35-20=15, HP 260->245)", warrior.getHp() == 245);

        // Wolf ATK 45 vs Warrior DEF 20 = 25 damage
        wolf.enemyAI(warrior);
        check("Wolf enemyAI deals correct damage (45-20=25, HP 245->220)", warrior.getHp() == 220);

        // Enemies should not damage invulnerable targets
        warrior.addEffect(new InvulEffect(1));
        goblin.enemyAI(warrior);
        check("Enemy deals no damage to invulnerable warrior", warrior.getHp() == 220);
    }
}