## Getting Started

SpawnManager — takes over spawnInitialEnemies():

SpawnManager
  + createGoblin(String name) : Goblin
  + createWolf(String name) : Wolf
  + createInitialWave(int difficulty) : List<Enemy>
  + createBackupWave(int difficulty) : List<Enemy>


LevelManager — owns difficulty config and calls SpawnManager:

LevelManager
  + getLevelConfig(int difficulty) : LevelConfig  
  + getInitialEnemies(int difficulty) : List<Enemy>
  + getBackupEnemies(int difficulty) : List<Enemy>
  + hasBackup(int difficulty) : boolean


ActionManager — takes over runPlayerTurn(), executePlayerSkill(), useItemMenu():

ActionManager
  + executeBasicAttack(Player p, Enemy target)
  + executeSkill(Player p, List<Combatant> targets, Set<Combatant> actedThisRound)
  + executeDefend(Player p)
  + executeItem(Player p, int index, List<Combatant> targets)


GameUI — takes over all System.out.println() calls:

GameUI
  + showLoadingScreen()
  + showRoundHeader(int round)
  + showPlayerStatus(Player p)
  + showEnemyStatus(List<Enemy> enemies)
  + showRoundEndStatus(Player p, List<Enemy> enemies, int round)
  + showVictoryScreen(Player p, int rounds)
  + showDefeatScreen(int enemiesRemaining, int rounds)
  + showActionMenu(Player p)
  + showItemMenu(List<Item> items)
  + showEnemyList(List<Enemy> enemies)


InputManager — takes over getIntInput() and all scanner calls:

InputManager
  + getIntInput(int min, int max) : int
  + getPlayerName() : String
  + getClassChoice() : int
  + getItemChoice() : int
  + getDifficultyChoice() : int
  + getActionChoice(Player p) : int
  + getTargetChoice(List<Enemy> alive) : Enemy


BattleManager — keeps only the orchestration:

BattleManager
  - runBattle()
  - runRound()
  - checkBackupSpawn()
  - checkWinLoss() : boolean
