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
    public boolean[] minorSkills;
    
    public Build() {
        echoes = new Echo[5];
        skillLevels = new int[5];
        for (int i = 0; i < 5; i++) skillLevels[i] = 1;
        minorSkills = new boolean[8];
        for (int i = 0; i < 8; i++) minorSkills[i] = false;
    }
}
