package net.thedarktide.xolsom.scrapyard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * This is a temporary class providing a custom event for as long as yardsale is not available to me.
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
    
    public void setCancelled(Boolean cancel) {
        // Nothing here, just a replacement
    }
}
