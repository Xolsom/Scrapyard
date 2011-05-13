package net.thedarktide.xolsom.scrapyard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * @author Xolsom
 */
public class ScrapyardYardSaleEvent extends Event {
    private Player player;
    
    public ScrapyardYardSaleEvent(final String type, Player player) {
        super(type);
        
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
}
