package ca.litten.discordbot.wuwabuilder.wuwa;

public enum Stat {
    critDMG("Crit. DMG"),
    critRate("Crit. Rate"),
    flatATK("ATK"),
    percentATK("ATK%"),
    flatHP("HP"),
    percentHP("HP%"),
    flatDEF("DEF"),
    percentDEF("DEF%"),
    energyRegen("Energy Regen"),
    healingBonus("Healing Bonus"),
    glacioBonus("Glacio DMG Bonus"),
    fusionBonus("Fusion DMG Bonus"),
    electroBonus("Electro DMG Bonus"),
    aeroBonus("Aero DMG Bonus"),
    spectroBonus("Spectro DMG Bonus"),
    havocBonus("Havoc DMG Bonus"),
    basicBonus("Basic Attack DMG Bonus"),
    heavyBonus("Heavy Attack DMG Bonus"),
    skillBonus("Resonance Skill DMG Bonus"),
    ultBonus("Resonance Liberation DMG Bonus");
    
    public final String string;
    Stat(String s) {
        string = s;
    }
}
