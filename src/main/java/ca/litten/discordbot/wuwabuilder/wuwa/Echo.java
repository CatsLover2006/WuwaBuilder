package ca.litten.discordbot.wuwabuilder.wuwa;

import java.util.*;

public class Echo {
    public final Map<Stat, Float> subStats;
    public final Stat mainStat;
    public final float mainStatMagnitude;
    public final long echoID;
    public final long sonataID;
    
    public Echo(long echoID, long sonataID, Stat mainStat, float mainStatMagnitude, Map<Stat, Float> subStats) {
        this.echoID = echoID;
        this.sonataID = sonataID;
        this.mainStat = mainStat;
        this.mainStatMagnitude = mainStatMagnitude;
        this.subStats = Collections.unmodifiableMap(subStats);
    }
}
