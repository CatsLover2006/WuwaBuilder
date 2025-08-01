package ca.litten.discordbot.wuwabuilder.wuwa;

public enum Element {
    Glacio(1),
    Fusion(2),
    Electro(3),
    Aero(4),
    Spectro(5),
    Havoc(6);
    
    public final int index;
    
    Element(int index) {
        this.index = index;
    }
}
