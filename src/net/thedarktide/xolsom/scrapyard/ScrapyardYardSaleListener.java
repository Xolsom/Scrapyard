package net.thedarktide.xolsom.scrapyard;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import net.thedarktide.squallseed31.yardsale.YardSaleEvent;
import net.thedarktide.squallseed31.yardsale.YardShop;

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
    @Override
    public void onCustomEvent(Event event) {
        if(!(event instanceof YardSaleEvent)) return;

        YardSaleEvent yardSaleEvent = (YardSaleEvent)event;
        YardShop yardShop = yardSaleEvent.getShop();

        if(yardShop.getAsset().getAssetType() == yardShop.getAsset().getAssetType().EVENT) {
            if(yardShop.getAssetData().split(" ")[0].equalsIgnoreCase("Scrapyard")) {
                if(!this.plugin.startScrap(yardSaleEvent.getPlayer())) yardSaleEvent.setCancelled(true);
            }
        }
    }
}
