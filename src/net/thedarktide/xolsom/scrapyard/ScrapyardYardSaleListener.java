package net.thedarktide.xolsom.scrapyard;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.CustomEventListener;
import org.bukkit.inventory.ItemStack;

/**
 * @author Xolsom
 */
public class ScrapyardYardSaleListener extends CustomEventListener {
    private final Scrapyard plugin;
    
    public ScrapyardYardSaleListener(Scrapyard plugin) {
        this.plugin = plugin;
    }
    
    //public boolean onCustomEvent(ScrapyardYardSaleEvent event) {
    public void onCustomEvent(Event event) {
        ScrapyardYardSaleEvent ySEvent = (ScrapyardYardSaleEvent)event;
        
        if(!this.plugin.scrapping(ySEvent.getPlayer())) {
            // Cancel
        }

    }
}
