package ca.litten.discordbot.wuwabuilder.wuwa;

public class Build {
    public Character character;
    public Weapon weapon;
    public Level characterLevel;
    public Level weaponLevel;
    public int chainLength;
    public int weaponRank;
    public Echo[] echoes;
    public int[] skillLevels;
    
    public Build() {
        echoes = new Echo[5];
        skillLevels = new int[5];
    }
}
