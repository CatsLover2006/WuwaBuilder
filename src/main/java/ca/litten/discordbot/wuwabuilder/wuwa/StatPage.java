package ca.litten.discordbot.wuwabuilder.wuwa;

public class StatPage {
    public final float HP, ATK, DEF, energyRegen,
            critRate, critDMG, healingBonus,
            glacioBonus, fusionBonus, electroBonus,
            aeroBonus, spectroBonus, havocBonus,
            basicBonus, heavyBonus, skillBonus, ultBonus;
    
    private StatPage(float hp, float atk, float def, float energyRegen, float critRate, float critDMG, float healingBonus, float glacioBonus, float fusionBonus, float electroBonus, float aeroBonus, float spectroBonus, float havocBonus, float basicBonus, float heavyBonus, float skillBonus, float ultBonus) {
        HP = hp;
        ATK = atk;
        DEF = def;
        this.energyRegen = energyRegen;
        this.critRate = critRate;
        this.critDMG = critDMG;
        this.healingBonus = healingBonus;
        this.glacioBonus = glacioBonus;
        this.fusionBonus = fusionBonus;
        this.electroBonus = electroBonus;
        this.aeroBonus = aeroBonus;
        this.spectroBonus = spectroBonus;
        this.havocBonus = havocBonus;
        this.basicBonus = basicBonus;
        this.heavyBonus = heavyBonus;
        this.skillBonus = skillBonus;
        this.ultBonus = ultBonus;
    }
    
    public static StatPage calculateStats(Build build) {
        float totalHPpercent = 0, totalATKpercent = 0, totalDEFpercent = 0,
                totalER = 0, totalCR = 0, totalCD = 0, totalHeB = 0, totalGlB = 0,
                totalFuB = 0, totalElB = 0, totalAeB = 0, totalSpB = 0, totalHaB = 0,
                totalBaB = 0, totalHhB = 0, totalSkB = 0, totalUlB = 0, totalFlatHP = 0,
                totalFlatATK = 0, totalFlatDEF = 0, baseATK, baseHP, baseDEF;
        baseATK = build.character.getAtkForLevel(build.characterLevel);
        baseHP = build.character.getHpForLevel(build.characterLevel);
        baseDEF = build.character.getDefForLevel(build.characterLevel);
        switch (build.weapon.getMainStat()) {
            case flatDEF:
                baseDEF += build.weapon.getMainStatForLevel(build.weaponLevel);
                break;
            case flatHP:
                baseHP += build.weapon.getMainStatForLevel(build.weaponLevel);
                break;
            default:
                baseATK += build.weapon.getMainStatForLevel(build.weaponLevel);
                break;
        }
        float statToAdd = build.weapon.getSubStatForLevel(build.weaponLevel);
        switch (build.weapon.getSubStat()) {
            case flatDEF:
                totalFlatDEF += statToAdd;
                break;
            case flatHP:
                totalFlatHP += statToAdd;
                break;
            case flatATK:
                totalFlatATK += statToAdd;
                break;
            case percentDEF:
                totalDEFpercent += statToAdd;
                break;
            case percentHP:
                totalHPpercent += statToAdd;
                break;
            case percentATK:
                totalATKpercent += statToAdd;
                break;
            case critDMG:
                totalCD += statToAdd;
                break;
            case critRate:
                totalCR += statToAdd;
                break;
            case energyRegen:
                totalER += statToAdd;
                break;
            case skillBonus:
                totalSkB += statToAdd;
                break;
            case ultBonus:
                totalUlB += statToAdd;
                break;
            case basicBonus:
                totalBaB += statToAdd;
                break;
            case havocBonus:
                totalHaB += statToAdd;
                break;
            case heavyBonus:
                totalHhB += statToAdd;
                break;
            case spectroBonus:
                totalSpB += statToAdd;
                break;
            case aeroBonus:
                totalAeB += statToAdd;
                break;
            case fusionBonus:
                totalFuB += statToAdd;
                break;
            case glacioBonus:
                totalGlB += statToAdd;
                break;
            case electroBonus:
                totalElB += statToAdd;
                break;
            case healingBonus:
                totalHeB += statToAdd;
                break;
        }
        for (Echo echo : build.echoes) {
            statToAdd = echo.mainStatMagnitude;
            switch (echo.mainStat) {
                case flatDEF:
                    totalFlatDEF += statToAdd;
                    break;
                case flatHP:
                    totalFlatHP += statToAdd;
                    break;
                case flatATK:
                    totalFlatATK += statToAdd;
                    break;
                case percentDEF:
                    totalDEFpercent += statToAdd;
                    break;
                case percentHP:
                    totalHPpercent += statToAdd;
                    break;
                case percentATK:
                    totalATKpercent += statToAdd;
                    break;
                case critDMG:
                    totalCD += statToAdd;
                    break;
                case critRate:
                    totalCR += statToAdd;
                    break;
                case energyRegen:
                    totalER += statToAdd;
                    break;
                case skillBonus:
                    totalSkB += statToAdd;
                    break;
                case ultBonus:
                    totalUlB += statToAdd;
                    break;
                case basicBonus:
                    totalBaB += statToAdd;
                    break;
                case havocBonus:
                    totalHaB += statToAdd;
                    break;
                case heavyBonus:
                    totalHhB += statToAdd;
                    break;
                case spectroBonus:
                    totalSpB += statToAdd;
                    break;
                case aeroBonus:
                    totalAeB += statToAdd;
                    break;
                case fusionBonus:
                    totalFuB += statToAdd;
                    break;
                case glacioBonus:
                    totalGlB += statToAdd;
                    break;
                case electroBonus:
                    totalElB += statToAdd;
                    break;
                case healingBonus:
                    totalHeB += statToAdd;
                    break;
            }
            statToAdd = echo.secondStatMagnitude;
            switch (echo.secondStat) {
                case flatDEF:
                    totalFlatDEF += statToAdd;
                    break;
                case flatHP:
                    totalFlatHP += statToAdd;
                    break;
                case flatATK:
                    totalFlatATK += statToAdd;
                    break;
                case percentDEF:
                    totalDEFpercent += statToAdd;
                    break;
                case percentHP:
                    totalHPpercent += statToAdd;
                    break;
                case percentATK:
                    totalATKpercent += statToAdd;
                    break;
                case critDMG:
                    totalCD += statToAdd;
                    break;
                case critRate:
                    totalCR += statToAdd;
                    break;
                case energyRegen:
                    totalER += statToAdd;
                    break;
                case skillBonus:
                    totalSkB += statToAdd;
                    break;
                case ultBonus:
                    totalUlB += statToAdd;
                    break;
                case basicBonus:
                    totalBaB += statToAdd;
                    break;
                case havocBonus:
                    totalHaB += statToAdd;
                    break;
                case heavyBonus:
                    totalHhB += statToAdd;
                    break;
                case spectroBonus:
                    totalSpB += statToAdd;
                    break;
                case aeroBonus:
                    totalAeB += statToAdd;
                    break;
                case fusionBonus:
                    totalFuB += statToAdd;
                    break;
                case glacioBonus:
                    totalGlB += statToAdd;
                    break;
                case electroBonus:
                    totalElB += statToAdd;
                    break;
                case healingBonus:
                    totalHeB += statToAdd;
                    break;
            }
            for (Stat substat : echo.subStats.keySet()) {
                statToAdd = echo.subStats.get(substat);
                switch (substat) {
                    case flatDEF:
                        totalFlatDEF += statToAdd;
                        break;
                    case flatHP:
                        totalFlatHP += statToAdd;
                        break;
                    case flatATK:
                        totalFlatATK += statToAdd;
                        break;
                    case percentDEF:
                        totalDEFpercent += statToAdd;
                        break;
                    case percentHP:
                        totalHPpercent += statToAdd;
                        break;
                    case percentATK:
                        totalATKpercent += statToAdd;
                        break;
                    case critDMG:
                        totalCD += statToAdd;
                        break;
                    case critRate:
                        totalCR += statToAdd;
                        break;
                    case energyRegen:
                        totalER += statToAdd;
                        break;
                    case skillBonus:
                        totalSkB += statToAdd;
                        break;
                    case ultBonus:
                        totalUlB += statToAdd;
                        break;
                    case basicBonus:
                        totalBaB += statToAdd;
                        break;
                    case havocBonus:
                        totalHaB += statToAdd;
                        break;
                    case heavyBonus:
                        totalHhB += statToAdd;
                        break;
                    case spectroBonus:
                        totalSpB += statToAdd;
                        break;
                    case aeroBonus:
                        totalAeB += statToAdd;
                        break;
                    case fusionBonus:
                        totalFuB += statToAdd;
                        break;
                    case glacioBonus:
                        totalGlB += statToAdd;
                        break;
                    case electroBonus:
                        totalElB += statToAdd;
                        break;
                    case healingBonus:
                        totalHeB += statToAdd;
                        break;
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            if (build.minorSkills[i]) {
                statToAdd = build.character.getStatBuf(i).stat.value;
                switch (build.character.getStatBuf(i).stat.stat) {
                    case flatDEF:
                        totalFlatDEF += statToAdd;
                        break;
                    case flatHP:
                        totalFlatHP += statToAdd;
                        break;
                    case flatATK:
                        totalFlatATK += statToAdd;
                        break;
                    case percentDEF:
                        totalDEFpercent += statToAdd;
                        break;
                    case percentHP:
                        totalHPpercent += statToAdd;
                        break;
                    case percentATK:
                        totalATKpercent += statToAdd;
                        break;
                    case critDMG:
                        totalCD += statToAdd;
                        break;
                    case critRate:
                        totalCR += statToAdd;
                        break;
                    case energyRegen:
                        totalER += statToAdd;
                        break;
                    case skillBonus:
                        totalSkB += statToAdd;
                        break;
                    case ultBonus:
                        totalUlB += statToAdd;
                        break;
                    case basicBonus:
                        totalBaB += statToAdd;
                        break;
                    case havocBonus:
                        totalHaB += statToAdd;
                        break;
                    case heavyBonus:
                        totalHhB += statToAdd;
                        break;
                    case spectroBonus:
                        totalSpB += statToAdd;
                        break;
                    case aeroBonus:
                        totalAeB += statToAdd;
                        break;
                    case fusionBonus:
                        totalFuB += statToAdd;
                        break;
                    case glacioBonus:
                        totalGlB += statToAdd;
                        break;
                    case electroBonus:
                        totalElB += statToAdd;
                        break;
                    case healingBonus:
                        totalHeB += statToAdd;
                        break;
                }
            }
        }
        return new StatPage(baseHP * (1 + totalHPpercent / 100) + totalFlatHP,
                baseATK * (1 + totalATKpercent / 100) + totalFlatATK,
                baseDEF * (1 + totalDEFpercent / 100) + totalFlatDEF,
                100 + totalER, 5 + totalCR, 150 + totalCD,
                totalHeB, totalGlB, totalFuB, totalElB, totalAeB, totalSpB, totalHaB,
                totalBaB, totalHhB, totalSkB, totalUlB);
    }
}
