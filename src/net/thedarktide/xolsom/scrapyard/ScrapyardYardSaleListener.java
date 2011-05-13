package net.thedarktide.xolsom.scrapyard;

import org.bukkit.event.Event;
import org.bukkit.event.CustomEventListener;

/**
 * Listener for the yardsale plugin event.
 * @author Xolsom
 */
public class ScrapyardYardSaleListener extends CustomEventListener {
    private final Scrapyard plugin;
    
    public ScrapyardYardSaleListener(Scrapyard plugin) {
        this.plugin = plugin;
    }
    
    /**
     * A custom event it checks for the YardSaleEvent event and starts scrapping.
     * Also it does cancel the event, if the scrapping fails.
     * @param event 
     */
    public void onCustomEvent(Event event) {
        ScrapyardYardSaleEvent ySEvent = (ScrapyardYardSaleEvent)event;
        
        if(!this.plugin.startScrap(ySEvent.getPlayer())) {
            ySEvent.setCancelled(true);
        }
        
        // Scrap successful
    }
}
