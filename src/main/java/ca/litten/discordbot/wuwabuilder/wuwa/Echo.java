package ca.litten.discordbot.wuwabuilder.wuwa;

import java.util.*;

public class Echo {
    public final Map<Stat, Float> subStats;
    public Stat mainStat;
    public float mainStatMagnitude;
    public Stat secondStat;
    public float secondStatMagnitude;
    public long echoID;
    public long sonataID;
    
    public Echo(long echoID, long sonataID, Stat mainStat, float mainStatMagnitude, Stat secondStat, float secondStatMagnitude, Map<Stat, Float> subStats) {
        this.echoID = echoID;
        this.sonataID = sonataID;
        this.mainStat = mainStat;
        this.mainStatMagnitude = mainStatMagnitude;
        this.secondStat = secondStat;
        this.secondStatMagnitude = secondStatMagnitude;
        this.subStats = subStats;
    }
}
